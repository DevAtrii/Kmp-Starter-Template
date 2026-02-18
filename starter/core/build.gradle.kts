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

    androidLibrary {
        namespace = "com.kmpstarter.core"
        compileSdk = 36
        minSdk = 24
    }


    val xcfName = "starter:coreKit"



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
                // utils
                api(projects.starter.utils)
                // other
                implementation(libs.atomic.fu)
                implementation(libs.datastore.preferences)
            }
        }

        androidMain {
            dependencies {
                // google play services
                implementation(libs.play.app.review.ktx)
                implementation(libs.kotlinx.coroutines.play.services)
            }
        }


        iosMain {
            dependencies {

            }
        }
    }

}