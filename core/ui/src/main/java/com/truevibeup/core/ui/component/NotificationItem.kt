package com.truevibeup.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.truevibeup.core.common.model.AppNotification
import com.truevibeup.core.ui.theme.*
import com.truevibeup.core.ui.util.formatTime
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationItem(notification: AppNotification, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
            .background(if (!notification.isRead) PrimarySurface else Surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            Avatar(imageUrl = notification.actor.avatar, size = 46.dp)
            val (icon, iconColor) = when (notification.type) {
                "like" -> Icons.Default.Favorite to Primary
                "comment" -> Icons.Default.ChatBubble to MaterialTheme.colorScheme.secondary
                "follow" -> Icons.Default.PersonAdd to MaterialTheme.colorScheme.tertiary
                else -> Icons.Default.Notifications to TextMuted
            }
            Box(
                modifier = Modifier.size(18.dp).align(Alignment.BottomEnd).background(iconColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) { Icon(icon, contentDescription = null, tint = Surface, modifier = Modifier.size(12.dp)) }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            val description = when (notification.type) {
                "like" -> "${notification.actor.name} liked your post"
                "comment" -> "${notification.actor.name} commented on your post"
                "follow" -> "${notification.actor.name} started following you"
                "message" -> "${notification.actor.name} sent you a message"
                else -> "New notification"
            }
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(2.dp))
            Text(text = formatTime(notification.createdAt), style = MaterialTheme.typography.bodySmall)
        }
    }
}
