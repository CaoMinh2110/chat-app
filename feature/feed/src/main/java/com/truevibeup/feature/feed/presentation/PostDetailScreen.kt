package com.truevibeup.feature.feed.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.truevibeup.core.common.navigation.NavRoute
import com.truevibeup.core.ui.component.Avatar
import com.truevibeup.core.ui.component.PostCardShimmer
import com.truevibeup.core.ui.component.TranslateSection
import com.truevibeup.core.ui.theme.Background
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.SurfaceVariant
import com.truevibeup.core.ui.theme.TextMuted
import com.truevibeup.core.ui.util.formatTime
import com.truevibeup.feature.feed.R
import com.truevibeup.feature.feed.components.CommentItemView
import com.truevibeup.feature.feed.viewmodel.PostDetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PostDetailScreen(
    navController: NavController,
    postId: Long,
    currentUserId: String,
    scrollToComments: Boolean = false,
    viewModel: PostDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var commentText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val post = state.post
    var menuExpanded by remember { mutableStateOf(false) }
    val isOwn = post?.author?.id == currentUserId
    val replyingToCommentId = state.replyingToCommentId
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    var isTextFieldFocused by remember { mutableStateOf(false) }

    var hasPerformedInitialScroll by rememberSaveable(postId) { mutableStateOf(false) }

    val replyingToComment = remember(replyingToCommentId, state.flatComments) {
        state.flatComments.find { it.id == replyingToCommentId }
    }

    val isAtTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 }
    }

    LaunchedEffect(postId) { viewModel.load(postId) }

    LaunchedEffect(state.isLoading, state.flatComments) {
        if (!state.isLoading && scrollToComments && !hasPerformedInitialScroll && state.flatComments.isNotEmpty()) {
            val p = state.post ?: return@LaunchedEffect
            val hasContent = !p.content.isNullOrBlank()
            val hasImages = p.images.isNotEmpty()
            val headerCount = (if (hasContent) 1 else 0) + (if (hasImages) 1 else 0) + 1
            listState.animateScrollToItem(headerCount)
            hasPerformedInitialScroll = true
        }
    }

    LaunchedEffect(replyingToCommentId) {
        if (replyingToCommentId == null) return@LaunchedEffect

        delay(50)

        focusRequester.requestFocus()

        val flatIdx = state.flatComments.indexOfFirst { it.id == replyingToCommentId }

        if (flatIdx >= 0) {
            val hasContent = state.post?.content?.isNotBlank() == true
            val hasImages = state.post?.images?.isNotEmpty() == true
            val headerCount = (if (hasContent) 1 else 0) + (if (hasImages) 1 else 0) + 1

            listState.animateScrollToItem(index = headerCount + flatIdx)

            delay(100)
        }
    }

    BackHandler(enabled = replyingToCommentId != null || isTextFieldFocused) {
        if (replyingToCommentId != null) {
            viewModel.setReplyingTo(null)
        }
        focusManager.clearFocus()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Background)
                        .statusBarsPadding()
                        .height(56.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            coroutineScope.launch {
                                listState.animateScrollToItem(0)
                            }
                        }
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
                    }
                    if (post != null) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Avatar(
                                imageUrl = post.author.avatar,
                                size = 36.dp,
                                onImageClick = {
                                    navController.navigate(
                                        NavRoute.UserProfile.createRoute(post.author.id)
                                    )
                                }
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = post.author.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { navController.navigate(NavRoute.UserProfile.createRoute(post.author.id)) }
                                )
                                Text(
                                    text = formatTime(post.createdAt),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextMuted
                                )
                            }
                        }
                        if (!isOwn) {
                            OutlinedButton(
                                onClick = {
                                    if (post.author.isFollowing) viewModel.unfollow(post.author.id)
                                    else viewModel.follow(post.author.id)
                                },
                                shape = CircleShape,
                                border = BorderStroke(
                                    2.dp,
                                    if (post.author.isFollowing) TextMuted else Primary
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = if (post.author.isFollowing) TextMuted else Primary
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                            ) {
                                Text(
                                    text = stringResource(
                                        if (post.author.isFollowing) R.string.title_following
                                        else R.string.title_follow
                                    ),
                                    color = if (post.author.isFollowing) TextMuted else Primary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(Modifier.width(4.dp))
                        }

                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                if (isOwn) {
//                                    DropdownMenuItem(
//                                        text = { Text(stringResource(R.string.action_delete_post), color = Danger) },
//                                        onClick = {
//                                            viewModel.deletePost()
//                                            menuExpanded = false
//                                            navController.popBackStack()
//                                        }
//                                    )
                                } else {
//                                    DropdownMenuItem(
//                                        text = { Text(stringResource(R.string.action_report)) },
//                                        onClick = { menuExpanded = false }
//                                    )
                                }
                            }
                        }
                    }
                }
                AnimatedVisibility(!isAtTop) {
                    HorizontalDivider(color = TextMuted, thickness = 1.dp)
                }
            }
        },
        bottomBar = {
            if (post != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .imePadding()
                        .navigationBarsPadding()
                ) {
                    HorizontalDivider(color = TextMuted)

                    AnimatedVisibility(
                        visible = replyingToCommentId != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.prefix_reply_to,
                                    replyingToComment?.author?.name ?: ""
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            IconButton(
                                onClick = { viewModel.setReplyingTo(null) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cancel",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = {
                                Text(
                                    stringResource(if (replyingToCommentId != null) R.string.hint_add_reply else R.string.hint_comment),
                                    color = TextMuted
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .onFocusChanged { isTextFieldFocused = it.isFocused },
                            maxLines = 3,
                            shape = CircleShape,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SurfaceVariant,
                                unfocusedContainerColor = SurfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { /* camera */ }) {
                                        Icon(
                                            Icons.Outlined.CameraAlt,
                                            contentDescription = "Camera",
                                            tint = TextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    IconButton(onClick = { /* emoji */ }) {
                                        Icon(
                                            Icons.Outlined.EmojiEmotions,
                                            contentDescription = "Emoji",
                                            tint = TextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (commentText.isNotBlank()) {
                                    if (replyingToCommentId != null) {
                                        viewModel.addReply(commentText.trim())
                                    } else {
                                        viewModel.addComment(commentText.trim())
                                    }
                                    commentText = ""
                                }
                            }),
                        )
                        AnimatedContent(
                            targetState = commentText.isBlank(),
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                        ) { isEmpty ->
                            if (!isEmpty) {
                                Spacer(Modifier.width(8.dp))

                                IconButton(onClick = {
                                    if (commentText.isNotBlank()) {
                                        if (replyingToCommentId != null) {
                                            viewModel.addReply(commentText.trim())
                                        } else {
                                            viewModel.addComment(commentText.trim())
                                        }
                                        commentText = ""
                                    }
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.Send,
                                        contentDescription = "Send",
                                        tint = Primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
    ) { padding ->
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(top = 16.dp)
            ) {
                PostCardShimmer()
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                ),
            ) {
                post?.let { p ->
                    if (!p.content.isNullOrBlank()) {
                        item {
                            Text(
                                text = p.content!!,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextMuted,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 12.dp, bottom = 4.dp)
                            )

                            TranslateSection(
                                p.content!!, Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 12.dp)
                            )
                        }
                    }

                    if (p.images.isNotEmpty()) {
                        item { PostDetailImageGrid(images = p.images) }
                    }

                    item {
                        HorizontalDivider(color = TextMuted)
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { if (p.isLiked) viewModel.unlike() else viewModel.like() }) {
                                Icon(
                                    if (p.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like",
                                    tint = if (p.isLiked) Primary else TextMuted
                                )
                            }
                            Text("${p.likesCount}", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.width(16.dp))

                            Row(
                                modifier = Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    coroutineScope.launch {
                                        val hasContent = !p.content.isNullOrBlank()
                                        val hasImages = p.images.isNotEmpty()
                                        val headerCount =
                                            (if (hasContent) 1 else 0) + (if (hasImages) 1 else 0) + 1
                                        listState.animateScrollToItem(headerCount)
                                        hasPerformedInitialScroll = true
                                    }
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ChatBubbleOutline,
                                    contentDescription = "Comments",
                                    tint = TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "${p.commentsCount}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        HorizontalDivider(color = TextMuted)
                    }

                    items(state.flatComments, key = { it.id }) { comment ->
                        CommentItemView(
                            comment = comment,
                            replyingToCommentId = replyingToCommentId,
                            onReplyClick = { viewModel.setReplyingTo(it) },
                            onToggleReplies = { viewModel.toggleReplies(it) },
                            onImageClick = {
                                navController.navigate(NavRoute.UserProfile.createRoute(it))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PostDetailImageGrid(images: List<String>) {
    when (images.size) {
        1 -> AsyncImage(
            model = images[0],
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )

        2 -> Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            images.forEach { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp)
                )
            }
        }

        else -> Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            AsyncImage(
                model = images[0],
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .height(200.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                images.drop(1).take(2).forEachIndexed { index, url ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(99.dp)
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        if (index == 1 && images.size > 3) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${images.size - 3}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
