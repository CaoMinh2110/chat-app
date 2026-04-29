package com.truevibeup.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.truevibeup.core.common.model.Post
import com.truevibeup.core.ui.R
import com.truevibeup.core.ui.theme.BorderLight
import com.truevibeup.core.ui.theme.Danger
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.TextMuted
import com.truevibeup.core.ui.util.formatTime

@Composable
fun PostCard(
    post: Post,
    currentUserId: String,
    onLike: () -> Unit,
    onUnlike: () -> Unit,
    onFollow: (String) -> Unit,
    onUnfollow: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onPostClick: (Long) -> Unit,
    onCommentClick: (Long) -> Unit = {},
    onDelete: (Long) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var contentExpanded by remember { mutableStateOf(false) }
    val isOwn = post.author.id == currentUserId

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onPostClick(post.id) },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(
                    modifier = Modifier.clickable { onAuthorClick(post.author.id) },
                    imageUrl = post.author.avatar,
                    size = 40.dp,
                    onImageClick = { onAuthorClick(post.author.id) }
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.author.name,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onAuthorClick(post.author.id) })
                    Text(
                        text = formatTime(post.createdAt),
                        style = MaterialTheme.typography.titleSmall,
                        color = TextMuted
                    )
                }
                if (!isOwn) {
                    OutlinedButton(
                        onClick = {
                            if (post.author.isFollowing) onUnfollow(post.author.id)
                            else onFollow(post.author.id)
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
                }
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = TextMuted)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }) {
                        if (isOwn) {
                            DropdownMenuItem(
                                text = { Text("Delete", color = Danger) },
                                onClick = { menuExpanded = false; onDelete(post.id) })
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            val content = post.content.orEmpty()

            // Content
            if (content.isNotBlank()) {
                val isLong = content.length > 200
                Column {
                    Text(
                        text = if (!contentExpanded && isLong) content.take(200) + "..." else content,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextMuted
                    )
                    
                    TranslateSection(content = content, modifier = Modifier.padding(top = 4.dp))
                    
                    if (isLong) {
                        Row(
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { contentExpanded = !contentExpanded },
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    if (contentExpanded) R.string.title_show_less
                                    else R.string.title_show_more
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                color = Primary,
                                fontSize = 13.sp,
                            )

                            Icon(
                                imageVector =
                                    if (contentExpanded) Icons.Rounded.KeyboardArrowUp
                                    else Icons.Rounded.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Primary
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            // Images
            if (post.images.isNotEmpty()) {
                PostImageGrid(images = post.images)
                Spacer(Modifier.height(8.dp))
            }
            // Actions
            HorizontalDivider(color = BorderLight)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (post.isLiked) onUnlike() else onLike() }) {
                    Icon(
                        if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like", tint = if (post.isLiked) Primary else TextMuted
                    )
                }
                Text(text = "${post.likesCount}", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.width(16.dp))
                Row(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onCommentClick(post.id) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(text = "${post.commentsCount}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun PostImageGrid(images: List<String>) {
    when (images.size) {
        1 -> AsyncImage(
            model = images[0], contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        2 -> Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            images.forEach { url ->
                AsyncImage(
                    model = url, contentDescription = null, contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }

        else -> Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            AsyncImage(
                model = images[0], contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                images.drop(1).take(2).forEachIndexed { index, url ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(78.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
