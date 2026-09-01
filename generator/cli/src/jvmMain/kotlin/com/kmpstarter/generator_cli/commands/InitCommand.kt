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
import com.kmpstarter.generator_cli.commands.viewmodels.InitViewModel
import com.kmpstarter.generator_cli.prompts.InteractivePrompts
import com.kmpstarter.generator_cli.util.CliUpdateGuard
import com.kmpstarter.generator_domain.ProjectMode
import kotlinx.coroutines.runBlocking
import org.koin.mp.KoinPlatform

class InitCommand : CliktCommand(name = "init") {

    override fun help(context: Context) =
        "Add KMP Starter to an existing project (writes starter.json and includes required modules)"

    private val dirOption: String? by option("--dir", help = "Target project directory")

    private val pkgOption: String? by option("--package", help = "Application package name")

    private val modeOption: ProjectMode? by option("--mode", help = "Project mode: lib or module")
        .choice("lib" to ProjectMode.LIB, "module" to ProjectMode.MODULE)

    private val versionOption: String? by option("--version", help = "Starter template version")

    private val zipOption: String? by option("--zip", help = "Path to a local starter source zip")

    override fun run() {
        runBlocking {
        CliUpdateGuard.offerUpdateIfNeeded()
        val viewModel = KoinPlatform.getKoin().get<InitViewModel>()
        val interactive = dirOption == null || pkgOption == null || modeOption == null

        if (interactive) {
            echo("Initialize starter.json for an existing project")
            echo("")
        }

        val dir = dirOption ?: InteractivePrompts.promptString(
            message = "Project directory",
            default = ".",
        )
        val pkg = pkgOption ?: InteractivePrompts.promptString(
            message = "Package name",
            default = viewModel.defaultPackageName(),
        )
        val mode = modeOption ?: InteractivePrompts.promptProjectMode()
        val version = versionOption ?: if (interactive) {
            InteractivePrompts.promptString(
                message = "Starter version",
                default = viewModel.defaultStarterVersion(),
                required = false,
            ).ifBlank { null }
        } else {
            null
        }

        viewModel.init(
            dir = dir,
            packageName = pkg,
            mode = mode,
            starterVersion = version,
            sourceZipPath = zipOption,
        ).onSuccess { result ->
            echo("Created ${result.starterJsonPath}")
            if (result.adopted) {
                echo("  included required starter modules into the existing KMP app")
            }
            echo("  package=$pkg mode=$mode version=${version ?: viewModel.defaultStarterVersion()}")
        }.onFailure { error ->
            throw CliktError(error.message ?: "Failed to initialize starter.json.")
        }
        }
    }
}
