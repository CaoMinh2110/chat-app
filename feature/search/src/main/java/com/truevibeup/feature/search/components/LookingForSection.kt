package com.truevibeup.feature.search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.truevibeup.core.common.Options.LOOKING_FOR_OPTIONS
import com.truevibeup.feature.search.R

@Composable
fun LookingForSection(
    selected: String?,
    onSelect: (String?) -> Unit,
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(title = stringResource(R.string.title_looking), onBack = onBack)

        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                RadioOption(
                    name = "All",
                    selected = selected == null,
                    onClick = { onSelect(null) }
                )
            }
            items(LOOKING_FOR_OPTIONS) { option ->
                RadioOption (
                    name = stringResource(option.translationKey),
                    selected = selected == option.value,
                    onClick = { onSelect(option.value) }
                )
            }
        }
    }
}
