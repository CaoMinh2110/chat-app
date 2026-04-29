package com.truevibeup.feature.auth.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.truevibeup.core.ui.component.CustomDropdown
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.TextSecondary
import com.truevibeup.feature.auth.presentation.component.ContinueButton
import com.truevibeup.feature.auth.presentation.component.FieldLabel
import com.truevibeup.feature.auth.presentation.component.StepHeader
import com.truevibeup.feature.auth.presentation.viewmodel.AuthState
import com.truevibeup.feature.auth.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepLocation(
    state: AuthState,
    viewModel: AuthViewModel,
    countryName: String, onCountryChange: (String) -> Unit,
    cityName: String, onCityChange: (String) -> Unit,
    onNext: () -> Unit
) {
    var countryMenuExpanded by remember { mutableStateOf(false) }
    var cityMenuExpanded by remember { mutableStateOf(false) }

    var countryCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var cityCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    StepHeader("Your Location", "Help others find you nearby")

    FieldLabel("Country")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { countryCoords = it }
    ) {
        OutlinedTextField(
            value = countryName,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Select Country") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryMenuExpanded) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = TextSecondary, focusedBorderColor = Primary
            )
        )
        Surface(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(12.dp))
                .clickable { countryMenuExpanded = true },
            color = Color.Transparent
        ) {}

        CustomDropdown(
            expanded = countryMenuExpanded,
            onDismiss = { countryMenuExpanded = false },
            anchorCoordinates = countryCoords,
            items = state.countries,
            itemToString = { it.name.orEmpty() },
            onItemClick = { country ->
                onCountryChange(country.name.orEmpty())
                country.code?.let { viewModel.loadCities(it) }
                countryMenuExpanded = false
            }
        )
    }

    Spacer(Modifier.height(16.dp))

    FieldLabel("City")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { cityCoords = it }
    ) {
        OutlinedTextField(
            value = cityName,
            onValueChange = {},
            readOnly = true,
            enabled = countryName.isNotEmpty(),
            placeholder = { Text(if (countryName.isEmpty()) "Select a country first" else "Select City") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityMenuExpanded) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = TextSecondary, focusedBorderColor = Primary
            )
        )
        if (countryName.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { cityMenuExpanded = true },
                color = Color.Transparent
            ) {}
        }

        CustomDropdown(
            expanded = cityMenuExpanded,
            onDismiss = { cityMenuExpanded = false },
            anchorCoordinates = cityCoords,
            items = state.cities,
            itemToString = { it.name.orEmpty() },
            onItemClick = { city ->
                onCityChange(city.name.orEmpty())
                cityMenuExpanded = false
            },
            isLoading = state.isLoadingLocations
        )
    }

    Spacer(Modifier.height(32.dp))

    ContinueButton(
        enabled = countryName.isNotBlank() && cityName.isNotBlank(),
        onClick = onNext
    )
}
