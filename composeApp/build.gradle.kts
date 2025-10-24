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
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.swift.klib)
    alias(libs.plugins.kotlin.cocoapods)
}

private object SwiftBindings{
    const val BINDINGS_NAME ="SwiftBindings"
    const val FOLDER_PATH = "../iosApp/iosApp/Bindings"
    const val PACKAGE_NAME = "com.kmpstarter.bindings"
}

swiftklib {
    create(SwiftBindings.BINDINGS_NAME) {
        path = file(SwiftBindings.FOLDER_PATH)
        packageName(SwiftBindings.PACKAGE_NAME)
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // cocoapods
    cocoapods {
        summary = "Shared module using cocoapods gradle plugin"
        homepage = "link"
        version = "1.0"
        ios.deploymentTarget = "18.6"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ComposeApp"
            isStatic = true
        }

        pod("Mixpanel") {
            version = "5.0.8"
            extraOpts += listOf("-compiler-option", "-fmodules")
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
        iosTarget.compilations {
            val main by getting {
                cinterops {
                    create(SwiftBindings.BINDINGS_NAME)
                }
            }
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

            // firebase
            implementation(project.dependencies.platform(libs.firebase.bom))

            // ktor
            implementation(libs.ktor.client.okhttp)

            // accompanist
            implementation(libs.accompanist.system.ui.controller)

            // mix panel
            implementation(libs.mixpanel.android)

        }
        commonMain.dependencies {
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

            // Serialization
            implementation(libs.kotlinx.serialization.json)


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

            // Database
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            // Logging
            api(libs.logging)

            // image libs
            implementation(libs.coil.compose)
            implementation(libs.calf.file.picker)

            // firebase
            implementation(libs.gitlive.firebase.firestore)
            implementation(libs.gitlive.firebase.analytics)
            implementation(libs.gitlive.firebase.auth)

            // google sign in
            implementation(libs.kmpauth.google)
            implementation(libs.kmpauth.firebase)

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
        versionCode = 1
        versionName = "1.0"
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

    // Database
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}