plugins {
    alias(libs.plugins.aether.library)
}

android {
    namespace = "com.aether.core.common"
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
}
