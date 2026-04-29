package com.truevibeup.feature.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.truevibeup.core.common.model.User
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.core.ui.component.BadgedIcon
import com.truevibeup.core.ui.component.IconResource
import com.truevibeup.core.ui.component.UserCard
import com.truevibeup.core.ui.theme.Background
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.feature.search.components.FilterSheet
import com.truevibeup.core.ui.component.SearchBar
import com.truevibeup.feature.search.viewmodel.SearchFilter
import com.truevibeup.feature.search.viewmodel.SearchState
import com.truevibeup.feature.search.viewmodel.SearchViewModel
import com.truevibeup.feature.search.viewmodel.activeCount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    SearchContent(
        state = state,
        navController = navController,
        onQueryChange = viewModel::onQueryChange,
        onLoadMore = viewModel::loadMore,
        onApplyFilter = viewModel::applyFilter,
        onMessageClick = { uuid ->
            viewModel.getOrCreateConversation(uuid) { conversationId ->
                navController.navigate(NavRoute.ChatRoom.createRoute(conversationId))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    state: SearchState,
    navController: NavController,
    onQueryChange: (String) -> Unit,
    onLoadMore: () -> Unit,
    onApplyFilter: (SearchFilter) -> Unit,
    onMessageClick: (String) -> Unit,
) {
    val gridState = rememberLazyGridState()
    var showFilterSheet by remember { mutableStateOf(false) }

    val lastVisibleIndex by remember {
        derivedStateOf { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
    }
    LaunchedEffect(lastVisibleIndex) {
        if (lastVisibleIndex >= state.users.size - 4 && !state.isLoadingMore && state.hasMore) {
            onLoadMore()
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchBar(
                    query = state.query,
                    onQueryChange = onQueryChange,
                    modifier = Modifier.weight(1f)
                )
                BadgedIcon(
                    icon = IconResource.Vector(Icons.Rounded.FilterAlt),
                    size = 24.dp,
                    iconColor = Color.Black.copy(alpha = 0.7f),
                    backgroundColor = Background,
                    number = state.filters.activeCount(state.defaultFilter)
                ) { showFilterSheet = true }
            }
        }
    ) { padding ->
        when {
            state.isLoading && state.users.isEmpty() -> LoadingState(Modifier.padding(padding))
            state.users.isEmpty() -> EmptyState(Modifier.padding(padding))
            else -> UserGrid(
                state = state,
                gridState = gridState,
                padding = padding,
                navController = navController,
                onMessageClick = onMessageClick
            )
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState =  rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                )
            ) {
                FilterSheet(
                    initialFilter = state.filters,
                    countries = state.countries,
                    onApply = { filter ->
                        onApplyFilter(filter)
                        showFilterSheet = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("No users found", color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun UserGrid(
    state: SearchState,
    gridState: LazyGridState,
    padding: PaddingValues,
    navController: NavController,
    onMessageClick: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(
            start = 8.dp, end = 8.dp,
            top = padding.calculateTopPadding() + 8.dp,
            bottom = 80.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.navigationBarsPadding(),
    ) {
        items(state.users, key = { it.id }) { user ->
            UserCard(
                user = user,
                onUserClick = { navController.navigate(NavRoute.UserProfile.createRoute(it)) },
                onMessage = onMessageClick,
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
                    CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val dummyState = SearchState(
        users = listOf(
            User(id = "1", name = "John Doe", city = "New York", country = "USA", age = 25),
            User(id = "2", name = "Jane Smith", city = "London", country = "UK", age = 22),
            User(id = "3", name = "Alice Johnson", city = "Paris", country = "France", age = 28),
            User(id = "4", name = "Bob Brown", city = "Berlin", country = "Germany", age = 30),
        ),
        isLoading = false
    )
    MaterialTheme {
        SearchContent(
            state = dummyState,
            navController = rememberNavController(),
            onQueryChange = {},
            onLoadMore = {},
            onApplyFilter = {},
            onMessageClick = {}
        )
    }
}
