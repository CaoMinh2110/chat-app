package com.truevibeup.core.ui.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

sealed class IconResource {
    data class Vector(val imageVector: ImageVector) : IconResource()
    data class Drawable(val resId: Int) : IconResource()
}

@Composable
fun GetIcon(
    modifier: Modifier = Modifier,
    icon: IconResource,
    iconColor: Color
) {
    when (icon) {
        is IconResource.Vector -> Icon(
            imageVector = icon.imageVector,
            contentDescription = null,
            tint = iconColor,
            modifier = modifier
        )

        is IconResource.Drawable -> Icon(
            painter = painterResource(icon.resId),
            contentDescription = null,
            tint = iconColor,
            modifier = modifier
        )
    }
}