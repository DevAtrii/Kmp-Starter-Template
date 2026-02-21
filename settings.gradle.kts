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
include(":starter:features:purchases")
include(":androidApp")
include(":features:navigation")
include(":features:core:domain")
include(":features:core:data")
include(":features:core:presentation")
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
