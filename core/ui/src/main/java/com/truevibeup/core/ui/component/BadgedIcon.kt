package com.truevibeup.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truevibeup.core.ui.theme.Primary

@Composable
fun BadgedIcon(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    icon: IconResource,
    iconColor: Color = Primary,
    badgeColor: Color = Color.Red,
    backgroundColor: Color = Color.White,
    numberColor: Color = Color.White,
    number: Int = 0,
    maxCount: Int = 99,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .background(backgroundColor)
            .let {
                if (onClick != null) {
                    it.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    it
                }
            }
    ) {
        GetIcon(
            modifier = Modifier.fillMaxSize(),
            icon = icon,
            iconColor = iconColor
        )

        if (number > 0) {
            val display = if (number > maxCount) "$maxCount+" else "$number"

            val badgeOuterPadding = size * 0.05f
            val badgeInnerHorizontal = size * 0.12f
            val textSize = (size.value * 0.22f).coerceAtLeast(10f).sp

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(backgroundColor, CircleShape)
                    .padding(badgeOuterPadding)
            ) {
                Box(
                    modifier = Modifier
                        .background(badgeColor, CircleShape)
                        .padding(horizontal = badgeInnerHorizontal),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = display,
                        color = numberColor,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall.copy(
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                            fontSize = textSize
                        )
                    )
                }
            }
        }
    }
}
