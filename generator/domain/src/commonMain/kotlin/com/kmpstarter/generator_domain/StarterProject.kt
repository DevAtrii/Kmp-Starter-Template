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

    fun dependencies(): List<StarterModules>

    fun moduleGradleDep(mode: ProjectMode): String {
        val clazz = this::class
        val raw = clazz.qualifiedName!!
            .replace("com.kmpstarter.generator_domain.StarterModules.", "")
            .lowercase()
            .let {
                if (mode == ProjectMode.LIB) {
                    it.replace("features", "feature").let { it2 ->
                        if (it2.startsWith("starter."))
                            it2.removePrefix("starter.")
                        else it2
                    }
                } else it
            }

        val prefix = when (mode) {
            ProjectMode.MODULE -> "projects"
            ProjectMode.LIB -> "libs.starter"
        }
        val dep = "$prefix.$raw"
        return dep
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
}

fun main() {
    val module = StarterModules.Features.Database
    println("\n\n=====MODULE=====")
    println(module::class.qualifiedName)
    println(module.moduleGradleDep(ProjectMode.LIB))
    println(module.moduleFilePath())
    println(module.moduleGradlePath())
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

            // Core App
            Features.CoreApp.Data,
            Features.CoreApp.Domain,
            Features.CoreApp.Presentation,

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
                override fun dependencies(): List<StarterModules> = listOf(
                    Domain,
                    Starter.Core
                )
            }

            data object Domain : Analytics() {
                override fun dependencies(): List<StarterModules> = listOf()
            }
        }

        @Serializable
        sealed class CoreApp : Features() {
            data object Data : CoreApp() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Core,
                    Domain,
                    Core.Domain,
                )


                override fun moduleFilePath(): String {
                    return "features/core_app/data"
                }

                override fun moduleGradleDep(mode: ProjectMode): String {
                    if (mode == ProjectMode.LIB) throw ModuleOnlyException()

                    return "projects.features.coreApp.data"
                }

                override fun moduleGradlePath(): String {
                    return ":features:core_app:data"
                }
            }

            data object Domain : CoreApp() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Analytics.Domain,
                )

                override fun moduleFilePath(): String {
                    return "features/core_app/domain"
                }

                override fun moduleGradleDep(mode: ProjectMode): String {
                    if (mode == ProjectMode.LIB) throw ModuleOnlyException()

                    return "projects.features.coreApp.domain"
                }
                override fun moduleGradlePath(): String {
                    return ":features:core_app:domain"
                }
            }

            data object Presentation : CoreApp() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Analytics.Domain,
                    Resources,
                    Domain,
                    Core.Domain,
                    Starter.Ui.Layouts
                )

                override fun moduleFilePath(): String {
                    return "features/core_app/presentation"
                }

                override fun moduleGradleDep(mode: ProjectMode): String {
                    if (mode == ProjectMode.LIB) throw ModuleOnlyException()

                    return "projects.features.coreApp.presentation"
                }
                override fun moduleGradlePath(): String {
                    return ":features:core_app:presentation"
                }
            }

        }

        @Serializable
        sealed class Core : Features() {
            data object Data : Core() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Core,
                    Domain,
                )
            }

            data object Domain : Core() {
                override fun dependencies(): List<StarterModules> = listOf()
            }

            data object Presentation : Core() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Domain,
                    Starter.Ui.Layouts
                )
            }

        }

        @Serializable
        data object Database : Features() {
            override fun dependencies(): List<StarterModules> = listOf()
        }

        data object Locale : Features() {
            override fun dependencies(): List<StarterModules> = listOf(
                Starter.Ui.Components
            )
        }

        @Serializable
        data object Navigation : Features() {
            override fun dependencies(): List<StarterModules> = listOf()
        }

        @Serializable
        sealed class Notifications : Features() {
            data object Core : Notifications() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Core,
                    Resources,
                )
            }

            data object Local : Notifications() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Core,
                    Starter.Core
                )
            }

            data object Push : Notifications() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Core,
                    Starter.Core
                )
            }
        }

        @Serializable
        sealed class Purchases : Features() {
            data object Data : Purchases() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Core,
                    Domain,
                )
            }

            data object Domain : Purchases() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Core
                )
            }

            data object Presentation : Purchases() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Ui.Layouts,
                    Domain
                )
            }
        }

        @Serializable
        sealed class RemoteConfig : Features() {
            data object Data : RemoteConfig() {
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
            }

            data object Domain : RemoteConfig() {
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
            }

            data object Presentation : RemoteConfig() {
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
            }
        }

        @Serializable
        data object Resources : Features() {
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
            override fun dependencies(): List<StarterModules> = listOf(
                Utils
            )
        }

        sealed class Native : Starter() {
            @Serializable
            data object Bindings : Native() {
                override fun dependencies(): List<StarterModules> = listOf()
            }
        }

        @Serializable
        sealed class Ui : Starter() {
            @Serializable
            data object Utils : Ui() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Core
                )
            }

            @Serializable
            data object Components : Ui() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Utils
                )
            }

            @Serializable
            data object Layouts : Ui() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Components
                )
            }

        }

        @Serializable
        data object Utils : Starter() {
            override fun dependencies(): List<StarterModules> = listOf(
                Native.Bindings,
            )
        }
    }

}





















