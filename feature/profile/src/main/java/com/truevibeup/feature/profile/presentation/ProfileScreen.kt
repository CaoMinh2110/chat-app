package com.truevibeup.feature.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.core.ui.component.ProfileContent
import com.truevibeup.feature.profile.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val user = state.currentUser

    LaunchedEffect(Unit) { viewModel.loadProfile() }

    ProfileContent(
        user = user,
        posts = state.posts,
        currentUserId = user?.id ?: "",
        isLoading = state.isLoading,
        isOwner = true,
        isUploadingAvatar = state.isUploadingAvatar,
        uploadError = state.uploadError,
        onUpdateAvatar = viewModel::updateAvatar,
        onEditClick = { navController.navigate(NavRoute.EditProfile.route) },
        onSettingsClick = { navController.navigate(NavRoute.Settings.route) },
        onFollowersClick = { user?.id?.let { navController.navigate(NavRoute.Follow.createRoute(it, 0)) } },
        onFollowingClick = { user?.id?.let { navController.navigate(NavRoute.Follow.createRoute(it, 1)) } },
        onLoadMore = viewModel::loadMorePosts,
        onCreatePost = viewModel::createPost,
        onLikePost = viewModel::likePost,
        onUnlikePost = viewModel::unlikePost,
        onDeletePost = viewModel::deletePost,
        onPostClick = { navController.navigate(NavRoute.PostDetail.createRoute(it)) },
        onAuthorClick = { navController.navigate(NavRoute.UserProfile.createRoute(it)) },
    )
}
