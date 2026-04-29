package com.truevibeup.feature.search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.truevibeup.core.common.Options.GENDER_OPTIONS
import com.truevibeup.feature.search.R

@Composable
@Preview
fun GenderSection(
    selected: String? = null,
    onSelect: (String?) -> Unit = {},
    onBack: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SectionHeader(title = stringResource(R.string.title_select_gender), onBack = onBack)

        Spacer(Modifier.height(16.dp))

        RadioOption(
            name = "All",
            selected = selected == null,
            onClick = { onSelect(null) }
        )

        GENDER_OPTIONS.forEach { option ->
            RadioOption (
                name = stringResource(option.translationKey),
                selected = selected == option.value,
                onClick = { onSelect(option.value) }
            )
        }
    }
}
