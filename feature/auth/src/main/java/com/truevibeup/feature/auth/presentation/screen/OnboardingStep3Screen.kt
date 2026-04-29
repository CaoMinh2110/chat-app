package com.truevibeup.feature.auth.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FaceRetouchingNatural
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.truevibeup.core.common.AppConstants.MAX_AGE
import com.truevibeup.core.common.AppConstants.MIN_AGE
import com.truevibeup.core.common.Options
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.Surface
import com.truevibeup.core.ui.theme.TextSecondary
import com.truevibeup.feature.auth.R
import com.truevibeup.feature.auth.presentation.component.ContinueButton
import com.truevibeup.feature.auth.presentation.component.FieldLabel
import com.truevibeup.feature.auth.presentation.component.StepHeader
import java.util.Locale.getDefault

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StepAgePreferences(
    ageMin: Float, onAgeMinChange: (Float) -> Unit,
    ageMax: Float, onAgeMaxChange: (Float) -> Unit,
    lookingForGender: String, onLookingForGenderChange: (String) -> Unit,
    lookingForPersonality: String, onLookingForPersonalityChange: (String) -> Unit,
    onNext: () -> Unit
) {
    StepHeader(stringResource(R.string.title_preferences), stringResource(R.string.message_looking_for))

    FieldLabel(stringResource(R.string.prefix_age_range, ageMin.toInt(), ageMax.toInt()))
    RangeSlider(
        value = ageMin..ageMax,
        onValueChange = { range ->
            onAgeMinChange(range.start)
            onAgeMaxChange(range.endInclusive)
        },
        valueRange =  MIN_AGE.toFloat()..MAX_AGE.toFloat(),
        colors = SliderDefaults.colors(thumbColor = Primary, activeTrackColor = Primary)
    )
    Spacer(Modifier.height(24.dp))

    FieldLabel(stringResource(R.string.title_looking_for_gender))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf(
            Options.GENDER_OPTIONS[0] to Icons.Rounded.Male,
            Options.GENDER_OPTIONS[1] to Icons.Rounded.Female,
            Options.GENDER_OPTIONS[2] to Icons.Rounded.FaceRetouchingNatural
        ).forEach { (text, icon) ->
            FilterChip(
                selected = lookingForGender == text.value,
                onClick = { onLookingForGenderChange(text.value.lowercase(getDefault())) },
                label = {
                    Text(stringResource(text.translationKey))
                },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (lookingForGender == text.value) Surface else TextSecondary
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    labelColor = TextSecondary,
                    selectedContainerColor = Primary,
                    selectedLabelColor = Surface
                ),
                border = BorderStroke(
                    1.25.dp,
                    if (lookingForGender == text.value) Color.Transparent else TextSecondary
                )
            )
        }
    }
    Spacer(Modifier.height(24.dp))

    FieldLabel(stringResource(R.string.title_looking_for_personality))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Options.PERSONALITIES_OPTIONS.forEach { option ->
            val isSelected = lookingForPersonality == option.value
            FilterChip(
                selected = isSelected,
                onClick = { onLookingForPersonalityChange(option.value) },
                label = { Text(stringResource(option.translationKey)) },
                colors = FilterChipDefaults.filterChipColors(
                    labelColor = TextSecondary,
                    selectedContainerColor = Primary,
                    selectedLabelColor = Surface
                ),
                border = BorderStroke(
                    1.25.dp,
                    if (isSelected) Color.Transparent else TextSecondary
                )
            )
        }
    }

    Spacer(Modifier.height(32.dp))
    ContinueButton(
        enabled = lookingForGender.isNotBlank() && lookingForPersonality.isNotBlank(),
        onClick = onNext
    )
}
