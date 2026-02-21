plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.build.koin.compose.get().pluginId)
    id(libs.plugins.build.compose.multiplatform.get().pluginId)
    id(libs.plugins.build.common.get().pluginId)
}

val packageName = "com.atriidev.feature_purchases_presentation"
val xcfName = "FeaturePurchasesPresentationKit"

compose.resources {
    generateResClass = never
}

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
            implementation(projects.starter.ui.layouts)
            api(projects.features.purchases.domain)
            implementation(projects.features.resources)
            // analytics
            implementation(projects.features.analytics.domain)
        }

        androidMain.dependencies {
        }

        iosMain.dependencies {
        }
    }
}