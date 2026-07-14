import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

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

// Keep in sync with KmpLibraryPublishPlugin.PUBLISHABLE_MODULE_PATHS
private val publishableKmpModules =
    listOf(
        ":starter:core",
        ":starter:utils",
        ":starter:native:bindings",
        ":starter:ui:utils",
        ":starter:ui:components",
        ":starter:ui:layouts",
        ":features:navigation",
        ":features:analytics:data",
        ":features:analytics:domain",
        ":features:locale",
        ":features:remote_config:data",
        ":features:remote_config:domain",
        ":features:remote_config:presentation",
        ":features:purchases:data",
        ":features:purchases:domain",
        ":features:purchases:presentation",
        ":features:notifications:core",
        ":features:notifications:local",
        ":features:notifications:push",
    )

subprojects {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        if (project.path in publishableKmpModules) {
            pluginManager.apply("com.kmpstarter.plugins.kmplibrarypublish")
        }
        extensions.configure<KotlinMultiplatformExtension>{
            compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
                freeCompilerArgs.add("-Xcontext-parameters")
            }
        }
    }
}

tasks.register("publishKmpLibrariesToLocalRepository") {
    group = "publishing"
    description =
        "Publishes all KMP libraries (starter + features) to .starter-libs (gitignored Maven repo at project root)."
    dependsOn(
        publishableKmpModules.map { "$it:publishAllPublicationsToStarterLocalRepository" },
    )
}

tasks.register("publishKmpLibrariesToMavenCentral") {
    group = "publishing"
    description = "Publishes all KMP libraries (starter + features) to Maven Central."
    dependsOn(
        publishableKmpModules.map { "$it:publishAllPublicationsToMavenCentralRepository" },
    )
}

// Backward-compatible aliases
tasks.register("publishStarterLibrariesToLocalRepository") {
    group = "publishing"
    dependsOn("publishKmpLibrariesToLocalRepository")
}

tasks.register("publishAllKmpLibrariesToLocalRepository") {
    group = "publishing"
    dependsOn("publishKmpLibrariesToLocalRepository")
}
