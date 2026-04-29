@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalAnimationApi::class
)

package com.truevibeup.feature.auth.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.truevibeup.core.common.AppConstants.MIN_AGE
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.PrimarySurface
import com.truevibeup.core.ui.theme.TextMuted
import com.truevibeup.core.ui.theme.TextPrimary
import com.truevibeup.feature.auth.navigation.AUTH_REGISTRATION_FLOW
import com.truevibeup.feature.auth.presentation.viewmodel.AuthViewModel

private const val TOTAL_STEPS = 7

@Composable
fun OnboardingScreen(navController: NavController) {
    val parentEntry =
        remember(navController) { navController.getBackStackEntry(AUTH_REGISTRATION_FLOW) }
    val viewModel: AuthViewModel = hiltViewModel(parentEntry)
    val state by viewModel.state.collectAsState()

    // ── Step state ──────────────────────────────────────────────────────────
    var currentStep by rememberSaveable { mutableIntStateOf(1) }

    // Step 1
    var name by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var birthdayText by rememberSaveable { mutableStateOf("") }

    // Step 2
    var countryName by rememberSaveable { mutableStateOf("") }
    var cityName by rememberSaveable { mutableStateOf("") }

    // Step 3
    var ageMin by rememberSaveable { mutableFloatStateOf(MIN_AGE.toFloat()) }
    var ageMax by rememberSaveable { mutableFloatStateOf(45f) }
    var lookingForGender by rememberSaveable { mutableStateOf("") }
    var lookingForPersonality by rememberSaveable { mutableStateOf("") }

    // Step 4
    val selectedLookingFor = remember { mutableStateListOf<String>() }

    // Step 5
    val photos = remember { mutableStateListOf<String>() }

    // Step 6
    var description by rememberSaveable { mutableStateOf("") }
    val selectedHobbies = remember { mutableStateListOf<String>() }
    val selectedTraits = remember { mutableStateListOf<String>() }
    val selectedMusic = remember { mutableStateListOf<String>() }
    val selectedMovies = remember { mutableStateListOf<String>() }

    // ── Progress animation – stays alive in ONE composition ──────────────────
    val animatedProgress by animateFloatAsState(
        targetValue = currentStep.toFloat() / TOTAL_STEPS,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "onboardingProgress"
    )

    BackHandler(enabled = currentStep > 1) { currentStep-- }

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            navController.navigate(NavRoute.Main.route) {
                popUpTo(AUTH_REGISTRATION_FLOW) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentStep > 1) currentStep-- else navController.popBackStack()
                        }) {
                            Icon(Icons.Rounded.ArrowBackIosNew, null, tint = TextPrimary)
                        }
                    },
                    title = {
                        Text(
                            "Step $currentStep of $TOTAL_STEPS",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextMuted
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp),
                    color = Primary,
                    trackColor = PrimarySurface,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }
    ) { padding ->
        // AnimatedContent animates between step contents while Scaffold (progress bar) stays put
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                val toRight = targetState > initialState
                val enter = slideInHorizontally(tween(300)) { if (toRight) it else -it }
                val exit = slideOutHorizontally(tween(300)) { if (toRight) -it else it }
                enter togetherWith exit
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { step ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
            ) {
                Spacer(Modifier.height(20.dp))
                when (step) {
                    1 -> StepAboutYou(
                        name = name, onNameChange = { name = it },
                        gender = gender, onGenderChange = { gender = it },
                        birthdayText = birthdayText, onBirthdayChange = { birthdayText = it },
                        onNext = { currentStep++ }
                    )

                    2 -> StepLocation(
                        state = state,
                        viewModel = viewModel,
                        countryName = countryName,
                        onCountryChange = { countryName = it; cityName = "" },
                        cityName = cityName,
                        onCityChange = { cityName = it },
                        onNext = { currentStep++ }
                    )

                    3 -> StepAgePreferences(
                        ageMin = ageMin, onAgeMinChange = { ageMin = it },
                        ageMax = ageMax, onAgeMaxChange = { ageMax = it },
                        lookingForGender = lookingForGender, onLookingForGenderChange = { lookingForGender = it },
                        lookingForPersonality = lookingForPersonality, onLookingForPersonalityChange = { lookingForPersonality = it },
                        onNext = { currentStep++ }
                    )

                    4 -> StepLookingFor(
                        selected = selectedLookingFor,
                        onNext = { currentStep++ }
                    )

                    5 -> StepPhotos(
                        photos = photos,
                        onNext = { currentStep++ }
                    )

                    6 -> StepAboutMe(
                        description = description, onDescriptionChange = { description = it },
                        hobbies = selectedHobbies,
                        traits = selectedTraits,
                        music = selectedMusic,
                        movies = selectedMovies,
                        onNext = { currentStep++ }
                    )

                    7 -> StepFinish(
                        state = state,
                        onFinish = {
                            // Collect all form data into ViewModel in one call
                            viewModel.updateRegistrationData(
                                mapOf(
                                    "name" to name,
                                    "gender" to gender,
                                    "birthday" to birthdayText,
                                    "country" to countryName,
                                    "city" to cityName,
                                    "looking_for_age_min" to ageMin.toInt(),
                                    "looking_for_age_max" to ageMax.toInt(),
                                    "looking_for_gender" to lookingForGender,
                                    "looking_for_personality" to lookingForPersonality,
                                    "looking_for" to selectedLookingFor.toList(),
                                    "photos" to photos.toList(),
                                    "description" to description,
                                    "hobbies" to selectedHobbies.toList(),
                                    "traits" to selectedTraits.toList(),
                                    "music" to selectedMusic.toList(),
                                    "movies" to selectedMovies.toList()
                                )
                            )
                            viewModel.completeOnboardingAndRegister()
                        }
                    )
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
