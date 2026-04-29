package com.truevibeup.feature.auth.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.truevibeup.core.common.Options.LOOKING_FOR_OPTIONS
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.Surface
import com.truevibeup.core.ui.theme.TextSecondary
import com.truevibeup.feature.auth.presentation.component.ContinueButton
import com.truevibeup.feature.auth.presentation.component.StepHeader

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StepLookingFor(selected: MutableList<String>, onNext: () -> Unit) {
    StepHeader("What Are You Looking For?", "Select all that apply")

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LOOKING_FOR_OPTIONS.forEach { option ->
            val isSelected = selected.contains(option.value)
            FilterChip(
                selected = isSelected,
                onClick = { if (isSelected) selected.remove(option.value) else selected.add(option.value) },
                label = { Text(stringResource(option.translationKey)) },
                colors = FilterChipDefaults.filterChipColors(
                    labelColor = TextSecondary,
                    selectedContainerColor = Primary, selectedLabelColor = Surface
                ),
                border = BorderStroke(1.25.dp, if (isSelected) Color.Transparent else TextSecondary)
            )
        }
    }
    Spacer(Modifier.height(32.dp))
    ContinueButton(enabled = selected.isNotEmpty(), onClick = onNext)
}