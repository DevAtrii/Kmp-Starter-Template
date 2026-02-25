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
    id(libs.plugins.build.common.get().pluginId)
    id(libs.plugins.build.koin.compose.get().pluginId)
    id(libs.plugins.build.compose.multiplatform.get().pluginId)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.kmpstarter.feature_resources"
    generateResClass = auto
    this.always
}

kotlin {

    androidLibrary {
        androidResources.enable = true
        namespace = "com.kmpstarter.feature_resources"
        compileSdk {
            version = release(version = libs.versions.android.compileSdk.get().toInt())
        }
        minSdk {
            version = release(libs.versions.android.minSdk.get().toInt())
        }
    }


    val xcfName = "starter:featureResourcesKit"



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
                implementation(projects.starter.ui.components)
            }
        }

        androidMain {
            dependencies {

            }
        }


        iosMain {
            dependencies {

            }
        }
    }

}


// -----------------------------
// Task: Generate Compose Resource Accessors
// -----------------------------
val generateAccessors by tasks.registering {
    group = "resources"
    description = "Generates type-safe Compose Multiplatform resource accessors for the features module"

    // Make this task depend on all CMP-generated resource accessor tasks
    dependsOn(
        ":features:resources:generateResourceAccessorsForCommonMain",
        ":features:resources:generateResourceAccessorsForAndroidMain",
        ":features:resources:generateResourceAccessorsForAppleMain",
        ":features:resources:generateResourceAccessorsForIosArm64Main",
        ":features:resources:generateResourceAccessorsForIosSimulatorArm64Main",
        ":features:resources:generateResourceAccessorsForNativeMain"
    )

    doLast {
        println("✅ Compose resource accessors for resource module generated successfully!")
    }
}