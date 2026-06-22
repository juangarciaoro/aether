package com.aether.core.common.device

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration

enum class DeviceType { PHONE, TV, TABLET }

fun Context.getDeviceType(): DeviceType {
    val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    return when {
        uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION -> DeviceType.TV
        resources.configuration.smallestScreenWidthDp >= 600 -> DeviceType.TABLET
        else -> DeviceType.PHONE
    }
}
