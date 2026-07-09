/*
 *
 *  *
 *  *  * Copyright (c) 2026
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

package com.kmpstarter.generator_domain

import com.kmpstarter.generator_domain.StarterModules.Starter.Native
import kotlinx.serialization.Serializable

enum class ProjectMode {
    MODULE,
    LIB
}

@Serializable
data class StarterProject(
    val workingDir: String = ".",
    val projectName: String,
    val packageName: String,
    val mode: ProjectMode = ProjectMode.LIB,
    val featureName: String? = null,
    val includeWorkflows: Boolean = true,
    val modules: List<StarterModules> = StarterModules.all(),
) {
    fun getFeatureNameAsPascalCasing(): String =
        featureName
            ?.split('_', '-', ' ')
            ?.filter { it.isNotBlank() }
            ?.joinToString("") { part ->
                part.replaceFirstChar(Char::uppercase)
            }
            .orEmpty()
}

interface BaseModule {

    val packageName: String

    fun dependencies(): List<StarterModules>

    fun moduleGradleDep(mode: ProjectMode): String {
        val segments = this::class.qualifiedName!!
            .replace("com.kmpstarter.generator_domain.StarterModules.", "")
            .split('.')

        val raw = if (mode == ProjectMode.LIB) {
            segments.joinToString(".") { it.lowercase() }
                .replace("features", "feature")
                .let { path ->
                    if (path.startsWith("starter.")) path.removePrefix("starter.")
                    else path
                }
        } else {
            segments.joinToString(".") { segment ->
                segment.replaceFirstChar(Char::lowercaseChar)
            }
        }

        val prefix = when (mode) {
            ProjectMode.MODULE -> "projects"
            ProjectMode.LIB -> "libs.starter"
        }
        return "$prefix.$raw"
    }

    fun moduleFilePath(): String {
        val clazz = this::class
        val path = clazz.qualifiedName!!
            .replace("com.kmpstarter.generator_domain.StarterModules.", "")
            .replace(".", "/")
            .lowercase()
        return path
    }

    fun moduleGradlePath(): String {
        val clazz = this::class
        val path = clazz.qualifiedName!!
            .replace("com.kmpstarter.generator_domain.StarterModules.", "")
            .replace(".", ":")
            .lowercase()
        return ":$path"
    }


    fun koinModules(): List<String> {
        throw NotImplementedError()
    }

    fun mavenArtifactId(): String {
        val libDep = moduleGradleDep(ProjectMode.LIB).removePrefix("libs.starter.")
        val artifact = libDep.replace('.', '-')
        return if (libDep.startsWith("feature.")) artifact else "starter-$artifact"
    }
}

fun main() {
    val module = StarterModules.Starter.Core
    println("\n\n=====MODULE=====")
    println(module::class.qualifiedName)
    println(module.moduleGradleDep(ProjectMode.LIB))
    println(module.moduleFilePath())
    println(module.moduleGradlePath())
    println(module.packageName)
}

class ModuleOnlyException(
    message: String = "This module can only be added as a module.",
) : IllegalStateException(message)

@Serializable
sealed class StarterModules : BaseModule {

    companion object {
        fun all() = listOf(
            /** Features **/

            // Analytics
            Features.Analytics.Data,
            Features.Analytics.Domain,

            // Core
            Features.Core.Data,
            Features.Core.Domain,
            Features.Core.Presentation,

            // Database
            Features.Database,

            // Locale
            Features.Locale,

            // Navigation
            Features.Navigation,

            // Notifications
            Features.Notifications.Core,
            Features.Notifications.Local,
            Features.Notifications.Push,

            // Purchases
            Features.Purchases.Data,
            Features.Purchases.Domain,
            Features.Purchases.Presentation,

            // Remote Config
            Features.RemoteConfig.Data,
            Features.RemoteConfig.Domain,
            Features.RemoteConfig.Presentation,

            // Resources
            Features.Resources,

            /** Starter **/

            // Core
            Starter.Core,

            // Bindings
            Native.Bindings,

            // UI
            Starter.Ui.Utils,
            Starter.Ui.Components,
            Starter.Ui.Layouts,

            // Utils
            Starter.Utils,
        )
    }

    @Serializable
    sealed class Features : StarterModules() {
        @Serializable
        sealed class Analytics : Features() {
            @Serializable
            data object Data : Analytics() {
                override val packageName: String = "com.kmpstarter.feature_analytics_data"

                override fun dependencies(): List<StarterModules> = listOf(
                    Domain,
                    Starter.Core
                )

                override fun koinModules(): List<String> = listOf(
                    "analyticsDataModule"
                )
            }

            data object Domain : Analytics() {
                override val packageName: String = "com.kmpstarter.feature_analytics_domain"

                override fun dependencies(): List<StarterModules> = listOf()

                override fun koinModules(): List<String> = listOf()
            }
        }


        @Serializable
        sealed class Core : Features() {
            data object Data : Core() {
                override val packageName: String = "com.kmpstarter.feature_core_data"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Core,
                    Domain,
                )
            }

            data object Domain : Core() {
                override val packageName: String = "com.kmpstarter.feature_core_domain"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf()
            }

            data object Presentation : Core() {
                override val packageName: String = "com.kmpstarter.feature_core_presentation"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Domain,
                    Starter.Ui.Layouts
                )
            }

        }

        @Serializable
        data object Database : Features() {
            override val packageName: String = "com.kmpstarter.feature_database"

            override fun koinModules(): List<String> {
                return super.koinModules()
            }

            override fun dependencies(): List<StarterModules> = listOf()
        }

        data object Locale : Features() {
            override val packageName: String = "com.kmpstarter.feature_locale"

            override fun koinModules(): List<String> {
                return super.koinModules()
            }

            override fun dependencies(): List<StarterModules> = listOf(
                Starter.Ui.Components
            )
        }

        @Serializable
        data object Navigation : Features() {
            override val packageName: String = "com.kmpstarter.feature_navigation"

            override fun koinModules(): List<String> {
                return super.koinModules()
            }

            override fun dependencies(): List<StarterModules> = listOf()
        }

        @Serializable
        sealed class Notifications : Features() {
            data object Core : Notifications() {
                override val packageName: String = "com.kmpstarter.feature_notifications_core"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Core,
                    Resources,
                )
            }

            data object Local : Notifications() {
                override val packageName: String = "com.kmpstarter.feature_notifications_local"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Core,
                    Starter.Core
                )
            }

            data object Push : Notifications() {
                override val packageName: String = "com.kmpstarter.feature_notifications_push"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Core,
                    Starter.Core
                )
            }
        }

        @Serializable
        sealed class Purchases : Features() {
            data object Data : Purchases() {
                override val packageName: String = "com.kmpstarter.feature_purchases_data"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Core,
                    Domain,
                )
            }

            data object Domain : Purchases() {
                override val packageName: String = "com.kmpstarter.feature_purchases_domain"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Core
                )
            }

            data object Presentation : Purchases() {
                override val packageName: String = "com.kmpstarter.feature_purchases_presentation"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Ui.Layouts,
                    Domain
                )
            }
        }

        @Serializable
        sealed class RemoteConfig : Features() {
            data object Data : RemoteConfig() {
                override val packageName: String = "com.kmpstarter.feature_remote_config_data"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Utils,
                    Domain
                )

                override fun moduleFilePath(): String {
                    return "features/remote_config/data"
                }

                override fun moduleGradleDep(mode: ProjectMode): String {
                    if (mode == ProjectMode.MODULE) return super.moduleGradleDep(mode)

                    return "libs.starter.feature.remoteConfig.data"
                }

                override fun moduleGradlePath(): String {
                    return ":features:remote_config:data"
                }

                override fun mavenArtifactId(): String {
                    return "feature-remote-config-data"
                }
            }

            data object Domain : RemoteConfig() {
                override val packageName: String = "com.kmpstarter.feature_remote_config_domain"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Utils,
                )

                override fun moduleFilePath(): String {
                    return "features/remote_config/domain"
                }

                override fun moduleGradleDep(mode: ProjectMode): String {
                    if (mode == ProjectMode.MODULE) return super.moduleGradleDep(mode)

                    return "libs.starter.feature.remoteConfig.domain"
                }

                override fun moduleGradlePath(): String {
                    return ":features:remote_config:domain"
                }

                override fun mavenArtifactId(): String {
                    return "feature-remote-config-domain"
                }
            }

            data object Presentation : RemoteConfig() {
                override val packageName: String =
                    "com.kmpstarter.feature_remote_config_presentation"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Domain
                )

                override fun moduleFilePath(): String {
                    return "features/remote_config/presentation"
                }

                override fun moduleGradleDep(mode: ProjectMode): String {
                    if (mode == ProjectMode.MODULE) return super.moduleGradleDep(mode)

                    return "libs.starter.feature.remoteConfig.presentation"
                }

                override fun moduleGradlePath(): String {
                    return ":features:remote_config:presentation"
                }

                override fun mavenArtifactId(): String {
                    return "feature-remote-config-presentation"
                }
            }
        }

        @Serializable
        data object Resources : Features() {
            override val packageName: String = "com.kmpstarter.feature_resources"

            override fun koinModules(): List<String> {
                return super.koinModules()
            }

            override fun dependencies(): List<StarterModules> = listOf(
                Locale,
                Starter.Ui.Utils
            )
        }

    }


    @Serializable
    sealed class Starter : StarterModules() {
        @Serializable
        data object Core : Starter() {
            override val packageName: String = "com.kmpstarter.core"

            override fun koinModules(): List<String> {
                return super.koinModules()
            }

            override fun dependencies(): List<StarterModules> = listOf(
                Utils
            )
        }

        sealed class Native : Starter() {
            @Serializable
            data object Bindings : Native() {
                override val packageName: String = "com.kmpstarter.native_bindings"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf()
            }
        }

        @Serializable
        sealed class Ui : Starter() {
            @Serializable
            data object Utils : Ui() {
                override val packageName: String = "com.kmpstarter.ui_utils"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Core
                )
            }

            @Serializable
            data object Components : Ui() {
                override val packageName: String = "com.kmpstarter.ui_components"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Utils
                )
            }

            @Serializable
            data object Layouts : Ui() {
                override val packageName: String = "com.kmpstarter.ui_layouts"

                override fun koinModules(): List<String> {
                    return super.koinModules()
                }

                override fun dependencies(): List<StarterModules> = listOf(
                    Components
                )
            }

        }

        @Serializable
        data object Utils : Starter() {
            override val packageName: String = "com.kmpstarter.utils"

            override fun koinModules(): List<String> {
                return super.koinModules()
            }

            override fun dependencies(): List<StarterModules> = listOf(
                Native.Bindings,
            )
        }
    }

}





















