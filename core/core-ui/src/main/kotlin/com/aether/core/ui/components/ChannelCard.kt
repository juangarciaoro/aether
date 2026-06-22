package com.aether.core.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import com.aether.core.ui.theme.DeepSpace
import com.aether.core.ui.theme.NeonIndigo

@Composable
fun TvChannelCard(
    name: String,
    logoUrl: String,
    channelId: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    logoBitmap: Bitmap? = null,
) {
    var isFocused by remember { mutableStateOf(false) }

    val glowColor = remember(logoBitmap) {
        logoBitmap?.let {
            val vibrant = Palette.from(it).generate().getVibrantColor(NeonIndigo.toArgb())
            Color(vibrant)
        } ?: NeonIndigo
    }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.08f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tvCardScale",
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 0.7f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tvGlow",
    )

    Column(
        modifier = modifier
            .width(120.dp)
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable(onClick = onClick)
            .drawBehind {
                if (glowAlpha > 0f) {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(glowColor.copy(alpha = glowAlpha * 0.5f), Color.Transparent),
                            radius = size.maxDimension,
                        ),
                    )
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(if (isFocused) RoundedCornerShape(12.dp) else CircleShape)
                .background(DeepSpace)
                .then(
                    if (isFocused) Modifier.border(2.dp, glowColor, RoundedCornerShape(12.dp)) else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (logoUrl.isNotBlank()) {
                AsyncImage(
                    model = logoUrl,
                    contentDescription = name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(72.dp),
                )
            } else {
                Text(
                    text = name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonIndigo,
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
