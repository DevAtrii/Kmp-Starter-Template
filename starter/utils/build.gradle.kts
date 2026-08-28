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
    id(libs.plugins.build.koin.core.get().pluginId)
    id(libs.plugins.build.common.get().pluginId)
    alias(libs.plugins.kotlin.serialization)

}

kotlin {

    android {
        namespace = "com.kmpstarter.utils"
        compileSdk {
            version = release(libs.versions.android.compileSdk.get().toInt())
        }
        minSdk {
            version = release(libs.versions.android.minSdk.get().toInt())
        }
    }


    val xcfName = "starter:utilsKit"



    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }


    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                // Data Storage
                implementation(libs.datastore.preferences)
                implementation(libs.atomic.fu)
                // Date & Time
                implementation(libs.kotlinx.datetime)
                // Logging
                api(libs.logging)
                // bindings
                implementation(projects.starter.native.bindings)

            }
        }


        androidMain {
            dependencies {
                implementation(libs.androidx.activity.ktx)
                implementation(libs.androidx.core.ktx)
                api(libs.androidx.startup)
            }
        }

        iosMain {
            dependencies {

            }
        }
    }

}