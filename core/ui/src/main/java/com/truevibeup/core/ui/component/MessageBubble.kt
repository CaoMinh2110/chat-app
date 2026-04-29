package com.truevibeup.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.truevibeup.core.common.model.Message
import com.truevibeup.core.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessageBubble(message: Message, isMine: Boolean) {
    val bubbleColor = if (isMine) Primary else SurfaceVariant
    val textColor = if (isMine) Surface else TextPrimary
    val shape = if (isMine)
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    else
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start,
    ) {
        if (message.isDeleted) {
            Box(modifier = Modifier.background(SurfaceVariant, shape).padding(horizontal = 14.dp, vertical = 8.dp)) {
                Text("This message was unsent", color = TextMuted, fontSize = 13.sp)
            }
        } else when (message.type) {
            "image" -> AsyncImage(
                model = message.mediaUrl, contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(width = 220.dp, height = 180.dp).clip(shape),
            )
            "audio" -> Box(modifier = Modifier.background(bubbleColor, shape).padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎤", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    val secs = message.duration ?: 0
                    Text("${secs / 60}:${"%02d".format(secs % 60)}", color = textColor, fontSize = 14.sp)
                }
            }
            "sticker" -> AsyncImage(model = message.mediaUrl, contentDescription = "Sticker", modifier = Modifier.size(100.dp))
            else -> Box(modifier = Modifier.background(bubbleColor, shape).padding(horizontal = 14.dp, vertical = 8.dp)) {
                Text(text = message.content ?: "", color = textColor, fontSize = 15.sp)
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = formatMsgTime(message.createdAt),
            style = MaterialTheme.typography.labelSmall,
            textAlign = if (isMine) TextAlign.End else TextAlign.Start,
        )
    }
}

private fun formatMsgTime(iso: String): String = try {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(sdf.parse(iso) ?: "")
} catch (_: Exception) { "" }
