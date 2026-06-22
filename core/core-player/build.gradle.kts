plugins {
    alias(libs.plugins.aether.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.aether.core.player"
    buildFeatures.compose = true
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    api(libs.media3.exoplayer)
    api(libs.media3.exoplayer.hls)
    api(libs.media3.exoplayer.dash)
    api(libs.media3.ui)
    api(libs.media3.session)
    implementation(libs.media3.datasource.okhttp)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(project(":core:core-common"))
    implementation(project(":core:core-datastore"))
}
