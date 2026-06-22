plugins {
    alias(libs.plugins.aether.application)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.aether"

    defaultConfig {
        applicationId = "com.aether.iptv"
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("aether.keystore").takeIf { it.exists() }
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias = "aether"
            keyPassword = System.getenv("KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            isDebuggable = true
        }
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.splashscreen)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    ksp(libs.hilt.compiler)

    // Core modules
    implementation(project(":core:core-common"))
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-network"))
    implementation(project(":core:core-database"))
    implementation(project(":core:core-datastore"))
    implementation(project(":core:core-player"))

    // Data modules
    implementation(project(":data:data-xtream"))
    implementation(project(":data:data-m3u"))
    implementation(project(":data:data-epg"))

    // Feature modules
    implementation(project(":feature:feature-onboarding"))
    implementation(project(":feature:feature-home"))
    implementation(project(":feature:feature-live"))
    implementation(project(":feature:feature-epg"))
    implementation(project(":feature:feature-vod"))
    implementation(project(":feature:feature-series"))
    implementation(project(":feature:feature-player"))
    implementation(project(":feature:feature-search"))
    implementation(project(":feature:feature-settings"))
}
