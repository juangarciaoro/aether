plugins {
    alias(libs.plugins.aether.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.aether.data.xtream"
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    implementation(libs.okhttp)
    implementation(libs.serialization.json)
    implementation(libs.coroutines.core)
    implementation(project(":core:core-common"))
    implementation(project(":core:core-network"))
    implementation(project(":core:core-database"))
}
