package com.aether.feature.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aether.core.ui.components.AetherButton
import com.aether.core.ui.theme.NeonIndigo
import com.aether.core.ui.theme.Nebula
import com.aether.core.ui.theme.Void
import kotlinx.coroutines.delay

@Composable
fun OnboardingWelcomeScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showLogo by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showLogo = true
        delay(800)
        showTagline = true
        delay(600)
        showButton = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Void),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp),
        ) {
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn() + slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow,
                    ),
                    initialOffsetY = { it / 2 },
                ),
            ) {
                AetherLogoText()
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showTagline,
                enter = fadeIn(),
            ) {
                Text(
                    text = "Tu televisión. Sin límites.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Nebula,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            ) {
                AetherButton(
                    text = "Comenzar",
                    onClick = onGetStarted,
                )
            }
        }
    }
}

@Composable
private fun AetherLogoText() {
    Text(
        text = "AETHER",
        style = MaterialTheme.typography.displayMedium,
        color = NeonIndigo,
        textAlign = TextAlign.Center,
    )
}
