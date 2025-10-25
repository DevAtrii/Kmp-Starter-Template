plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.swift.klib)
    id(libs.plugins.build.common.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

private object SwiftBindings{
    const val BINDINGS_NAME ="SwiftBindings"
    const val FOLDER_PATH = "src/native/swift/com/kmpstarter/native_bindings"
    const val PACKAGE_NAME = "com.kmpstarter.native_bindings"
}

swiftklib {
    create(SwiftBindings.BINDINGS_NAME) {
        path = file(SwiftBindings.FOLDER_PATH)
        packageName(SwiftBindings.PACKAGE_NAME)
    }
}

kotlin {


    androidLibrary {
        namespace = "com.kmpstarter.native_bindings"
        compileSdk = 36
        minSdk = 24
    }

    val xcfName = "starter:native:bindingsKit"

   listOf(
       iosArm64(),
       iosSimulatorArm64()
   ).forEach { iosTarget->
       iosTarget.compilations {
           val main by getting {
               cinterops {
                   create(SwiftBindings.BINDINGS_NAME)
               }
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
                // Add KMP dependencies here
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