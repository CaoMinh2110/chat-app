package com.truevibeup.feature.chat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.feature.chat.presentation.screen.ChatRoomScreen
import com.truevibeup.feature.chat.presentation.screen.ConversationsScreen

fun NavGraphBuilder.chatGraph(navController: NavController) {
    composable(
        route = NavRoute.ChatRoom.route,
        arguments = listOf(navArgument("conversationId") { type = NavType.LongType })
    ) { backStack ->
        val conversationId = backStack.arguments?.getLong("conversationId") ?: return@composable
        ChatRoomScreen(navController = navController, conversationId = conversationId)
    }
}
