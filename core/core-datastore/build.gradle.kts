plugins {
    alias(libs.plugins.aether.library)
}

android {
    namespace = "com.aether.core.datastore"
}

dependencies {
    api(libs.datastore.preferences)
    implementation(libs.coroutines.core)
    implementation(project(":core:core-common"))
}
