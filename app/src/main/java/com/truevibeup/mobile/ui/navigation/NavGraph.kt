package com.truevibeup.mobile.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.feature.auth.navigation.authGraph
import com.truevibeup.feature.auth.presentation.viewmodel.AuthViewModel
import com.truevibeup.mobile.ui.screen.MainScreen
import com.truevibeup.feature.feed.presentation.FeedScreen
import com.truevibeup.feature.search.presentation.SearchScreen
import com.truevibeup.feature.chat.presentation.screen.ConversationsScreen
import com.truevibeup.feature.notifications.presentation.screen.NotificationsScreen
import com.truevibeup.feature.profile.presentation.ProfileScreen
import com.truevibeup.feature.chat.navigation.chatGraph
import com.truevibeup.feature.feed.navigation.feedGraph
import com.truevibeup.feature.profile.navigation.profileGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrueVibeUpNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.state.collectAsState()
    val currentUserId = authState.currentUser?.id ?: ""

    NavHost(
        navController = navController,
        startDestination = NavRoute.Splash.route,
        enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
        exitTransition = { slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300)) },
        popEnterTransition = { slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) },
        popExitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)) },
    ) {
        authGraph(navController)

        composable(
            NavRoute.Main.route,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = { fadeOut(tween(200)) },
        ) {
            MainScreen(
                feedScreen = { scrollBehavior ->
                    FeedScreen(
                        navController = navController,
                        currentUserId = currentUserId,
                        scrollBehavior = scrollBehavior
                    )
                },
                searchScreen = { SearchScreen(navController = navController) },
                chatScreen = { ConversationsScreen(navController = navController) },
                notificationsScreen = { 
                    NotificationsScreen(
                        onNotificationClick = { type, id ->
                            when(type) {
                                "POST" -> navController.navigate(NavRoute.PostDetail.createRoute(id as Long))
                                "USER" -> navController.navigate(NavRoute.UserProfile.createRoute(id as String))
                            }
                        }
                    ) 
                },
                profileScreen = { ProfileScreen(navController = navController) }
            )
        }

        chatGraph(navController)
        feedGraph(navController, currentUserId)
        profileGraph(navController)
    }
}
