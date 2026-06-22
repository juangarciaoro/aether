pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "aether"

include(":app")

// Core modules
include(":core:core-common")
include(":core:core-ui")
include(":core:core-network")
include(":core:core-database")
include(":core:core-datastore")
include(":core:core-player")

// Data modules
include(":data:data-xtream")
include(":data:data-m3u")
include(":data:data-epg")

// Feature modules
include(":feature:feature-onboarding")
include(":feature:feature-home")
include(":feature:feature-live")
include(":feature:feature-epg")
include(":feature:feature-vod")
include(":feature:feature-series")
include(":feature:feature-player")
include(":feature:feature-search")
include(":feature:feature-settings")
