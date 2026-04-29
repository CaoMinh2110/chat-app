package com.truevibeup.mobile.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.truevibeup.core.ui.component.Avatar
import com.truevibeup.core.ui.component.BadgedIcon
import com.truevibeup.core.ui.component.IconResource
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.Surface
import com.truevibeup.core.ui.theme.TextMuted
import com.truevibeup.feature.auth.presentation.viewmodel.AuthViewModel
import com.truevibeup.feature.notifications.presentation.viewmodel.NotificationsViewModel

private enum class MainTab(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    Feed("main_feed", Icons.Outlined.Home, Icons.Rounded.Home),
    Search("main_search", Icons.Outlined.Search, Icons.Rounded.Search),
    Chat("main_chat", Icons.Rounded.ChatBubbleOutline, Icons.AutoMirrored.Rounded.Chat),
    Notifications(
        "main_notifications",
        Icons.Rounded.NotificationsNone,
        Icons.Rounded.Notifications
    ),
    Profile("main_profile", Icons.Outlined.Person, Icons.Rounded.Person),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    feedScreen: @Composable (TopAppBarScrollBehavior) -> Unit,
    searchScreen: @Composable () -> Unit,
    chatScreen: @Composable () -> Unit,
    notificationsScreen: @Composable () -> Unit,
    profileScreen: @Composable () -> Unit
) {
    val bottomNavController = rememberNavController()
    val notificationsVm: NotificationsViewModel = hiltViewModel()
    val authVm: AuthViewModel = hiltViewModel()
    val notifState by notificationsVm.state.collectAsState()
    val authState by authVm.state.collectAsState()

    val backStack by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val selectedTabIndex =
        MainTab.entries.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    val isFeedScreen = currentRoute == MainTab.Feed.route
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var bottomBarHeight by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val flingUpThreshold = with(density) { 600.dp.toPx() }

    val customFeedScrollConnection = remember(scrollBehavior.state, flingUpThreshold) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0) {
                    val newOffset = (scrollBehavior.state.heightOffset + available.y)
                        .coerceIn(scrollBehavior.state.heightOffsetLimit, 0f)
                    scrollBehavior.state.heightOffset = newOffset
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (available.y > flingUpThreshold) {
                    scrollBehavior.state.heightOffset = 0f
                }
                return Velocity.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val currentOffset = scrollBehavior.state.heightOffset
                val limit = scrollBehavior.state.heightOffsetLimit
                if (currentOffset != 0f && currentOffset != limit) {
                    val target = if (currentOffset > limit / 2) 0f else limit
                    val anim = Animatable(currentOffset)
                    anim.animateTo(target, tween(200)) {
                        scrollBehavior.state.heightOffset = value
                    }
                }
                return super.onPostFling(consumed, available)
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .then(if (isFeedScreen) Modifier.nestedScroll(customFeedScrollConnection) else Modifier),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            val currentOffset = if (isFeedScreen) -scrollBehavior.state.heightOffset else 0f

            Column(
                modifier = Modifier
                    .onGloballyPositioned {
                        val height = it.size.height.toFloat()
                        if (bottomBarHeight != height) {
                            bottomBarHeight = height
                            scrollBehavior.state.heightOffsetLimit = -height
                        }
                    }
                    .graphicsLayer { translationY = currentOffset }
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Surface)
                    .navigationBarsPadding()
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = Primary,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            val tab = tabPositions[selectedTabIndex]
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.TopStart)
                                    .offset(x = tab.left)
                                    .width(tab.width)
                                    .padding(horizontal = 24.dp)
                                    .height(2.dp)
                                    .clip(CircleShape)
                                    .background(Primary)
                            )
                        }
                    },
                    divider = {},
                ) {
                    MainTab.entries.forEach { tab ->
                        val isSelected = currentRoute == tab.route
                        val unread = if (tab == MainTab.Notifications) notifState.unreadCount else 0

                        Tab(
                            selected = isSelected,
                            onClick = {
                                bottomNavController.navigate(tab.route) {
                                    popUpTo(bottomNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Box(contentAlignment = Alignment.Center) {
                                    when (tab) {
                                        MainTab.Profile -> Avatar(
                                            imageUrl = authState.currentUser?.avatar,
                                            size = 30.dp,
                                            borderThickness = 2.dp,
                                            borderColor = if (isSelected) Primary else TextMuted,
                                            placeHolderIconColor = if (isSelected) Primary else TextMuted
                                        )

                                        MainTab.Chat, MainTab.Notifications -> BadgedIcon(
                                            size = 26.dp,
                                            icon = IconResource.Vector(if (isSelected) tab.selectedIcon else tab.icon),
                                            iconColor = if (isSelected) Primary else TextMuted,
                                            backgroundColor = Surface,
                                            number = unread
                                        )

                                        else -> Icon(
                                            imageVector = if (isSelected) tab.selectedIcon else tab.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Primary else TextMuted,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        val feedBottomPadding = with(density) {
            (bottomBarHeight + scrollBehavior.state.heightOffset).toDp().coerceAtLeast(0.dp)
        }

        NavHost(
            navController = bottomNavController,
            startDestination = MainTab.Feed.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                val from =
                    MainTab.entries.indexOfFirst { it.route == initialState.destination.route }
                val to = MainTab.entries.indexOfFirst { it.route == targetState.destination.route }
                if (to >= from) slideInHorizontally(tween(280)) { it } + fadeIn(tween(280))
                else slideInHorizontally(tween(280)) { -it } + fadeIn(tween(280))
            },
            exitTransition = {
                val from =
                    MainTab.entries.indexOfFirst { it.route == initialState.destination.route }
                val to = MainTab.entries.indexOfFirst { it.route == targetState.destination.route }
                if (to >= from) slideOutHorizontally(tween(280)) { -it } + fadeOut(tween(280))
                else slideOutHorizontally(tween(280)) { it } + fadeOut(tween(280))
            },
            popEnterTransition = {
                val from =
                    MainTab.entries.indexOfFirst { it.route == initialState.destination.route }
                val to = MainTab.entries.indexOfFirst { it.route == targetState.destination.route }
                if (to >= from) slideInHorizontally(tween(280)) { it } + fadeIn(tween(280))
                else slideInHorizontally(tween(280)) { -it } + fadeIn(tween(280))
            },
            popExitTransition = {
                val from =
                    MainTab.entries.indexOfFirst { it.route == initialState.destination.route }
                val to = MainTab.entries.indexOfFirst { it.route == targetState.destination.route }
                if (to >= from) slideOutHorizontally(tween(280)) { -it } + fadeOut(tween(280))
                else slideOutHorizontally(tween(280)) { it } + fadeOut(tween(280))
            },
        ) {
            composable(MainTab.Feed.route) {
                Box(modifier = Modifier.padding(bottom = feedBottomPadding)) {
                    feedScreen(scrollBehavior)
                }
            }
            composable(MainTab.Search.route) {
                Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                    searchScreen()
                }
            }
            composable(MainTab.Chat.route) {
                Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                    chatScreen()
                }
            }
            composable(MainTab.Notifications.route) {
                Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                    notificationsScreen()
                }
            }
            composable(MainTab.Profile.route) {
                Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                    profileScreen()
                }
            }
        }
    }
}
