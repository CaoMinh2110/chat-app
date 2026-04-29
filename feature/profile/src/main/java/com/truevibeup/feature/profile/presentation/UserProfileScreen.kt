package com.truevibeup.feature.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.core.ui.component.ProfileContent
import com.truevibeup.feature.profile.viewmodel.UserProfileViewModel

@Composable
fun UserProfileScreen(
    navController: NavController,
    uuid: String,
    viewModel: UserProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val user = state.user

    LaunchedEffect(uuid) { viewModel.loadUser(uuid) }

    ProfileContent(
        user = user,
        posts = state.posts,
        currentUserId = uuid,
        isLoading = state.isLoading,
        isOwner = false,
        onFollowClick = {
            if (user?.isFollowing == true) viewModel.unfollow(uuid)
            else viewModel.follow(uuid)
        },
        onMessageClick = {
            viewModel.getOrCreateConversation(uuid) { convId ->
                navController.navigate(NavRoute.ChatRoom.createRoute(convId))
            }
        },
        onFollowersClick = { navController.navigate(NavRoute.Follow.createRoute(uuid, 0)) },
        onFollowingClick = { navController.navigate(NavRoute.Follow.createRoute(uuid, 1)) },
        onBackClick = { navController.popBackStack() },
        onLoadMore = viewModel::loadMorePosts,
        onLikePost = viewModel::likePost,
        onUnlikePost = viewModel::unlikePost,
        onDeletePost = viewModel::deletePost,
        onPostClick = { navController.navigate(NavRoute.PostDetail.createRoute(it)) },
        onAuthorClick = { navController.navigate(NavRoute.UserProfile.createRoute(it)) },
    )
}
