package com.aether.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aether.core.common.device.DeviceType
import com.aether.feature.epg.EpgScreen
import com.aether.feature.home.HomeScreen
import com.aether.feature.live.LiveScreen
import com.aether.feature.onboarding.OnboardingWelcomeScreen
import com.aether.feature.onboarding.ProviderSetupScreen
import com.aether.feature.player.PlayerScreen
import com.aether.feature.search.SearchScreen
import com.aether.feature.series.SeriesDetailScreen
import com.aether.feature.series.SeriesScreen
import com.aether.feature.settings.SettingsScreen
import com.aether.feature.vod.VodDetailScreen
import com.aether.feature.vod.VodScreen

private const val ARG_STREAM_URL = "streamUrl"

@Composable
fun AetherNavHost(
    deviceType: DeviceType,
    onPlayerVisibilityChange: (Boolean) -> Unit = {},
    viewModel: NavViewModel = hiltViewModel(),
) {
    val onboardingComplete by viewModel.onboardingComplete.collectAsState(initial = false)
    val navController = rememberNavController()

    if (!onboardingComplete) {
        OnboardingFlow(
            navController = navController,
            onComplete = { viewModel.setOnboardingComplete() },
        )
    } else {
        MainAppFlow(
            navController = navController,
            deviceType = deviceType,
            onPlayerVisibilityChange = onPlayerVisibilityChange,
        )
    }
}

@Composable
private fun OnboardingFlow(
    navController: NavHostController,
    onComplete: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = "onboarding_welcome",
    ) {
        composable("onboarding_welcome") {
            OnboardingWelcomeScreen(
                onGetStarted = { navController.navigate("onboarding_setup") },
            )
        }
        composable("onboarding_setup") {
            ProviderSetupScreen(
                onProviderAdded = onComplete,
            )
        }
    }
}

@Composable
private fun MainAppFlow(
    navController: NavHostController,
    deviceType: DeviceType,
    onPlayerVisibilityChange: (Boolean) -> Unit,
) {
    val bottomNavItems = listOf(
        BottomNavItem("home", Icons.Rounded.Home, "Inicio"),
        BottomNavItem("live", Icons.Rounded.LiveTv, "En Directo"),
        BottomNavItem("epg", Icons.Rounded.Tv, "Guía"),
        BottomNavItem("vod", Icons.Rounded.Movie, "Películas"),
        BottomNavItem("search", Icons.Rounded.Search, "Buscar"),
        BottomNavItem("settings", Icons.Rounded.Settings, "Ajustes"),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isPlayerRoute = currentRoute?.startsWith("player") == true
    val showBottomBar = !isPlayerRoute

    androidx.compose.runtime.LaunchedEffect(isPlayerRoute) {
        onPlayerVisibilityChange(isPlayerRoute)
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar && deviceType != DeviceType.TV) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("home") {
                HomeScreen(
                    deviceType = deviceType,
                    onChannelClick = { channelId ->
                        navController.navigate("player/${channelId}")
                    },
                    onVodClick = { vodId ->
                        navController.navigate("vod_detail/${vodId}")
                    },
                )
            }
            composable("live") {
                LiveScreen(
                    onChannelClick = { channelId ->
                        navController.navigate("player/${channelId}")
                    },
                )
            }
            composable("epg") {
                EpgScreen(
                    onProgramClick = { program ->
                        navController.navigate("player/${program.channelTvgId}")
                    },
                )
            }
            composable("vod") {
                VodScreen(
                    onVodClick = { vodId ->
                        navController.navigate("vod_detail/$vodId")
                    },
                )
            }
            composable(
                route = "vod_detail/{vodId}",
                arguments = listOf(navArgument("vodId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val vodId = backStackEntry.arguments?.getString("vodId") ?: return@composable
                VodDetailScreen(
                    vodId = vodId,
                    onPlay = { streamUrl -> navController.navigate("player/$streamUrl") },
                    onBack = { navController.popBackStack() },
                )
            }
            composable("series") {
                SeriesScreen(
                    onSeriesClick = { seriesId ->
                        navController.navigate("series_detail/$seriesId")
                    },
                )
            }
            composable(
                route = "series_detail/{seriesId}",
                arguments = listOf(navArgument("seriesId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val seriesId = backStackEntry.arguments?.getString("seriesId") ?: return@composable
                SeriesDetailScreen(
                    seriesId = seriesId,
                    onEpisodeClick = { episodeUrl ->
                        navController.navigate("player/$episodeUrl")
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable("search") {
                SearchScreen(
                    onChannelClick = { navController.navigate("player/$it") },
                    onVodClick = { navController.navigate("player/$it") },
                    onSeriesClick = { navController.navigate("series/$it") },
                )
            }
            composable("settings") {
                SettingsScreen(
                    onManageProviders = { navController.navigate("settings_providers") },
                )
            }
            composable(
                route = "player/{$ARG_STREAM_URL}",
                arguments = listOf(navArgument(ARG_STREAM_URL) { type = NavType.StringType }),
            ) { backStackEntry ->
                val streamUrl = backStackEntry.arguments?.getString(ARG_STREAM_URL) ?: return@composable
                PlayerScreen(
                    streamUrl = streamUrl,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
)
