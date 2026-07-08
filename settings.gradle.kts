/*
 *
 *  *
 *  *  * Copyright (c) 2026
 *  *  *
 *  *  * Author: Athar Gul
 *  *  * GitHub: https://github.com/DevAtrii/Kmp-Starter-Template
 *  *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *  *
 *  *  * All rights reserved.
 *  *
 *  *
 *
 */

rootProject.name = "KmpStarter"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        val starterLibsDir = rootDir.resolve(".starter-libs")
        if (starterLibsDir.exists()) {
            maven(starterLibsDir.toURI()) {
                name = "starterLibsLocal"
                content {
                    includeGroup("io.github.devatrii")
                }
            }
        }
    }
}

includeBuild("build-logic")
include(":composeApp")
include(":starter:core")
include(":starter:utils")
include(":starter:native:bindings")
include(":starter:ui:utils")
include(":starter:ui:components")
include(":starter:ui:layouts")
include(":androidApp")
include(":features:navigation")
include(":features:core_app:domain")
include(":features:core_app:data")
include(":features:core_app:presentation")
include(":features:remote_config:domain")
include(":features:remote_config:data")
include(":features:remote_config:presentation")
include(":features:resources")
include(":features:notifications:core")
include(":features:notifications:local")
include(":features:notifications:push")
include(":features:analytics:domain")
include(":features:analytics:data")
include(":features:database")
include(":features:purchases:data")
include(":features:purchases:domain")
include(":features:purchases:presentation")
/*Your Feature*/
include(":features:your-feature:presentation")
include(":features:your-feature:domain")
include(":features:your-feature:data")
include(":features:locale")
// tooling
include(":generator:data")
include(":generator:domain")
include(":generator:cli")
include(":generator:web")
