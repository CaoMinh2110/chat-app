package com.truevibeup.core.ui.component

import android.R.attr.digits
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.TextSecondary

@Composable
fun BirthdayTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = TextSecondary,
        unfocusedTextColor = TextSecondary,
        focusedBorderColor = Primary
    ),
    placeholder:  @Composable (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            val digits = newValue.text.filter { it.isDigit() }.take(8)
            val parts = newValue.text.split("-")

            val yearRaw = parts.getOrNull(0)?.filter { it.isDigit() }?.take(4) ?: digits.take(4)
            val monthRaw = parts.getOrNull(1)?.filter { it.isDigit() }?.take(2) ?: digits.drop(4).take(2)
            val dayRaw = parts.getOrNull(2)?.filter { it.isDigit() }?.take(2) ?: digits.drop(6).take(2)

            val year = if (yearRaw.length < 4 && (monthRaw.isNotEmpty() || dayRaw.isNotEmpty())) {
                yearRaw.padStart(4, '0')
            } else yearRaw

            val month = if (monthRaw.isNotEmpty()) {
                val mInt = monthRaw.toIntOrNull() ?: 0
                val mCoerced = if (dayRaw.isNotEmpty() || monthRaw.length == 2) mInt.coerceIn(1, 12) else mInt
                if (dayRaw.isNotEmpty() || monthRaw.length == 2) mCoerced.toString().padStart(2, '0') else monthRaw
            } else ""

            val day = if (dayRaw.isNotEmpty()) {
                val yInt = year.toIntOrNull() ?: 2000
                val mInt = month.toIntOrNull() ?: 1
                val maxDay = when (mInt) {
                    1,3,5,7,8,10,12 -> 31
                    4,6,9,11 -> 30
                    2 -> if (isLeapYear(yInt)) 29 else 28
                    else -> 31
                }
                val dInt = dayRaw.toIntOrNull() ?: 0
                val dCoerced = if (dayRaw.length == 2) dInt.coerceIn(1, maxDay) else dInt
                if (dayRaw.length == 2) dCoerced.toString().padStart(2, '0') else dayRaw
            } else ""

            val formatted = buildString {
                append(year)
                if (month.isNotEmpty() || digits.length > 4) append("-$month")
                if (day.isNotEmpty() || digits.length > 6) append("-$day")
            }

            var cursorPos = newValue.selection.end

            val dashBeforeCursor = formatted.take(cursorPos).count { it == '-' }

            cursorPos += dashBeforeCursor

            val newCursor = cursorPos.coerceIn(0, formatted.length)

            onValueChange(TextFieldValue(formatted, TextRange(newCursor)))
        },
        placeholder = placeholder,
        modifier = modifier.onFocusChanged { focusState ->
            if (!focusState.isFocused && value.text.length == 9) {
                val padded = value.text.replaceRange(8, 9, value.text[8].toString().padStart(2, '0'))
                onValueChange(TextFieldValue(padded, TextRange(padded.length)))
            }
            isFocused = focusState.isFocused
        },
        singleLine = true,
        shape = shape,
        colors = colors,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}