plugins {
    alias(libs.plugins.aether.feature)
}

android {
    namespace = "com.aether.feature.settings"
}

dependencies {
    implementation(project(":core:core-datastore"))
    implementation(project(":core:core-database"))
    implementation(project(":data:data-epg"))
}
