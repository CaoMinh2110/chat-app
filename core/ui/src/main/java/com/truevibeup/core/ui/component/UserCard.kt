package com.truevibeup.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.truevibeup.core.common.model.User
import com.truevibeup.core.ui.theme.*

@Composable
fun UserCard(user: User, onUserClick: (String) -> Unit, onMessage: (String) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onUserClick(user.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                if (!user.avatar.isNullOrBlank()) {
                    AsyncImage(
                        model = user.avatar, contentDescription = user.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    )
                }
                if (user.isOnline == 1) {
                    Surface(
                        color = Online,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(8.dp).size(10.dp)
                            .align(androidx.compose.ui.Alignment.TopEnd),
                    ) {}
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = buildString { append(user.name); user.age?.let { if (it > 0) append(", ${user.age}") } },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
                if (!user.city.isNullOrBlank() || !user.country.isNullOrBlank()) {
                    Text(
                        text = listOfNotNull(user.city, user.country).joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(6.dp))
                Button(
                    onClick = { onMessage(user.id) },
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                ) { Text("Message", fontSize = 12.sp, color = Surface) }
            }
        }
    }
}
