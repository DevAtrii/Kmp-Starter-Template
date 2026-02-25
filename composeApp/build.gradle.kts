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

// helper function for version


plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    // conviction plugins
    id(libs.plugins.build.common.get().pluginId)
    id(libs.plugins.build.compose.multiplatform.get().pluginId)
    id(libs.plugins.build.koin.compose.get().pluginId)
}

compose.resources {
    generateResClass = never
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
    androidLibrary {
        namespace = "com.kmpstarter"
        compileSdk {
            version = release(version = libs.versions.android.compileSdk.get().toInt())
        }
        minSdk {
            version = release(libs.versions.android.minSdk.get().toInt())
        }
        androidResources {
            enable = true
        }
    }


    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            // Required when using NativeSQLiteDriver
            linkerOpts.add("-lsqlite3")
        }
    }

    sourceSets {

        androidMain.dependencies {
        }
        commonMain.dependencies {
            // local modules
            api(projects.starter.core)
            // database
            implementation(projects.features.database)
            // ui
            api(projects.starter.ui.utils)
            implementation(projects.starter.ui.components)
            implementation(projects.starter.ui.layouts)
            // analytics
            implementation(projects.features.analytics.data)
            implementation(projects.features.analytics.domain)
            // purchases
            implementation(projects.features.purchases.data)
            implementation(projects.features.purchases.domain)
            implementation(projects.features.purchases.presentation)
            // notifications
            implementation(projects.features.notifications.core)
            implementation(projects.features.notifications.local)
            implementation(projects.features.notifications.push)
            // Navigation
            implementation(projects.features.navigation)
            // remote config
            implementation(projects.features.remoteConfig.data)
            implementation(projects.features.remoteConfig.domain)
            implementation(projects.features.remoteConfig.presentation)
            // resources
            implementation(projects.features.resources)
            // Feature Core
            implementation(projects.features.core.data)
            implementation(projects.features.core.domain)
            implementation(projects.features.core.presentation)

            // Feature Your Feature
            implementation(projects.features.yourFeature.data)
            implementation(projects.features.yourFeature.domain)
            implementation(projects.features.yourFeature.presentation)

        }
        iosMain.dependencies {

        }
    }
}

@Deprecated("moved this to build phase inside project.pbxproj")
val setXcodeTargetVersion by tasks.registering {
    val xcconfigFile = File(project.rootDir, "iosApp/AppConfig.xcconfig")

    // Read version from libs.versions.toml
    val versionMajor = libs.versions.app.version.major.get().toInt()
    val versionMinor = libs.versions.app.version.minor.get().toInt()
    val versionPatch = libs.versions.app.version.patch.get().toInt()

    val projectVersion = (versionMajor * 10000) + (versionMinor * 100) + versionPatch
    val marketingVersion = "$versionMajor.$versionMinor.$versionPatch"

    doLast {
        if (!xcconfigFile.exists()) {
            throw GradleException("AppConfig.xcconfig not found at ${xcconfigFile.absolutePath}")
        }

        val content = xcconfigFile.readText()
            // Replace CURRENT_PROJECT_VERSION
            .replace(
                Regex("CURRENT_PROJECT_VERSION\\s*=\\s*\\d+"),
                "CURRENT_PROJECT_VERSION=$projectVersion"
            )
            // Replace MARKETING_VERSION
            .replace(Regex("MARKETING_VERSION\\s*=\\s*.+"), "MARKETING_VERSION=$marketingVersion")

        xcconfigFile.writeText(content)

        println("Updated AppConfig.xcconfig: CURRENT_PROJECT_VERSION=$projectVersion, MARKETING_VERSION=$marketingVersion")
    }
}
//
//// Make sure it runs before the Xcode build
//tasks.named("embedAndSignAppleFrameworkForXcode") {
//    dependsOn(setXcodeTargetVersion)
//}




















