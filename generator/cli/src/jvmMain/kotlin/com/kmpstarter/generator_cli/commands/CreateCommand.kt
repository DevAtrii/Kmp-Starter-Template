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
import com.kmpstarter.generator_cli.commands.viewmodels.CreateViewModel
import com.kmpstarter.generator_cli.presentation.CliModuleCatalog
import com.kmpstarter.generator_cli.prompts.InteractivePrompts
import com.kmpstarter.generator_domain.ProjectMode
import com.kmpstarter.generator_domain.StarterProject
import kotlinx.coroutines.runBlocking
import org.koin.mp.KoinPlatform

class CreateCommand : CliktCommand(name = "create") {

    override fun help(context: Context) = "Create a new KMP starter project as a zip archive"

    private val nameOption: String? by option("--name", help = "Project name")

    private val pkgOption: String? by option("--package", help = "Application package name")

    private val modeOption: ProjectMode? by option("--mode", help = "Project mode: lib or module")
        .choice("lib" to ProjectMode.LIB, "module" to ProjectMode.MODULE)

    private val featureOption: String? by option("--feature", help = "Feature module name")

    private val workflowsFlag: Boolean by option("--workflows", help = "Include GitHub workflows")
        .flag(default = false)

    private val noWorkflows: Boolean by option("--no-workflows", help = "Exclude GitHub workflows")
        .flag(default = false)

    private val modulesOption: String? by option(
        "--modules",
        help = "Comma-separated module ids, or 'all' (required modules are always included)",
    )

    private val outputOption: String? by option("--output", help = "Output zip file path")

    private val noExtract: Boolean by option("--no-extract", help = "Save zip only; do not extract")
        .flag(default = false)

    override fun run() {
        runBlocking {
        val interactive = nameOption == null || pkgOption == null || featureOption == null || modulesOption == null
        if (interactive) {
            echo("Create a new KMP starter project")
            echo("")
        }

        val name = nameOption ?: InteractivePrompts.promptString("App name")
        val pkg = pkgOption ?: InteractivePrompts.promptString(
            message = "Package name",
            default = InteractivePrompts.suggestPackageName(name),
        )
        val mode = modeOption ?: if (interactive) {
            InteractivePrompts.promptProjectMode()
        } else {
            ProjectMode.LIB
        }
        val feature = featureOption ?: InteractivePrompts.promptString(
            message = "Feature name (e.g. notes)",
            default = InteractivePrompts.suggestFeatureName(name),
        )
        if (feature.isBlank()) {
            throw CliktError("Feature name is required.")
        }

        val selectedModules = when {
            modulesOption != null -> CliModuleCatalog.resolveModules(modulesOption!!)
            interactive -> InteractivePrompts.promptMultipleModules(CliModuleCatalog)
            else -> CliModuleCatalog.requiredModules()
        }

        val includeWorkflows = when {
            noWorkflows -> false
            workflowsFlag -> true
            interactive -> InteractivePrompts.promptYesNo("Include GitHub workflows?", default = true)
            else -> true
        }

        val output = outputOption ?: if (interactive) {
            InteractivePrompts.promptString(
                message = "Output zip path",
                default = "${name.lowercase().replace(' ', '-')}.zip",
                required = false,
            ).ifBlank { null }
        } else {
            null
        } ?: "${name.lowercase().replace(' ', '-')}.zip"

        val extract = if (interactive && outputOption == null) {
            InteractivePrompts.promptYesNo("Extract zip after generation?", default = true)
        } else {
            !noExtract
        }

        val project = StarterProject(
            projectName = name,
            packageName = pkg,
            mode = mode,
            featureName = feature,
            includeWorkflows = includeWorkflows,
            modules = selectedModules,
        )

        echo("")
        echo("Creating project '$name'...")
        echo("")

        val viewModel = KoinPlatform.getKoin().get<CreateViewModel>()
        viewModel.create(
            project = project,
            outputZipPath = output,
            extract = extract,
        ).onSuccess { result ->
            echo("Created project '$name'")
            echo("  zip: ${result.zipPath}")
            if (result.extractPath != null) {
                echo("  extracted: ${result.extractPath}")
            }
        }.onFailure { error ->
            throw CliktError(error.message ?: "Failed to create project.")
        }
        }
    }
}
