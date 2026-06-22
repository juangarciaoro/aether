package com.aether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aether.core.common.device.DeviceType
import com.aether.core.common.device.getDeviceType
import com.aether.core.ui.theme.AetherTheme
import com.aether.navigation.AetherNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val deviceType = getDeviceType()

        setContent {
            AetherTheme(deviceType = deviceType) {
                AetherNavHost(deviceType = deviceType)
            }
        }
    }
}
