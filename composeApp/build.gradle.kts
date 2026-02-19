/*
 *
 *  *
 *  *  * Copyright (c) 2025
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
            implementation(projects.starter.coreDb)
            // ui
            api(projects.starter.ui.utils)
            implementation(projects.starter.ui.components)
            implementation(projects.starter.ui.layouts)
            // features
            implementation(projects.starter.features.analytics)
            api(projects.starter.features.purchases)
            // notifications
            implementation(projects.starter.notifications)

            // Navigation
            implementation(projects.features.navigation)
            // remote config
            implementation(projects.features.remoteConfig.data)
            implementation(projects.features.remoteConfig.domain)
            implementation(projects.features.remoteConfig.presentation)

        }
        iosMain.dependencies {

        }
    }
}


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
            .replace(Regex("CURRENT_PROJECT_VERSION\\s*=\\s*\\d+"), "CURRENT_PROJECT_VERSION=$projectVersion")
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




















