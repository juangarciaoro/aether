package com.aether.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aether.core.ui.theme.DeepSpace
import com.aether.core.ui.theme.Nebula

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(DeepSpace.copy(alpha = 0.85f))
            .border(
                width = 0.5.dp,
                color = Nebula.copy(alpha = 0.3f),
                shape = RoundedCornerShape(cornerRadius),
            ),
        content = content,
    )
}
