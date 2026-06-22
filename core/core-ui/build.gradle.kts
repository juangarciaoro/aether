plugins {
    alias(libs.plugins.aether.library)
}

android {
    namespace = "com.aether.core.ui"
}

dependencies {
    api(platform(libs.compose.bom))
    api(libs.compose.ui)
    api(libs.compose.ui.tooling.preview)
    api(libs.compose.material3)
    api(libs.compose.material.icons)
    api(libs.compose.animation)
    api(libs.compose.foundation)
    api(libs.compose.tv.foundation)
    api(libs.compose.tv.material)
    api(libs.lifecycle.viewmodel.compose)
    api(libs.lifecycle.runtime.compose)
    api(libs.navigation.compose)
    api(libs.hilt.navigation.compose)
    implementation(libs.palette)
    implementation(libs.compose.ui.google.fonts)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    debugImplementation(libs.compose.ui.tooling)
    implementation(project(":core:core-common"))
}
