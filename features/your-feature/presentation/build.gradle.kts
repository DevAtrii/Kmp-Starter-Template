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
    generateResClass = never
}

kotlin {

    androidLibrary {
        namespace = "com.kmpstarter.feature_your_feature_presentation"
        compileSdk {
            version = release(version = libs.versions.android.compileSdk.get().toInt())
        }
        minSdk {
            version = release(libs.versions.android.minSdk.get().toInt())
        }
    }


    val xcfName = "starter:featureYourFeaturePresentationKit"



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
                implementation(projects.starter.ui.layouts)
                implementation(projects.features.yourFeature.domain)
                implementation(projects.features.core.presentation)
                implementation(projects.features.resources)
                implementation(projects.features.navigation)
                implementation(projects.features.analytics.domain)
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