package com.truevibeup.feature.auth.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FaceRetouchingNatural
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.truevibeup.core.common.Options
import com.truevibeup.core.ui.component.BirthdayTextField
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.Surface
import com.truevibeup.core.ui.theme.TextSecondary
import com.truevibeup.feature.auth.presentation.component.ContinueButton
import com.truevibeup.feature.auth.presentation.component.FieldLabel
import com.truevibeup.feature.auth.presentation.component.StepHeader
import java.util.Locale.getDefault

@Composable
fun StepAboutYou(
    name: String, onNameChange: (String) -> Unit,
    gender: String, onGenderChange: (String) -> Unit,
    birthdayText: String, onBirthdayChange: (String) -> Unit,
    onNext: () -> Unit
) {
    var birthday by remember { mutableStateOf(TextFieldValue(birthdayText)) }

    StepHeader("About You", "Tell us who you are")

    FieldLabel("Your Name")
    OutlinedTextField(
        value = name, onValueChange = onNameChange,
        placeholder = { Text("Enter your name") },
        modifier = Modifier.fillMaxWidth(), singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = TextSecondary, focusedBorderColor = Primary
        )
    )
    Spacer(Modifier.height(16.dp))

    FieldLabel("Gender")
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf(
            Options.GENDER_OPTIONS[0] to Icons.Rounded.Male,
            Options.GENDER_OPTIONS[1] to Icons.Rounded.Female,
            Options.GENDER_OPTIONS[2] to Icons.Rounded.FaceRetouchingNatural
        ).forEach { (text, icon) ->
            FilterChip(
                selected = gender == text.value,
                onClick = { onGenderChange(text.value.lowercase(getDefault())) },
                label = {
                    Text(stringResource(text.translationKey))
                },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (gender == text.value) Surface else TextSecondary
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    labelColor = TextSecondary,
                    selectedContainerColor = Primary,
                    selectedLabelColor = Surface
                ),
                border = BorderStroke(
                    1.25.dp,
                    if (gender == text.value) Color.Transparent else TextSecondary
                )
            )
        }
    }
    Spacer(Modifier.height(16.dp))

    FieldLabel("Birthday (YYYY-MM-DD)")
    BirthdayTextField(
        modifier = Modifier.fillMaxWidth(),
        value = birthday,
        onValueChange = {
            birthday = it
            onBirthdayChange(it.text)
        },
        placeholder = { Text("e.g. 1995-06-15") }
    )
    Spacer(Modifier.height(32.dp))

    ContinueButton(
        enabled = name.isNotBlank() && gender.isNotBlank() && birthdayText.length >= 8,
        onClick = onNext
    )
}
