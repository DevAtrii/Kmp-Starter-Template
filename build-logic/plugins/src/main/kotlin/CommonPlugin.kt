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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension


class CommonPlugin : Plugin<Project> {
    companion object {
        val JVM_VERSION = JvmTarget.JVM_17
    }

    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            val kmp = extensions.getByType<KotlinMultiplatformExtension>()
//            with(pluginManager) {
//                val version = libs.findVersion("kotlin").get().requiredVersion
//                apply("org.jetbrains.kotlin.plugin.serialization:$version")
//            }
            with(kmp) {
                with(sourceSets) {
                    commonMain {
                        dependencies {
                            // Dependency Injection (Koin)
                            implementation(libs.findLibrary("kotlinx-serialization-json").get())
                            implementation(libs.findLibrary("kotlinx.coroutines.core").get())
                        }
                    }
                    androidMain{
                        dependencies{
                            implementation(libs.findLibrary("kotlinx.coroutines.android").get())
                        }
                    }
                }

            }
        }
    }
}
