package com.truevibeup.feature.chat.presentation.screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.truevibeup.core.common.model.Conversation
import com.truevibeup.core.common.model.User
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.core.ui.component.Avatar
import com.truevibeup.core.ui.component.ConversationItem
import com.truevibeup.core.ui.component.CustomFilterChip
import com.truevibeup.core.ui.component.SearchBar
import com.truevibeup.core.ui.theme.Background
import com.truevibeup.core.ui.theme.BackgroundAlt
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.TextMuted
import com.truevibeup.core.ui.theme.TextSecondary
import com.truevibeup.feature.chat.R
import com.truevibeup.feature.chat.presentation.viewmodel.ChatViewModel

enum class ChatFilter { All, Unread }

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConversationsScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(ChatFilter.All) }
    var isSearchMode by remember { mutableStateOf(false) }
    var showNewChatSheet by remember { mutableStateOf(false) }

    val filteredConversations = remember(state.conversations, searchQuery, selectedFilter) {
        state.conversations.filter { conversation ->
            val matchesQuery =
                conversation.otherUser.name.contains(searchQuery, ignoreCase = true) ||
                        (conversation.lastMessage?.content?.contains(searchQuery, ignoreCase = true)
                            ?: false)
            val matchesFilter = when (selectedFilter) {
                ChatFilter.All -> true
                ChatFilter.Unread -> conversation.unreadCount > 0
            }
            matchesQuery && matchesFilter
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isSearchMode) {
                    IconButton(onClick = { 
                        isSearchMode = false
                        searchQuery = ""
                    }) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                SearchBar(
                    query = searchQuery,
                    onQueryChange = { 
                        searchQuery = it
                        if (it.isNotEmpty()) isSearchMode = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { isSearchMode = true },
                    placeholderRes = R.string.hint_search
                )

                if (isSearchMode) {
                    IconButton(onClick = { /* Action search if needed */ }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.Send,
                            contentDescription = "Send",
                            tint = Primary,
                        )
                    }
                } else {
                    IconButton(onClick = { showNewChatSheet = true }) {
                        Icon(
                            painterResource(R.drawable.ic_rounded_edit_square),
                            contentDescription = "New Chat",
                            tint = TextMuted,
                        )
                    }
                }
            }
        }
    ) { padding ->
        Crossfade(
            targetState = isSearchMode,
            animationSpec = tween(durationMillis = 300),
            label = "SearchFade"
        ) { searchActive ->
            if (searchActive) {
                SearchContent(
                    conversations = filteredConversations,
                    searchQuery = searchQuery,
                    onConversationClick = { id ->
                        navController.navigate(NavRoute.ChatRoom.createRoute(id))
                    }
                )
            } else {
                ConversationsList(
                    padding = padding,
                    isLoading = state.isLoading,
                    conversations = state.conversations,
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it },
                    onConversationClick = { id ->
                        navController.navigate(NavRoute.ChatRoom.createRoute(id))
                    }
                )
            }
        }

        if (showNewChatSheet) {
            NewChatBottomSheet(
                viewModel = viewModel,
                onDismiss = { showNewChatSheet = false },
                onUserClick = { user ->
                    showNewChatSheet = false
                    viewModel.getOrCreateConversation(user.id) { id ->
                        navController.navigate(NavRoute.ChatRoom.createRoute(id))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ConversationsList(
    padding: PaddingValues,
    isLoading: Boolean,
    conversations: List<Conversation>,
    selectedFilter: ChatFilter,
    onFilterChange: (ChatFilter) -> Unit,
    onConversationClick: (Long) -> Unit
) {
    val filtered = remember(conversations, selectedFilter) {
        when (selectedFilter) {
            ChatFilter.All -> conversations
            ChatFilter.Unread -> conversations.filter { it.unreadCount > 0 }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = padding.calculateBottomPadding()),
        contentPadding = PaddingValues(bottom = 80.dp),
    ) {
        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Background)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomFilterChip(
                    selected = selectedFilter == ChatFilter.All,
                    onClick = { onFilterChange(ChatFilter.All) },
                    labelRes = R.string.title_all,
                    enableColor = Primary,
                    disableColor = TextMuted
                )

                CustomFilterChip(
                    selected = selectedFilter == ChatFilter.Unread,
                    onClick = { onFilterChange(ChatFilter.Unread) },
                    labelRes = R.string.title_unread,
                    enableColor = Primary,
                    disableColor = TextMuted
                )
            }
        }

        if (isLoading && conversations.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }
        } else if (filtered.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No conversations yet",
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            items(filtered, key = { it.id }) { conversation ->
                ConversationItem(
                    conversation = conversation,
                    onClick = { onConversationClick(conversation.id) },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun SearchContent(
    conversations: List<Conversation>,
    searchQuery: String,
    onConversationClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        if (conversations.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isEmpty()) "Start typing to search" else "No results found",
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            items(conversations, key = { it.id }) { conversation ->
                ConversationItem(
                    conversation = conversation,
                    onClick = { onConversationClick(conversation.id) },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewChatBottomSheet(
    viewModel: ChatViewModel,
    onDismiss: () -> Unit,
    onUserClick: (User) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var query by remember { mutableStateOf("") }

    LaunchedEffect(query) {
        viewModel.loadFollowing(query.ifBlank { null })
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // Simple Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(BackgroundAlt, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.hint_search),
                                style = TextStyle(fontSize = 14.sp, color = TextSecondary)
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            
            Text(
                text = "Following",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (state.isFollowingLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.following, key = { it.id }) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUserClick(user) }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Avatar(imageUrl = user.avatar, size = 48.dp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (!user.city.isNullOrBlank()) {
                                    Text(
                                        text = user.city!!,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 76.dp, end = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
