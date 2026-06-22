plugins {
    `kotlin-dsl`
}

group = "com.aether.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.plugins.android.application.get().let { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" })
    compileOnly(libs.plugins.android.library.get().let { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" })
    compileOnly(libs.plugins.kotlin.android.get().let { "org.jetbrains.kotlin:kotlin-gradle-plugin:${it.version}" })
    compileOnly(libs.plugins.kotlin.compose.get().let { "org.jetbrains.kotlin:compose-compiler-gradle-plugin:${it.version}" })
    compileOnly(libs.plugins.hilt.get().let { "com.google.dagger:hilt-android-gradle-plugin:${it.version}" })
    compileOnly(libs.plugins.ksp.get().let { "com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${it.version}" })
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "aether.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "aether.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "aether.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}
