plugins {
    alias(libs.plugins.aether.feature)
}

android {
    namespace = "com.aether.feature.home"
}

dependencies {
    implementation(project(":core:core-database"))
    implementation(project(":core:core-datastore"))
    implementation(project(":data:data-xtream"))
}
