plugins {
    alias(libs.plugins.aether.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.aether.core.network"
}

dependencies {
    api(libs.retrofit)
    api(libs.retrofit.serialization)
    api(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.serialization.json)
    implementation(libs.coroutines.core)
}
