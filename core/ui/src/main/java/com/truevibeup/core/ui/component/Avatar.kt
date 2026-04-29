package com.truevibeup.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.truevibeup.core.ui.theme.Primary
import com.truevibeup.core.ui.theme.Surface
import com.truevibeup.core.ui.theme.SurfaceVariant

@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    size: Dp = 44.dp,
    borderThickness: Dp = 2.dp,
    borderColor: Color = Primary,
    placeHolderIconColor: Color = Primary,
    isEditable: Boolean = false,
    onImageClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
) {
    Box(
        Modifier.size(size)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(borderThickness, borderColor, CircleShape)
        ) {

            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .let { mod ->
                            if (onImageClick != null) mod.clickable { onImageClick() } else mod
                        }
                )
            } else {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SurfaceVariant)
                        .padding(8.dp),
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = placeHolderIconColor
                )
            }
        }

        if (isEditable) {
            // Camera icon (bottom-right)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(size * 0.3f)
                    .clip(CircleShape)
                    .let { mod ->
                        if (onEditClick != null) mod.clickable { onEditClick() } else mod
                    }
                    .background(Primary)
                    .border(2.dp, Surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CameraAlt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.14f)
                )
            }
        }
    }
}
