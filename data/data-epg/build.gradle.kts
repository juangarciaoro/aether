plugins {
    alias(libs.plugins.aether.library)
}

android {
    namespace = "com.aether.data.epg"
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.coroutines.core)
    implementation(libs.workmanager)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)
    implementation(project(":core:core-common"))
    implementation(project(":core:core-database"))
}
