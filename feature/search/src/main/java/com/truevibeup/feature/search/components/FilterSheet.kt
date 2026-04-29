package com.truevibeup.feature.search.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truevibeup.core.common.model.Country
import com.truevibeup.core.ui.theme.Divider
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.Surface
import com.truevibeup.core.ui.theme.SurfaceVariant2
import com.truevibeup.core.ui.theme.TextSecondary
import com.truevibeup.feature.search.R
import com.truevibeup.feature.search.viewmodel.SearchFilter

enum class FilterStep {
    MENU, COUNTRY, AGE_MIN, AGE_MAX, GENDER, LOOKING_FOR
}

@Composable
@Preview
fun FilterSheet(
    initialFilter: SearchFilter = SearchFilter(),
    countries: List<Country> = emptyList(),
    onApply: (SearchFilter) -> Unit = {},
) {
    var localFilter by remember { mutableStateOf(initialFilter) }
    var step by remember { mutableStateOf(FilterStep.MENU) }

    AnimatedContent(
        targetState = step,
        transitionSpec = {
            if (targetState == FilterStep.MENU) {
                fadeIn() togetherWith fadeOut()
            } else {
                slideInVertically { it } + fadeIn() togetherWith
                        slideOutVertically { -it / 2 } + fadeOut()
            }
        },
        label = "filter_step_animation"
    ) { target ->
        when (target) {
            FilterStep.MENU -> FilterMenu(
                filters = localFilter,
                onApply = { onApply(localFilter) },
                onClick = { step = it },
                onToggleOnline = { localFilter = localFilter.copy(online = !localFilter.online) },
                onToggleFollowing = {
                    localFilter = localFilter.copy(following = !localFilter.following)
                }
            )

            FilterStep.COUNTRY -> CountrySection(
                selected = localFilter.country,
                countries = countries,
                onSelect = {
                    localFilter = localFilter.copy(country = it)
                    step = FilterStep.MENU
                },
                onBack = { step = FilterStep.MENU }
            )

            FilterStep.GENDER -> GenderSection(
                selected = localFilter.gender,
                onSelect = {
                    localFilter = localFilter.copy(gender = it)
                    step = FilterStep.MENU
                },
                onBack = { step = FilterStep.MENU }
            )

            FilterStep.AGE_MIN -> AgePicker(
                title = stringResource(R.string.title_min_age),
                value = localFilter.ageMin ?: AGE_RANGE.first,
                onApply = { min ->
                    val max = localFilter.ageMax ?: AGE_RANGE.last
                    localFilter = localFilter.copy(
                        ageMin = min,
                        ageMax = if (min > max) min else max
                    )
                    step = FilterStep.MENU
                },
                onBack = { step = FilterStep.MENU }
            )

            FilterStep.AGE_MAX -> AgePicker(
                title = stringResource(R.string.title_max_age),
                value = localFilter.ageMax ?: AGE_RANGE.last,
                onApply = { max ->
                    val min = localFilter.ageMin ?: AGE_RANGE.first
                    localFilter = localFilter.copy(
                        ageMax = max,
                        ageMin = if (max < min) max else min
                    )
                    step = FilterStep.MENU
                },
                onBack = { step = FilterStep.MENU }
            )

            FilterStep.LOOKING_FOR -> LookingForSection(
                selected = localFilter.lookingFor,
                onSelect = {
                    localFilter = localFilter.copy(lookingFor = it)
                    step = FilterStep.MENU
                },
                onBack = { step = FilterStep.MENU }
            )
        }
    }
}

@Composable
fun FilterMenu(
    filters: SearchFilter,
    onApply: () -> Unit = {},
    onClick: (FilterStep) -> Unit = {},
    onToggleOnline: () -> Unit = {},
    onToggleFollowing: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        FilterItem(titleRes = R.string.title_country) {
            FilterValueText(filters.country) { onClick(FilterStep.COUNTRY) }
        }
        FilterItem(titleRes = R.string.title_age) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterValueText(filters.ageMin?.toString()) { onClick(FilterStep.AGE_MIN) }
                Text(":", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                FilterValueText(filters.ageMax?.toString()) { onClick(FilterStep.AGE_MAX) }
            }
        }
        FilterItem(titleRes = R.string.title_gender) {
            FilterValueText(filters.gender) { onClick(FilterStep.GENDER) }
        }
        FilterItem(titleRes = R.string.title_looking) {
            FilterValueText(filters.lookingFor) { onClick(FilterStep.LOOKING_FOR) }
        }
        FilterItem(titleRes = R.string.title_online) {
            FilterSwitch(checked = filters.online, onCheckedChange = { onToggleOnline() })
        }
        FilterItem(titleRes = R.string.title_follow) {
            FilterSwitch(checked = filters.following, onCheckedChange = { onToggleFollowing() })
        }

        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text(
                text = "Apply",
                color = Surface,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun FilterItem(titleRes: Int, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .height(46.dp)
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )
        content()
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Divider)
}

@Composable
fun FilterValueText(value: String?, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceVariant2,
            disabledContentColor = SurfaceVariant2
        )
    ) {
        Text(
            text = value ?: "Any",
            style = MaterialTheme.typography.bodyMedium,
            color = if (value != null) MaterialTheme.colorScheme.onSurface else TextSecondary
        )
    }
}

@Composable
private fun FilterSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedTrackColor = Primary,
            uncheckedTrackColor = SurfaceVariant2,
            checkedThumbColor = Surface,
            uncheckedThumbColor = TextSecondary,
            checkedBorderColor = Primary,
            uncheckedBorderColor = TextSecondary,
        )
    )
}
