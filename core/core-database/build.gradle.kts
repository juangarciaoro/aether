plugins {
    alias(libs.plugins.aether.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.aether.core.database"
}

dependencies {
    api(libs.room.runtime)
    api(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.serialization.json)
    implementation(libs.coroutines.core)
}
