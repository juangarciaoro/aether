plugins {
    alias(libs.plugins.aether.feature)
}

android {
    namespace = "com.aether.feature.search"
}

dependencies {
    implementation(project(":core:core-database"))
}
