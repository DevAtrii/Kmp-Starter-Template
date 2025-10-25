/*
 *
 *  *
 *  *  * Copyright (c) 2025
 *  *  *
 *  *  * Author: Athar Gul
 *  *  * GitHub: https://github.com/DevAtrii/Kmp-Starter-Template
 *  *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *  *
 *  *  * All rights reserved.
 *  *
 *  *
 *
 */

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KoinComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            with(pluginManager) {
                apply("com.kmpstarter.plugins.koin")
            }

            val kotlinMultiplatformExtension = extensions.getByType<KotlinMultiplatformExtension>()
            with(kotlinMultiplatformExtension) {
                with(sourceSets) {
                    commonMain {
                        dependencies {
                            // Dependency Injection (Koin)
                            api(libs.findLibrary("koin-compose").get())
                            api(libs.findLibrary("koin-compose-viewmodel").get())
                        }
                    }
                    androidMain {
                        // Compose UI
                        dependencies {
                            implementation(libs.findLibrary("koin-androidx-compose").get())
                        }
                    }
                }
            }
        }
    }
}
