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
private val publishableStarterModules =
    listOf(
        ":starter:core",
        ":starter:utils",
        ":starter:native:bindings",
        ":starter:ui:utils",
        ":starter:ui:components",
        ":starter:ui:layouts",
    )

private val publishableFeaturesBaseModules =
    listOf(
        ":features:analytics:data",
        ":features:analytics:domain",
        ":features:core_app:data",
        ":features:core_app:domain",
        ":features:locale",
        ":features:remote_config:data",
        ":features:remote_config:domain",
        ":features:purchases:data",
        ":features:purchases:domain",
        ":features:notifications:core",
        ":features:notifications:local",
        ":features:notifications:push",
    )

private val publishableFeaturesUiModules =
    listOf(
        ":features:navigation",
        ":features:core_app:presentation",
        ":features:remote_config:presentation",
        ":features:purchases:presentation",
    )

private val publishableKmpModules =
    publishableStarterModules + publishableFeaturesBaseModules + publishableFeaturesUiModules

subprojects {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        if (project.path in publishableKmpModules) {
            pluginManager.apply("com.kmpstarter.plugins.kmplibrarypublish")
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

fun publishAllToMavenCentralTaskName(modules: List<String>) =
    modules.map { "$it:publishAllPublicationsToMavenCentralRepository" }

tasks.register("publishKmpLibrariesStarterShardToMavenCentral") {
    group = "publishing"
    description = "CI shard: starter modules → Maven Central."
    dependsOn(publishAllToMavenCentralTaskName(publishableStarterModules))
}

tasks.register("publishKmpLibrariesFeaturesBaseShardToMavenCentral") {
    group = "publishing"
    description = "CI shard: feature data/domain/infra modules → Maven Central."
    dependsOn(publishAllToMavenCentralTaskName(publishableFeaturesBaseModules))
}

tasks.register("publishKmpLibrariesFeaturesUiShardToMavenCentral") {
    group = "publishing"
    description = "CI shard: feature presentation/navigation modules → Maven Central."
    dependsOn(publishAllToMavenCentralTaskName(publishableFeaturesUiModules))
}

tasks.register("publishKmpLibrariesToMavenCentral") {
    group = "publishing"
    description = "Publishes all KMP libraries (starter + features) to Maven Central."
    dependsOn(
        publishAllToMavenCentralTaskName(publishableKmpModules),
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
