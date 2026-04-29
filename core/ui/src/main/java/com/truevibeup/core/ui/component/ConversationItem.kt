package com.truevibeup.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truevibeup.core.common.model.Conversation
import com.truevibeup.core.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConversationItem(conversation: Conversation, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            Avatar(imageUrl = conversation.otherUser.avatar, size = 50.dp)
            if (conversation.otherUser.isOnline == 1) {
                Box(modifier = Modifier.size(12.dp).align(Alignment.BottomEnd).background(Online, CircleShape))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = conversation.otherUser.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(text = formatConvTime(conversation.lastActivityAt), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(2.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                val lastMsg = conversation.lastMessage
                val preview = when {
                    lastMsg == null -> "Start a conversation"
                    lastMsg.isDeleted -> "Message was unsent"
                    lastMsg.type == "image" -> "📷 Photo"
                    lastMsg.type == "audio" -> "🎤 Voice message"
                    lastMsg.type == "sticker" -> "🎨 Sticker"
                    else -> lastMsg.content ?: ""
                }
                Text(text = preview, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                if (conversation.unreadCount > 0) {
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier.background(Primary, RoundedCornerShape(50)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text(text = "${conversation.unreadCount}", color = Surface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun formatConvTime(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }
        val date = sdf.parse(iso) ?: return ""
        val diff = (System.currentTimeMillis() - date.time) / 1000
        when {
            diff < 3600 -> "${diff / 60}m"
            diff < 86400 -> "${diff / 3600}h"
            diff < 604800 -> "${diff / 86400}d"
            else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
        }
    } catch (_: Exception) { "" }
}
