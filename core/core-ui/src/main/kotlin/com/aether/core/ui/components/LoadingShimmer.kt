package com.aether.core.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.aether.core.ui.theme.Cosmic
import com.aether.core.ui.theme.CosmicLight

fun Modifier.shimmerEffect(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslate",
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(Cosmic, CosmicLight, Cosmic),
            start = Offset(translateAnim - 300f, 0f),
            end = Offset(translateAnim, 0f),
        ),
    )
}

@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier.shimmerEffect())
}

@Composable
fun ShimmerChannelCard(modifier: Modifier = Modifier) {
    ShimmerBox(
        modifier = modifier
            .size(160.dp, 90.dp)
            .clip(RoundedCornerShape(8.dp)),
    )
}

@Composable
fun ShimmerPosterCard(modifier: Modifier = Modifier) {
    ShimmerBox(
        modifier = modifier
            .size(120.dp, 180.dp)
            .clip(RoundedCornerShape(8.dp)),
    )
}
