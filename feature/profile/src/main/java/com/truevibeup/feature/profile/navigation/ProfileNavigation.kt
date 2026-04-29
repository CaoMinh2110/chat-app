package com.truevibeup.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.feature.profile.presentation.EditProfileScreen
import com.truevibeup.feature.profile.presentation.FollowScreen
import com.truevibeup.feature.profile.presentation.SettingsScreen
import com.truevibeup.feature.profile.presentation.UserProfileScreen

fun NavGraphBuilder.profileGraph(navController: NavController) {
    composable(NavRoute.EditProfile.route) {
        EditProfileScreen(navController = navController)
    }
    composable(NavRoute.Settings.route) {
        SettingsScreen(navController = navController)
    }
    composable(
        route = NavRoute.UserProfile.route,
        arguments = listOf(navArgument("uuid") { type = NavType.StringType })
    ) { backStack ->
        val uuid = backStack.arguments?.getString("uuid") ?: return@composable
        UserProfileScreen(navController = navController, uuid = uuid)
    }
    composable(
        route = NavRoute.Follow.route,
        arguments = listOf(
            navArgument("userId") { type = NavType.StringType },
            navArgument("initialTab") { type = NavType.IntType; defaultValue = 0 },
        )
    ) { backStack ->
        val userId = backStack.arguments?.getString("userId") ?: return@composable
        val initialTab = backStack.arguments?.getInt("initialTab") ?: 0
        FollowScreen(
            navController = navController,
            userId = userId,
            initialTab = initialTab,
        )
    }
}
