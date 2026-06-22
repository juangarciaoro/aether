plugins {
    alias(libs.plugins.aether.library)
}

android {
    namespace = "com.aether.data.m3u"
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.coroutines.core)
    implementation(project(":core:core-common"))
    implementation(project(":core:core-database"))
}
