package com.aether.core.player

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class SubtitlePosition {
    TOP_CENTER, CENTER, BOTTOM_CENTER, BOTTOM_LEFT, BOTTOM_RIGHT
}

data class SubtitleStyle(
    val fontSize: TextUnit = 18.sp,
    val fontWeight: FontWeight = FontWeight.Bold,
    val color: Color = Color.White,
    val backgroundColor: Color = Color.Black.copy(alpha = 0.6f),
    val outlineColor: Color = Color.Black,
    val outlineWidth: Dp = 2.dp,
    val position: SubtitlePosition = SubtitlePosition.BOTTOM_CENTER,
    val verticalOffset: Float = 0.1f,
)
