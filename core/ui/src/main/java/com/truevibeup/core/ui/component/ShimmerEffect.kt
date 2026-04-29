package com.truevibeup.core.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerBox(width: Dp = Dp.Unspecified, height: Dp, modifier: Modifier = Modifier, cornerRadius: Dp = 8.dp) {
    val shimmerColors = listOf(Color(0xFFE0E0E0), Color(0xFFF5F5F5), Color(0xFFE0E0E0))
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1200, easing = LinearEasing)),
        label = "shimmer_translate",
    )
    val brush = Brush.linearGradient(shimmerColors, start = Offset.Zero, end = Offset(translateAnim, translateAnim))
    val sizeMod = if (width != Dp.Unspecified) modifier.size(width = width, height = height)
    else modifier.fillMaxWidth().height(height)
    Box(modifier = sizeMod.clip(RoundedCornerShape(cornerRadius)).background(brush))
}

@Composable
fun PostCardShimmer() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
            .background(Color.White, RoundedCornerShape(16.dp)).padding(12.dp),
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            ShimmerBox(width = 40.dp, height = 40.dp, cornerRadius = 20.dp)
            Spacer(Modifier.width(10.dp))
            Column {
                ShimmerBox(width = 120.dp, height = 14.dp)
                Spacer(Modifier.height(4.dp))
                ShimmerBox(width = 80.dp, height = 11.dp)
            }
        }
        Spacer(Modifier.height(10.dp))
        ShimmerBox(height = 14.dp)
        Spacer(Modifier.height(4.dp))
        ShimmerBox(height = 14.dp)
        Spacer(Modifier.height(8.dp))
        ShimmerBox(height = 180.dp, cornerRadius = 12.dp)
    }
}
