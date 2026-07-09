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
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.kmpstarter.generator_cli.commands.viewmodels.IncludeViewModel
import com.kmpstarter.generator_cli.presentation.CliModuleCatalog
import com.kmpstarter.generator_cli.prompts.InteractivePrompts
import com.kmpstarter.generator_domain.ProjectMode
import com.kmpstarter.generator_domain.StarterModules
import kotlinx.coroutines.runBlocking
import org.koin.mp.KoinPlatform

class IncludeCommand : CliktCommand(name = "include") {

    override fun help(context: Context) = "Include a starter module into an existing project"

    private val moduleChoices = CliModuleCatalog.options.associate { it.id to it.module }

    private val dir: String by option("--dir", help = "Target project directory")
        .default(".")

    private val moduleOption: StarterModules? by option(
        "--module",
        help = "Starter module to include",
    ).choice(*moduleChoices.entries.map { (id, module) -> id to module }.toTypedArray())

    private val mode: ProjectMode? by option("--mode", help = "Project mode: lib or module")
        .choice("lib" to ProjectMode.LIB, "module" to ProjectMode.MODULE)

    private val pkg: String? by option("--package", help = "Application package name")

    private val target: String by option("--target", help = "Gradle module to add the dependency to")
        .default("composeApp")

    override fun run() {
        runBlocking {
        val viewModel = KoinPlatform.getKoin().get<IncludeViewModel>()

        val module = moduleOption ?: run {
            echo("Include a starter module into an existing project")
            echo("")
            InteractivePrompts.promptSingleModule(CliModuleCatalog.options)
        }

        val resolvedMode = mode ?: viewModel.readStarterJson(dir)?.mode ?: run {
            if (moduleOption != null) {
                throw CliktError("Project mode is required. Pass --mode or run init to create starter.json.")
            }
            InteractivePrompts.promptProjectMode()
        }

        viewModel.include(
            dir = dir,
            module = module,
            mode = resolvedMode,
            packageName = pkg,
            targetModule = target,
        ).onSuccess {
            val moduleId = CliModuleCatalog.findById(module.mavenArtifactId())?.id ?: module.mavenArtifactId()
            echo("Included module '$moduleId' into '$dir' (mode=$resolvedMode, target=$target)")
        }.onFailure { error ->
            throw CliktError(error.message ?: "Failed to include module.")
        }
        }
    }
}
