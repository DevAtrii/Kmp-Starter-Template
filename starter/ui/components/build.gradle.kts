plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    id(libs.plugins.build.compose.multiplatform.get().pluginId)
    id(libs.plugins.build.common.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

      androidLibrary {
        namespace = "com.kmpstarter.ui_components"
        compileSdk = 36
        minSdk = 24


    }

     val xcfName = "starter:ui:componentsKit"



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
                implementation(projects.starter.core)
                api(projects.starter.ui.utils)
                api(libs.coil.compose)
                api(libs.coil.svg)
                api(libs.coil.network.ktor)
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