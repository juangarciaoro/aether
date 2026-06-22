plugins {
    alias(libs.plugins.aether.feature)
}

android {
    namespace = "com.aether.feature.onboarding"
}

dependencies {
    implementation(project(":core:core-datastore"))
    implementation(project(":data:data-xtream"))
    implementation(project(":data:data-m3u"))
    implementation(project(":core:core-database"))
}
