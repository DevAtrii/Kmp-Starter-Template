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
)

interface BaseModule {
    fun dependencies(): List<StarterModules>
}

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
            Starter.Bindings,

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
            }

            data object Domain : RemoteConfig() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Starter.Utils,
                )
            }

            data object Presentation : RemoteConfig() {
                override fun dependencies(): List<StarterModules> = listOf(
                    Domain
                )
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

        @Serializable
        data object Bindings : Starter() {
            override fun dependencies(): List<StarterModules> = listOf()
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
                Bindings,
            )
        }
    }

}





















