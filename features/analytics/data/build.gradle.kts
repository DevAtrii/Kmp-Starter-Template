@file:OptIn(ExperimentalSpmForKmpFeature::class)

import io.github.frankois944.spmForKmp.swiftPackageConfig
import io.github.frankois944.spmForKmp.utils.ExperimentalSpmForKmpFeature

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.spm.kmp)
    // from build logic
    id(libs.plugins.build.koin.core.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.build.common.get().pluginId)
}

kotlin {

    androidLibrary {
        namespace = "com.kmpstarter.feature_analytics_data"
        compileSdk {
            version = release(version = libs.versions.android.compileSdk.get().toInt())
        }
        minSdk {
            version = release(libs.versions.android.minSdk.get().toInt())
        }
    }


    val xcfName = "starter:featureAnalyticsDataKit"

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.swiftPackageConfig(cinteropName = "interop") {
            dependency {
                val mixPanel = libs.ios.mixpanel.swift.get()
                remotePackageVersion(
                    url = uri(mixPanel.group),
                    products = {
                        add(mixPanel.name)
                    },
                    version = mixPanel.version!!,
                )
            }
        }
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
            }
        }

        androidMain {
            dependencies {
                implementation(libs.mixpanel.android)
            }
        }


        iosMain {
            dependencies {

            }
        }
    }

}