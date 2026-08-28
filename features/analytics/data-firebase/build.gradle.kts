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
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.build.common.get().pluginId)
}

kotlin {

    android {
        namespace = "com.kmpstarter.feature_analytics_data_firebase"
        compileSdk {
            version = release(version = libs.versions.android.compileSdk.get().toInt())
        }
        minSdk {
            version = release(libs.versions.android.minSdk.get().toInt())
        }
    }


    val xcfName = "starter:featureAnalyticsDataFirebaseKit"

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
    }



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
                api(projects.features.analytics.domain)
                api(projects.starter.core)
                implementation(libs.gitlive.firebase.analytics)
            }
        }

        androidMain {
            dependencies {
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation("com.google.firebase:firebase-analytics")
            }
        }


        iosMain {
            dependencies {

            }
        }
    }

}