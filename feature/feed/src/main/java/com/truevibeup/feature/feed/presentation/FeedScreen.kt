package com.truevibeup.feature.feed.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.core.ui.component.CustomFilterChip
import com.truevibeup.core.ui.component.PostCard
import com.truevibeup.core.ui.component.PostCardShimmer
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.TextMuted
import com.truevibeup.feature.feed.R
import com.truevibeup.feature.feed.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    navController: NavController,
    currentUserId: String,
    viewModel: FeedViewModel = hiltViewModel(),
    scrollBehavior: TopAppBarScrollBehavior
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val density = LocalDensity.current

    val isAtTop by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 } }
    LaunchedEffect(isAtTop) { if (isAtTop) { scrollBehavior.state.heightOffset = 0f } }

    val lastVisibleIndex by remember {
        derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
    }
    LaunchedEffect(lastVisibleIndex) {
        if (lastVisibleIndex >= state.posts.size - 3 && !state.isLoadingMore && state.hasMore) {
            viewModel.loadMore()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                modifier = Modifier
                    .graphicsLayer { translationY = scrollBehavior.state.heightOffset }
                    .onGloballyPositioned {
                        val height = it.size.height.toFloat()
                        if (scrollBehavior.state.heightOffsetLimit > -height) {
                            scrollBehavior.state.heightOffsetLimit = -height
                        }
                    },
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                tonalElevation = 2.dp,
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                TopAppBar(
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    modifier = Modifier.statusBarsPadding(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomFilterChip(
                                selected = state.selectedType == "all",
                                onClick = { viewModel.onTypeSelected("all") },
                                labelRes = R.string.title_all,
                                enableColor = Primary,
                                disableColor = TextMuted
                            )

                            Spacer(Modifier.width(12.dp))

                            CustomFilterChip(
                                selected = state.selectedType == "following",
                                onClick = { viewModel.onTypeSelected("following") },
                                labelRes = R.string.title_following,
                                enableColor = Primary,
                                disableColor = TextMuted
                            )
                        }
                    },
                )
            }
        },
    ) { padding ->
        val bottomNavPadding = WindowInsets.navigationBars.asPaddingValues()
        val dynamicTopPadding = padding.calculateTopPadding() + 8.dp + 
                with(density) { scrollBehavior.state.heightOffset.toDp() }

        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                top = dynamicTopPadding.coerceAtLeast(0.dp),
                bottom = bottomNavPadding.calculateBottomPadding() + 80.dp
            ),
        ) {
            if (state.isLoading && state.posts.isEmpty()) {
                items(5) { PostCardShimmer() }
            } else if (state.error != null && state.posts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                state.error
                                    ?: stringResource(R.string.message_something_went_wrong),
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadFeed(refresh = true) }) {
                                Text(
                                    stringResource(R.string.title_retry)
                                )
                            }
                        }
                    }
                }
            } else {
                items(state.posts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        currentUserId = currentUserId,
                        onLike = { viewModel.likePost(post.id) },
                        onUnlike = { viewModel.unlikePost(post.id) },
                        onFollow = { viewModel.followUser(it) },
                        onUnfollow = { viewModel.unfollowUser(it) },
                        onAuthorClick = { navController.navigate(NavRoute.UserProfile.createRoute(it)) },
                        onPostClick = { navController.navigate(NavRoute.PostDetail.createRoute(it)) },
                        onCommentClick = { navController.navigate(NavRoute.PostDetail.createRoute(it, scrollToComments = true)) },
                        onDelete = { viewModel.deletePost(it) },
                    )
                }
                if (state.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
