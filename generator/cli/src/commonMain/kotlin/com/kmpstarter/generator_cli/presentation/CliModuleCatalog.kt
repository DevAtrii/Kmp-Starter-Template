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

package com.kmpstarter.generator_cli.presentation

import com.kmpstarter.generator_domain.StarterModules

object CliModuleCatalog {

    val options: List<CliModuleOption> = listOf(
        // Starter
        option(StarterModules.Starter.Core, "Starter Core", required = true),
        option(StarterModules.Starter.Utils, "Starter Utils", required = true),
        option(StarterModules.Starter.Native.Bindings, "Native Bindings", required = true),
        option(StarterModules.Starter.Ui.Utils, "UI Utils", required = true),
        option(StarterModules.Starter.Ui.Components, "UI Components", required = true),
        option(StarterModules.Starter.Ui.Layouts, "UI Layouts", required = true),

        // Features — core app stack
        option(StarterModules.Features.Core.Data, "Core Data", required = true),
        option(StarterModules.Features.Core.Domain, "Core Domain", required = true),
        option(StarterModules.Features.Core.Presentation, "Core Presentation", required = true),
        option(StarterModules.Features.Resources, "Resources", required = true),
        option(StarterModules.Features.Navigation, "Navigation", required = true),
        option(StarterModules.Features.Locale, "Locale", required = true),
        option(StarterModules.Features.Database, "Database"),

        // Analytics
        option(StarterModules.Features.Analytics.Data, "Analytics Data"),
        option(StarterModules.Features.Analytics.Domain, "Analytics Domain"),

        // Notifications
        option(StarterModules.Features.Notifications.Core, "Notifications Core"),
        option(StarterModules.Features.Notifications.Local, "Notifications Local"),
        option(StarterModules.Features.Notifications.Push, "Notifications Push"),

        // Purchases
        option(StarterModules.Features.Purchases.Data, "Purchases Data"),
        option(StarterModules.Features.Purchases.Domain, "Purchases Domain"),
        option(StarterModules.Features.Purchases.Presentation, "Purchases Presentation"),

        // Remote Config
        option(StarterModules.Features.RemoteConfig.Data, "Remote Config Data"),
        option(StarterModules.Features.RemoteConfig.Domain, "Remote Config Domain"),
        option(StarterModules.Features.RemoteConfig.Presentation, "Remote Config Presentation"),
    )

    fun requiredModules(): List<StarterModules> =
        options.filter { it.required }.map { it.module }

    fun optionalOptions(): List<CliModuleOption> =
        options.filter { !it.required }

    fun findById(id: String): CliModuleOption? =
        options.find { it.id.equals(id, ignoreCase = true) }

    fun labelFor(module: StarterModules): String =
        options.find { it.module == module }?.label
            ?: module::class.simpleName.orEmpty()

    fun resolveModules(raw: String): List<StarterModules> {
        if (raw.equals("all", ignoreCase = true)) {
            return options.map { it.module }
        }

        val selectedIds = raw.split(',').map { it.trim() }.filter { it.isNotEmpty() }
        val resolved = selectedIds.map { id ->
            findById(id)?.module
                ?: error("Unknown module '$id'. Use one of: ${options.joinToString { it.id }}")
        }

        return ensureRequiredIncluded(resolved)
    }

    fun ensureRequiredIncluded(selected: List<StarterModules>): List<StarterModules> =
        (requiredModules() + selected).distinctBy { it::class }

    private fun option(
        module: StarterModules,
        label: String,
        required: Boolean = false,
    ): CliModuleOption = CliModuleOption(
        module = module,
        id = module.mavenArtifactId(),
        label = label,
        required = required,
    )
}
