import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlin.serialization)
    // from build logic
    id(libs.plugins.build.koin.core.get().pluginId)
    id(libs.plugins.build.common.get().pluginId)
}

kotlin {

    androidLibrary {
        namespace = "com.kmpstarter.core_db"
        compileSdk = 36
        minSdk = 24
    }


    val xcfName = "starter:core_dbKit"


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
                // Database
                implementation(libs.room.runtime)
                implementation(libs.sqlite.bundled)
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
dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
