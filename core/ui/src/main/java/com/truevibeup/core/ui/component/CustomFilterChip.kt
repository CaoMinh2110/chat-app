package com.truevibeup.core.ui.component

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource

@Composable
fun CustomFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    labelRes: Int,
    enableColor: Color,
    disableColor: Color,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(stringResource(labelRes)) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = enableColor.copy(alpha = 0.1f),
            selectedLabelColor = enableColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = disableColor,
            selectedBorderColor = enableColor,
            disabledBorderColor = disableColor
        )
    )
}