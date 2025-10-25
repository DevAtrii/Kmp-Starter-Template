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

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// helper function for version
private fun getVersionCode(): Int {
    val versionMajor = libs.versions.app.version.major.get().toInt()
    val versionMinor = libs.versions.app.version.minor.get().toInt()
    val versionPatch = libs.versions.app.version.patch.get().toInt()
    return versionMajor * 10000 + versionMinor * 100 + versionPatch
}

private fun getVersionName(): String {
    val versionMajor = libs.versions.app.version.major.get().toInt()
    val versionMinor = libs.versions.app.version.minor.get().toInt()
    val versionPatch = libs.versions.app.version.patch.get().toInt()
    return "$versionMajor.$versionMinor.$versionPatch"
}

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.build.common.get().pluginId)
}



kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
    androidTarget {
        compilerOptions {
            jvmTarget.set(CommonPlugin.JVM_VERSION)
        }
    }

    // cocoapods
    cocoapods {
        summary = "Shared module using cocoapods gradle plugin"
        homepage = "link"
        version = libs.versions.cocoapods.fw.get()
        ios.deploymentTarget = libs.versions.cocoapods.ios.get()
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ComposeApp"
            isStatic = true
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
            // Compose UI
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)


            // Coroutines
            implementation(libs.kotlinx.coroutines.android)


            // Dependency Injection
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            // Google Play Services
            implementation(libs.play.app.update.ktx)
            implementation(libs.play.app.review.ktx)
            implementation(libs.kotlinx.coroutines.play.services)


            // ktor
            implementation(libs.ktor.client.okhttp)

            // accompanist
            implementation(libs.accompanist.system.ui.controller)

            // mix panel
            implementation(libs.mixpanel.android)

        }
        commonMain.dependencies {
            // local modules
            implementation(projects.starter.core)
            implementation(projects.starter.coreDb)
            // ui
            implementation(projects.starter.ui.utils)
            implementation(projects.starter.ui.components)
            implementation(projects.starter.ui.layouts)
            // features
            implementation(projects.starter.features.analytics)
            implementation(projects.starter.features.purchases)
            // notifications
            implementation(projects.starter.notifications)


            // Compose Core UI
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            // ktor
            implementation(libs.ktor.client.core)
            // ktor plugins
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            // AndroidX Libraries
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // Navigation
            implementation(libs.navigation.compose)



            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // Date & Time
            implementation(libs.kotlinx.datetime)

            // RevenueCat Purchases
            implementation(libs.purchases.core)


            // Dependency Injection (Koin)
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)


            // Data Storage
            implementation(libs.datastore.preferences)
            implementation(libs.stately.common)
            implementation(libs.atomic.fu)

            // Logging
            api(libs.logging)

            // image libs
            implementation(libs.coil.compose)
            implementation(libs.calf.file.picker)

            // backhandler
            implementation(libs.ui.backhandler)

            // notifications
            implementation(libs.alarmee)
        }
        commonTest.dependencies {
            // Testing Framework
            implementation(libs.kotlin.test)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            // ktor
            implementation(libs.ktor.client.darwin)
        }
        named { it.lowercase().startsWith("ios") }.configureEach {
            languageSettings {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }
}

android {
    namespace = "com.kmpstarter"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        buildFeatures {
            buildConfig = true
        }
        applicationId = "com.kmpstarter"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = getVersionCode()
        versionName = getVersionName()

        val buildMessage = "versionCode: $versionCode, versionName: $versionName"
        println(buildMessage)
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            /*Todo set this to true in prod*/
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    // Debug Tools
    debugImplementation(compose.uiTooling)

}
