package com.truevibeup.feature.chat.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.truevibeup.core.ui.component.Avatar
import com.truevibeup.core.ui.component.MessageBubble
import com.truevibeup.core.ui.theme.*
import com.truevibeup.feature.chat.presentation.viewmodel.ChatRoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(navController: NavController, conversationId: Long) {
    val viewModel: ChatRoomViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    // Assuming we have a way to get currentUserId, maybe from a shared AuthViewModel or similar
    // For now, let's assume it's passed or available.
    val currentUserId = "" // This should be handled properly

    LaunchedEffect(conversationId) { viewModel.init(conversationId) }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    val otherUser = state.conversation?.otherUser

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", tint = Surface)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(imageUrl = otherUser?.avatar, size = 36.dp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = otherUser?.name ?: "",
                                color = Surface,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                            )
                            Text(
                                text = when {
                                    state.otherIsTyping -> "typing..."
                                    otherUser?.isOnline == 1 -> "Online"
                                    else -> "Offline"
                                },
                                color = Surface.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
            )
        },
        bottomBar = {
            ChatInputBar(
                value = state.messageInput,
                onValueChange = { viewModel.setMessageInput(it) },
                onSend = { viewModel.sendMessage() },
                onAttach = { /* TODO: Image picker */ },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background),
        ) {
            if (state.isLoading && state.messages.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                if (state.hasMore) {
                    TextButton(
                        onClick = { viewModel.loadMessages(loadMore = true) },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Load older messages", color = TextMuted, fontSize = 12.sp) }
                }
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    items(state.messages, key = { it.id }) { message ->
                        MessageBubble(
                            message = message,
                            isMine = message.sender.id == currentUserId,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttach: () -> Unit,
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onAttach) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = TextMuted)
            }
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Type a message...", color = TextPlaceholder) },
                modifier = Modifier.weight(1f),
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Border,
                ),
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (value.isNotBlank()) Primary else SurfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(onClick = onSend, enabled = value.isNotBlank()) {
                    Icon(
                        Icons.AutoMirrored.Rounded.Send,
                        contentDescription = "Send",
                        tint = if (value.isNotBlank()) Surface else TextMuted
                    )
                }
            }
        }
    }
}
