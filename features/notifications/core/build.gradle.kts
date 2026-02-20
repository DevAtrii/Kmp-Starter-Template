plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    id(libs.plugins.build.koin.core.get().pluginId)
    id(libs.plugins.build.common.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {


    androidLibrary {
        androidResources.enable = true
        namespace = "com.kmpstarter.feature_notifications_core"
        compileSdk {
            version = release(version = libs.versions.android.compileSdk.get().toInt())
        }
        minSdk {
            version = release(libs.versions.android.minSdk.get().toInt())
        }
    }


    val xcfName = "starter:featureNotificationCoreKit"

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

                implementation(libs.alarmee.local)
                implementation(projects.starter.core)
                implementation(projects.features.resources)
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