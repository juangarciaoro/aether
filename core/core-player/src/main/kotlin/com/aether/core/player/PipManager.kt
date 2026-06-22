package com.aether.core.player

import android.app.Activity
import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational
import androidx.annotation.RequiresApi

class PipManager(private val activity: Activity) {

    val isPipSupported: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            activity.packageManager.hasSystemFeature("android.software.picture_in_picture")

    @RequiresApi(Build.VERSION_CODES.O)
    fun enterPip(aspectRatio: Rational = Rational(16, 9)) {
        val paramsBuilder = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            paramsBuilder.setSeamlessResizeEnabled(true)
        }

        activity.enterPictureInPictureMode(paramsBuilder.build())
    }

    fun isInPipMode(): Boolean = activity.isInPictureInPictureMode
}
