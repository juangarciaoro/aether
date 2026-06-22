plugins {
    alias(libs.plugins.aether.feature)
}

android {
    namespace = "com.aether.feature.player"
}

dependencies {
    implementation(project(":core:core-player"))
    api(project(":core:core-database"))
    implementation(project(":core:core-datastore"))
    implementation(project(":data:data-epg"))
}
