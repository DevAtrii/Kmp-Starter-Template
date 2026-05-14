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

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.vanniktech.maven.publish) apply false
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        if (path.startsWith(":starter:")) {
            pluginManager.apply("com.kmpstarter.plugins.starterlibrarypublish")
        }
    }
}

tasks.register("publishStarterLibrariesToLocalRepository") {
    group = "publishing"
    description =
        "Publishes all :starter:* KMP libraries to .starter-libs (adds a Maven repo under the project root; gitignored)."
    dependsOn(
        ":starter:core:publishAllPublicationsToStarterLocalRepository",
        ":starter:utils:publishAllPublicationsToStarterLocalRepository",
        ":starter:native:bindings:publishAllPublicationsToStarterLocalRepository",
        ":starter:ui:utils:publishAllPublicationsToStarterLocalRepository",
        ":starter:ui:components:publishAllPublicationsToStarterLocalRepository",
        ":starter:ui:layouts:publishAllPublicationsToStarterLocalRepository",
    )
}