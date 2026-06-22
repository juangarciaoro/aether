package com.aether.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.aether.core.common.device.DeviceType

private val AetherColorScheme = darkColorScheme(
    primary = NeonIndigo,
    onPrimary = Starlight,
    primaryContainer = Cosmic,
    onPrimaryContainer = NeonIndigoLight,
    secondary = Plasma,
    onSecondary = Starlight,
    secondaryContainer = Cosmic,
    onSecondaryContainer = PlasmaLight,
    background = Void,
    onBackground = Starlight,
    surface = DeepSpace,
    onSurface = Starlight,
    surfaceVariant = Cosmic,
    onSurfaceVariant = Nebula,
    error = Error,
    onError = Starlight,
    outline = Nebula,
    outlineVariant = CosmicLight,
)

@Composable
fun AetherTheme(
    deviceType: DeviceType = DeviceType.PHONE,
    content: @Composable () -> Unit
) {
    val typography = when (deviceType) {
        DeviceType.TV -> AetherTvTypography
        else -> AetherTypography
    }

    CompositionLocalProvider(
        LocalSpacing provides AetherSpacing()
    ) {
        MaterialTheme(
            colorScheme = AetherColorScheme,
            typography = typography,
            shapes = AetherShapes,
            content = content,
        )
    }
}
