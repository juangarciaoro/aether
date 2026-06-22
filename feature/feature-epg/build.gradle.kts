plugins {
    alias(libs.plugins.aether.feature)
}

android {
    namespace = "com.aether.feature.epg"
}

dependencies {
    implementation(project(":core:core-database"))
    implementation(project(":data:data-epg"))
}
