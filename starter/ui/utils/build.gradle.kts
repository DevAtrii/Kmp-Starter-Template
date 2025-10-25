plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    id(libs.plugins.build.compose.multiplatform.get().pluginId)
    id(libs.plugins.build.koin.compose.get().pluginId)
    id(libs.plugins.build.common.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
    androidLibrary {
        namespace = "com.kmpstarter.ui_utils"
        compileSdk = 36
        minSdk = 24
        this.compilerOptions {
            jvmTarget.set(CommonPlugin.JVM_VERSION)
        }
    }


    val xcfName = "starter:ui:utilsKit"


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
                // Add KMP dependencies here
                api(projects.starter.core)
                implementation(libs.coil.compose)
                implementation(libs.navigation.compose)
                implementation(libs.kotlinx.datetime)
                // kotlin
                implementation(libs.kotlinx.serialization.json)
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