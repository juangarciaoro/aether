package com.aether.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.aether.core.common.device.DeviceType

@Composable
fun AetherNavHost(deviceType: DeviceType) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AetherDestination.Onboarding.route
    ) {
        // Navigation graph assembled from feature modules
        // Each feature registers its own composable destinations
    }
}

sealed class AetherDestination(val route: String) {
    data object Onboarding : AetherDestination("onboarding")
    data object Home : AetherDestination("home")
    data object Live : AetherDestination("live")
    data object Epg : AetherDestination("epg")
    data object Vod : AetherDestination("vod")
    data object Series : AetherDestination("series")
    data object Player : AetherDestination("player/{streamUrl}") {
        fun createRoute(streamUrl: String) = "player/$streamUrl"
    }
    data object Search : AetherDestination("search")
    data object Settings : AetherDestination("settings")
}
