package com.truevibeup.feature.feed.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.feature.feed.presentation.PostDetailScreen

fun NavGraphBuilder.feedGraph(navController: NavController, currentUserId: String) {
    composable(
        route = NavRoute.PostDetail.route,
        arguments = listOf(
            navArgument("postId") { type = NavType.LongType },
            navArgument("scrollToComments") { type = NavType.BoolType; defaultValue = false },
        )
    ) { backStack ->
        val postId = backStack.arguments?.getLong("postId") ?: return@composable
        val scrollToComments = backStack.arguments?.getBoolean("scrollToComments") ?: false
        PostDetailScreen(
            navController = navController,
            postId = postId,
            currentUserId = currentUserId,
            scrollToComments = scrollToComments,
        )
    }
}

// Note: FeedScreen is often used as a tab in MainScreen, so it might be called directly there.
