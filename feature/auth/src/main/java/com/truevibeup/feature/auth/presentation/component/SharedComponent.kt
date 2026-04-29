package com.truevibeup.feature.auth.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truevibeup.core.common.OptionItem
import com.truevibeup.core.common.Options
import com.truevibeup.core.ui.theme.*

@Composable
fun StepHeader(title: String, subtitle: String) {
    Text(title, style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(6.dp))
    Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
    Spacer(Modifier.height(24.dp))
}

@Composable
fun FieldLabel(label: String) {
    Text(label, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(6.dp))
}

@Composable
fun ContinueButton(enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Primary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("Continue", color = Surface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipMultiSelect(
    label: String,
    options: List<OptionItem>,
    selected: MutableList<String>,
    maxSelections: Int
) {
    FieldLabel("$label/$maxSelections)")
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = selected.contains(option.value)
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) selected.remove(option.value)
                    else if (selected.size < maxSelections) selected.add(option.value)
                },
                label = { Text(stringResource(option.translationKey)) },
                colors = FilterChipDefaults.filterChipColors(
                    labelColor = TextSecondary,
                    selectedContainerColor = Primary, selectedLabelColor = Surface
                ),
                border = BorderStroke(1.25.dp, if (isSelected) Color.Transparent else TextSecondary)
            )
        }
    }
}
