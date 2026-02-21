plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.build.koin.core.get().pluginId)
    id(libs.plugins.build.common.get().pluginId)
}

val packageName = "com.atriidev.feature_purchases_domain"
val xcfName = "FeaturePurchasesDomainKit"


kotlin {
    androidLibrary {
        namespace = packageName
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    iosArm64()
    iosSimulatorArm64()



    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = xcfName
            freeCompilerArgs += "-Xbinary=bundleId=$packageName"
        }
    }


    sourceSets {
        commonMain.dependencies {
            implementation(projects.starter.core)
        }

        androidMain.dependencies {
        }

        iosMain.dependencies {
        }
    }
}