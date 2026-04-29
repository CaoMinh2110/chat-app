package com.truevibeup.feature.auth.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.feature.auth.presentation.screen.*

const val AUTH_REGISTRATION_FLOW = "auth_registration_flow"

fun NavGraphBuilder.authGraph(navController: NavController) {
    composable(NavRoute.Splash.route) {
        SplashScreen(navController = navController)
    }
    composable(NavRoute.SelectLanguage.route) {
        SelectLanguageScreen(navController = navController)
    }

    // Nested graph — all screens share the same AuthViewModel instance
    navigation(startDestination = NavRoute.Welcome.route, route = AUTH_REGISTRATION_FLOW) {

        composable(NavRoute.Welcome.route) {
            WelcomeScreen(navController = navController)
        }

        composable(
            NavRoute.Login.route,
            enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
            exitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(200)) },
            popEnterTransition = { slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) },
            popExitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(200)) },
        ) {
            LoginScreen(navController = navController)
        }

        composable(
            NavRoute.Register.route,
            enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
            exitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(200)) },
            popEnterTransition = { slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) },
            popExitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(200)) },
        ) {
            RegisterScreen(navController = navController)
        }

        composable(
            NavRoute.OnboardingFlow.route,
            enterTransition = { fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 10 } },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(350)) },
            popExitTransition = { fadeOut(tween(200)) + slideOutVertically(tween(350)) { it / 10 } },
        ) {
            OnboardingScreen(navController = navController)
        }
    }
}
