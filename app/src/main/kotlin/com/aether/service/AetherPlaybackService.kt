package com.aether.service

import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AetherPlaybackService : MediaSessionService() {
    override fun onGetSession(controllerInfo: MediaSessionService.MediaSession.ControllerInfo?) = null
}
