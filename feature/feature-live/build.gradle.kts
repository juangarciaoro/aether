plugins {
    alias(libs.plugins.aether.feature)
}

android {
    namespace = "com.aether.feature.live"
}

dependencies {
    implementation(project(":core:core-database"))
    implementation(project(":data:data-epg"))
}
