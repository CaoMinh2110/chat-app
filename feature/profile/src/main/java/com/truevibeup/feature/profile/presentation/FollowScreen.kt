package com.truevibeup.feature.profile.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.truevibeup.core.common.model.User
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.core.ui.component.Avatar
import com.truevibeup.core.ui.theme.Background
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.SurfaceVariant
import com.truevibeup.core.ui.theme.TextMuted
import com.truevibeup.core.ui.theme.TextSecondary
import com.truevibeup.feature.profile.R
import com.truevibeup.feature.profile.viewmodel.FollowViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FollowScreen(
    navController: NavController,
    userId: String,
    initialTab: Int = 0,
    currentUserId: String = "",
    viewModel: FollowViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = initialTab) { 2 }

    LaunchedEffect(userId) { viewModel.init(userId) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", tint = Color.White)
                    }
                },
                title = {
                    Text(
                        text = state.username,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.White,
                contentColor = Primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = Primary
                    )
                }
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                    text = { Text(stringResource(R.string.title_followers)) }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                    text = { Text(stringResource(R.string.title_following)) }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) { page ->
                when (page) {
                    0 -> FollowList(
                        initialQuery = state.followersQuery,
                        users = state.followers,
                        isLoading = state.isLoadingFollowers,
                        currentUserId = currentUserId,
                        onQueryChange = viewModel::onFollowersQueryChange,
                        onFollowClick = { user ->
                            if (user.isFollowing) viewModel.unfollow(user.id)
                            else viewModel.follow(user.id)
                        },
                        onUserClick = { navController.navigate(NavRoute.UserProfile.createRoute(it)) }
                    )
                    1 -> FollowList(
                        initialQuery = state.followingQuery,
                        users = state.following,
                        isLoading = state.isLoadingFollowing,
                        currentUserId = currentUserId,
                        onQueryChange = viewModel::onFollowingQueryChange,
                        onFollowClick = { user ->
                            if (user.isFollowing) viewModel.unfollow(user.id)
                            else viewModel.follow(user.id)
                        },
                        onUserClick = { navController.navigate(NavRoute.UserProfile.createRoute(it)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FollowList(
    initialQuery: String,
    users: List<User>,
    isLoading: Boolean,
    currentUserId: String,
    onQueryChange: (String) -> Unit,
    onFollowClick: (User) -> Unit,
    onUserClick: (String) -> Unit,
) {
    var query by remember(initialQuery) { mutableStateOf(initialQuery) }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                onQueryChange(it)
            },
            placeholder = { Text("Search...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = TextSecondary) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = TextSecondary,
            )
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(users, key = { it.id }) { user ->
                    FollowUserItem(
                        user = user,
                        isSelf = user.id == currentUserId,
                        onFollowClick = { onFollowClick(user) },
                        onUserClick = { onUserClick(user.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FollowUserItem(
    user: User,
    isSelf: Boolean,
    onFollowClick: () -> Unit,
    onUserClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            imageUrl = user.avatar,
            size = 44.dp,
            onImageClick = onUserClick
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            if (!user.city.isNullOrBlank() || !user.country.isNullOrBlank()) {
                Text(
                    text = listOfNotNull(user.city, user.country).joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
        if (!isSelf) {
            Button(
                onClick = onFollowClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (user.isFollowing) SurfaceVariant else Primary,
                    contentColor = if (user.isFollowing) TextSecondary else Color.White,
                ),
                modifier = Modifier.size(width = 96.dp, height = 34.dp)
            ) {
                Text(
                    text = stringResource(
                        if (user.isFollowing) R.string.title_following 
                        else R.string.title_follow
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (user.isFollowing) TextMuted else Color.White,
                )
            }
        }
    }
}
