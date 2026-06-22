plugins {
    alias(libs.plugins.aether.feature)
}

android {
    namespace = "com.aether.feature.series"
}

dependencies {
    implementation(project(":core:core-database"))
    implementation(project(":data:data-xtream"))
}
