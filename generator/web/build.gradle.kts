import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl


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
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    // from build logic
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.build.koin.compose.get().pluginId)
    id(libs.plugins.build.common.get().pluginId)
    id(libs.plugins.build.compose.multiplatform.get().pluginId)
}

compose.resources {
    generateResClass = auto
}

kotlin {
    // adding this so that we don't get error because of build-logic plugins are applied
    androidLibrary {
        namespace = "com.kmpstarter.generator_web"
        compileSdk {
            version = release(version = libs.versions.android.compileSdk.get().toInt())
        }
        minSdk {
            version = release(libs.versions.android.minSdk.get().toInt())
        }
    }
    jvm()
    js {
        browser()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)
            api(projects.generator.domain)
            // add more deps
        }

        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }

    }

}