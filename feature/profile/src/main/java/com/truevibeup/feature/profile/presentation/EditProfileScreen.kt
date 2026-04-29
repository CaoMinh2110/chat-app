package com.truevibeup.feature.profile.presentation

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FaceRetouchingNatural
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.truevibeup.core.common.AppConstants.MAX_AGE
import com.truevibeup.core.common.AppConstants.MIN_AGE
import com.truevibeup.core.common.OptionItem
import com.truevibeup.core.common.Options
import com.truevibeup.core.ui.component.BirthdayTextField
import com.truevibeup.core.ui.component.CustomDropdown
import com.truevibeup.core.ui.component.dashedBorder
import com.truevibeup.core.ui.theme.Background
import com.truevibeup.core.ui.theme.Danger
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.Surface
import com.truevibeup.core.ui.theme.TextPrimary
import com.truevibeup.core.ui.theme.TextSecondary
import com.truevibeup.feature.profile.R
import com.truevibeup.feature.profile.viewmodel.ProfileViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val user = state.currentUser

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var cityName by remember { mutableStateOf("") }
    var countryName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf(TextFieldValue("")) }
    var ageMin by remember { mutableFloatStateOf(18f) }
    var ageMax by remember { mutableFloatStateOf(45f) }
    var lookingForGender by remember { mutableStateOf("") }
    var lookingForPersonality by remember { mutableStateOf("") }

    val profilePhotos = remember { mutableStateListOf<String>() }

    var countryMenuExpanded by remember { mutableStateOf(false) }
    var cityMenuExpanded by remember { mutableStateOf(false) }

    var countryCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var cityCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    BackHandler(enabled = countryMenuExpanded || cityMenuExpanded) {
        countryMenuExpanded = false
        cityMenuExpanded = false
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris ->
        uris.forEach { uri ->
            if (profilePhotos.size < 10) {
                profilePhotos.add(uri.toString())
            }
        }
    }

    val selectedLookingFor = remember { mutableStateListOf<String>() }
    val selectedHobbies = remember { mutableStateListOf<String>() }
    val selectedTraits = remember { mutableStateListOf<String>() }
    val selectedMovies = remember { mutableStateListOf<String>() }
    val selectedMusic = remember { mutableStateListOf<String>() }

    LaunchedEffect(user) {
        user?.let {
            name = it.name
            description = it.description.orEmpty()
            cityName = it.city.orEmpty()
            countryName = it.country.orEmpty()
            gender = it.gender
            birthday = TextFieldValue(it.birthday.orEmpty())
            ageMin = it.lookingForAgeMin?.toFloat() ?: 18f
            ageMax = it.lookingForAgeMax?.toFloat() ?: 45f
            lookingForGender = it.lookingForGender.orEmpty()
            lookingForPersonality = it.lookingForPersonality.orEmpty()

            selectedLookingFor.clear()
            it.lookingFor?.let { list -> selectedLookingFor.addAll(list) }

            selectedHobbies.clear()
            it.hobbies?.let { list -> selectedHobbies.addAll(list) }

            selectedTraits.clear()
            it.traits?.let { list -> selectedTraits.addAll(list) }

            selectedMovies.clear()
            it.movies?.let { list -> selectedMovies.addAll(list) }

            selectedMusic.clear()
            it.music?.let { list -> selectedMusic.addAll(list) }

            profilePhotos.clear()
            it.photos?.let { list -> profilePhotos.addAll(list) }
        }
    }

    LaunchedEffect(state.countries) {
        if (countryName.isNotEmpty() && state.countries.isNotEmpty() && state.cities.isEmpty() && !state.isLoadingLocations) {
            val countryObj = state.countries.find { c -> c.name.orEmpty() == countryName }
            countryObj?.code?.let { code -> viewModel.loadCities(code) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Surface
                        )
                    }
                },
                title = {
                    Text(
                        text = name,
                        color = Surface,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text(
                text = stringResource(R.string.title_personal_information),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Spacer(Modifier.height(16.dp))

            ProfileField(label = stringResource(R.string.title_name)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = TextSecondary,
                        unfocusedTextColor = TextSecondary,
                        focusedBorderColor = Primary
                    )
                )
            }

            ProfileField(label = stringResource(R.string.title_gender)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        Options.GENDER_OPTIONS[0] to Icons.Rounded.Male,
                        Options.GENDER_OPTIONS[1] to Icons.Rounded.Female,
                        Options.GENDER_OPTIONS[2] to Icons.Rounded.FaceRetouchingNatural
                    ).forEach { (text, icon) ->

                        val value = text.value.lowercase()
                        
                        FilterChip(
                            selected = gender == value,
                            onClick = { gender = value },
                            label = {
                                Text(stringResource(text.translationKey))
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (gender == value) Surface else TextSecondary
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                labelColor = TextSecondary,
                                selectedContainerColor = Primary,
                                selectedLabelColor = Surface
                            ),
                            border = BorderStroke(
                                1.25.dp,
                                if (gender == value) Color.Transparent else TextSecondary
                            )
                        )
                    }
                }
            }

            ProfileField(
                label = "${stringResource(R.string.title_birthday)} : ${stringResource(R.string.format_title_date)}"
            ) {
                BirthdayTextField(
                    Modifier.fillMaxWidth(),
                    value = birthday,
                    onValueChange = { birthday = it },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                stringResource(R.string.title_photos),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Spacer(Modifier.height(16.dp))

            EditablePhotoGallery(
                photoUrls = profilePhotos,
                onAddClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemoveClick = { index ->
                    profilePhotos.removeAt(index)
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Text(
                stringResource(R.string.title_location),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Spacer(Modifier.height(16.dp))

            ProfileField(label = stringResource(R.string.title_country)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { countryCoords = it }
                ) {
                    OutlinedTextField(
                        value = countryName,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text(stringResource(R.string.title_select_country)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryMenuExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextSecondary
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
                            countryName = country.name.orEmpty()
                            cityName = ""
                            country.code?.let { viewModel.loadCities(it) }
                        }
                    )
                }
            }

            ProfileField(label = stringResource(R.string.title_city)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { cityCoords = it }
                ) {
                    OutlinedTextField(
                        value = cityName,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = {
                            Text(
                                if (countryName.isEmpty())
                                    stringResource(R.string.title_select_country_first)
                                else
                                    stringResource(R.string.title_select_city)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = countryName.isNotEmpty(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityMenuExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextSecondary
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
                            cityName = city.name.orEmpty()
                        },
                        isLoading = state.isLoadingLocations
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Text(
                stringResource(R.string.title_preferences),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Spacer(Modifier.height(16.dp))

            ProfileField(label = "${stringResource(R.string.title_age_range)}: ${ageMin.toInt()} - ${ageMax.toInt()}") {
                RangeSlider(
                    value = ageMin..ageMax,
                    onValueChange = { range ->
                        ageMin = range.start
                        ageMax = range.endInclusive
                    },
                    valueRange = MIN_AGE.toFloat()..MAX_AGE.toFloat(),
                    colors = SliderDefaults.colors(thumbColor = Primary, activeTrackColor = Primary)
                )
            }

            ProfileField(label = stringResource(R.string.title_looking_for_gender)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        Options.GENDER_OPTIONS[0] to Icons.Rounded.Male,
                        Options.GENDER_OPTIONS[1] to Icons.Rounded.Female,
                        Options.GENDER_OPTIONS[2] to Icons.Rounded.FaceRetouchingNatural
                    ).forEach { (text, icon) ->

                        val value = text.value.lowercase()
                        FilterChip(
                            selected = lookingForGender == value,
                            onClick = { lookingForGender = value },
                            label = {
                                Text(stringResource(text.translationKey))
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (lookingForGender == value) Surface else TextSecondary
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                labelColor = TextSecondary,
                                selectedContainerColor = Primary,
                                selectedLabelColor = Surface
                            ),
                            border = BorderStroke(
                                1.25.dp,
                                if (lookingForGender == value) Color.Transparent else TextSecondary
                            )
                        )
                    }
                }
            }

            SingleSelectField(
                label = stringResource(R.string.title_looking_for_personality),
                options = Options.PERSONALITIES_OPTIONS,
                selectedItem = lookingForPersonality,
                onSelectionChange = { lookingForPersonality = it }
            )

            MultiSelectField(
                label = stringResource(R.string.title_looking_for),
                options = Options.LOOKING_FOR_OPTIONS,
                selectedItems = selectedLookingFor
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                stringResource(R.string.title_about_me),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Spacer(Modifier.height(16.dp))

            ProfileField(label = stringResource(R.string.title_bio)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    placeholder = { Text(stringResource(R.string.hint_about_yourself)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = TextSecondary,
                        unfocusedTextColor = TextSecondary,
                        focusedBorderColor = Primary
                    )
                )
            }

            MultiSelectField(
                label = stringResource(R.string.title_traits),
                options = Options.TRAITS_OPTIONS,
                selectedItems = selectedTraits
            )

            MultiSelectField(
                label = stringResource(R.string.title_interests),
                options = Options.HOBBIES_OPTIONS,
                selectedItems = selectedHobbies
            )

            MultiSelectField(
                label = stringResource(R.string.title_music),
                options = Options.MUSIC_OPTIONS,
                selectedItems = selectedMusic
            )

            MultiSelectField(
                label = stringResource(R.string.title_movies),
                options = Options.MOVIES_OPTIONS,
                selectedItems = selectedMovies
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Spacer(Modifier.height(32.dp))

            if (state.error != null) {
                Text(state.error!!, color = Danger, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    viewModel.updateProfile(
                        mapOf(
                            "name" to name,
                            "gender" to gender,
                            "birthday" to birthday.text,
                            "description" to description,
                            "city" to cityName,
                            "country" to countryName,
                            "looking_for_age_min" to ageMin.toInt(),
                            "looking_for_age_max" to ageMax.toInt(),
                            "looking_for_gender" to lookingForGender,
                            "looking_for_personality" to lookingForPersonality,
                            "looking_for" to selectedLookingFor.toList(),
                            "hobbies" to selectedHobbies.toList(),
                            "traits" to selectedTraits.toList(),
                            "music" to selectedMusic.toList(),
                            "movies" to selectedMovies.toList(),
                            "photos" to profilePhotos.toList()
                        ),
                        onBack = { navController.popBackStack() }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = name.isNotBlank() && !state.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(12.dp),
            ) {
                if (state.isLoading) CircularProgressIndicator(
                    color = Surface,
                    modifier = Modifier.size(20.dp)
                )
                else Text(
                    stringResource(R.string.title_save),
                    fontWeight = FontWeight.Bold,
                    color = Surface,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MultiSelectField(
    label: String,
    options: List<OptionItem>,
    selectedItems: MutableList<String>,
    maxSelections: Int = 3
) {
    ProfileField(label = "$label (${selectedItems.size}/$maxSelections)") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedItems.contains(option.value)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            selectedItems.remove(option.value)
                        } else {
                            if (selectedItems.size >= maxSelections) {
                                selectedItems.removeAt(0)
                            }
                            selectedItems.add(option.value)
                        }
                    },
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
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SingleSelectField(
    label: String,
    options: List<OptionItem>,
    selectedItem: String,
    onSelectionChange: (String) -> Unit
) {
    ProfileField(label = label) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedItem == option.value
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectionChange(option.value) },
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
    }
}

@Composable
private fun ProfileField(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(6.dp))
        content()
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditablePhotoGallery(
    photoUrls: List<String>,
    maxPhotos: Int = 10,
    maxItemsEachRow: Int = 4,
    onAddClick: () -> Unit,
    onRemoveClick: (Int) -> Unit
) {
    val spacing = 8.dp

    BoxWithConstraints {
        val itemSize = (maxWidth - spacing * (maxItemsEachRow - 1)) / maxItemsEachRow - 1.dp

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = maxItemsEachRow,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            photoUrls.forEachIndexed { index, url ->
                Box(modifier = Modifier.size(itemSize)) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(20.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .clickable { onRemoveClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            if (photoUrls.size < maxPhotos) {
                Box(modifier = Modifier.requiredSize(itemSize)) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .dashedBorder(
                                color = Primary,
                                strokeWidth = 2.dp,
                                cornerRadius = 8.dp
                            )
                            .clickable { onAddClick() },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", fontSize = 24.sp, color = Primary)
                        }
                    }
                }
            }
        }
    }
}
