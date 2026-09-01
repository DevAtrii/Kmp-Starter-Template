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

package com.kmpstarter.generator_cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.kmpstarter.generator_cli.commands.viewmodels.UpgradeViewModel
import com.kmpstarter.generator_cli.presentation.CliModuleCatalog
import com.kmpstarter.generator_domain.StarterModules
import kotlinx.coroutines.runBlocking
import org.koin.mp.KoinPlatform

class UpgradeCommand : CliktCommand(name = "upgrade") {

    override fun help(context: Context) =
        "Upgrade starter libraries or module sources to a newer template version"

    private val moduleChoices = CliModuleCatalog.options.associate { it.id to it.module }

    private val dir: String by option("--dir", help = "Target project directory")
        .default(".")

    private val moduleOption: StarterModules? by option(
        "--module",
        help = "Upgrade a single module (module mode). Omit to upgrade all included modules",
    ).choice(*moduleChoices.entries.map { (id, module) -> id to module }.toTypedArray())

    private val versionOption: String? by option("--version", help = "Target starter version")

    private val yes: Boolean by option("--yes", help = "Overwrite locally modified module sources")
        .flag(default = false)

    private val zipOption: String? by option("--zip", help = "Path to a local starter source zip")

    override fun run() {
        runBlocking {
            val viewModel = KoinPlatform.getKoin().get<UpgradeViewModel>()
            viewModel.upgrade(
                dir = dir,
                module = moduleOption,
                targetVersion = versionOption,
                force = yes,
                sourceZipPath = zipOption,
            ).onSuccess { result ->
                echo("Upgraded starter ${result.fromVersion} → ${result.toVersion}")
                if (result.upgraded.isNotEmpty()) {
                    echo("  updated: ${result.upgraded.joinToString()}")
                }
                if (result.skippedBecauseDirty.isNotEmpty()) {
                    echo("  skipped (dirty, pass --yes to overwrite): ${result.skippedBecauseDirty.joinToString()}")
                }
            }.onFailure { error ->
                throw CliktError(error.message ?: "Failed to upgrade starter.")
            }
        }
    }
}
