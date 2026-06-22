package com.aether.core.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette

@Composable
fun ChromaticFocusCard(
    bitmap: Bitmap?,
    isFocused: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val glowColor = remember(bitmap) {
        bitmap?.let {
            Palette.from(it).generate()
                .getVibrantColor(Color.Transparent.toArgb())
                .let { argb -> Color(argb) }
        } ?: Color.Transparent
    }

    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 0.8f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "glowAlpha",
    )

    Box(
        modifier = modifier.drawBehind {
            if (isFocused && glowAlpha > 0f) {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = glowAlpha * 0.4f),
                            Color.Transparent,
                        ),
                        radius = size.maxDimension * 0.8f,
                    ),
                )
            }
        },
    ) { content() }
}
