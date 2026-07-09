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

package com.kmpstarter.generator_cli.prompts

import com.github.ajalt.clikt.core.CliktError
import com.kmpstarter.generator_cli.presentation.CliModuleCatalog
import com.kmpstarter.generator_cli.presentation.CliModuleOption
import com.kmpstarter.generator_domain.ProjectMode
import com.kmpstarter.generator_domain.StarterModules

object InteractivePrompts {

    private val inputReader = System.`in`.bufferedReader()

    fun promptString(
        message: String,
        default: String? = null,
        required: Boolean = true,
    ): String {
        while (true) {
            val suffix = default?.let { " [$it]" }.orEmpty()
            val input = readInput("$message$suffix: ")
            when {
                input.isNotEmpty() -> return input
                default != null -> return default
                !required -> return ""
                else -> echo("This field is required.")
            }
        }
    }

    fun promptYesNo(message: String, default: Boolean = true): Boolean {
        val hint = if (default) "Y/n" else "y/N"
        while (true) {
            when (readInput("$message ($hint): ").lowercase()) {
                "" -> return default
                "y", "yes" -> return true
                "n", "no" -> return false
                else -> echo("Please enter y or n.")
            }
        }
    }

    fun promptProjectMode(default: ProjectMode = ProjectMode.LIB): ProjectMode {
        while (true) {
            when (readInput("Project mode [lib/module] (${default.name.lowercase()}): ").lowercase()) {
                "", "lib" -> return ProjectMode.LIB
                "module" -> return ProjectMode.MODULE
                else -> echo("Enter 'lib' or 'module'.")
            }
        }
    }

    fun promptSingleModule(options: List<CliModuleOption>): StarterModules {
        echo("Select a module to include:")
        options.forEachIndexed { index, option ->
            echo("  ${index + 1}. ${option.label} (${option.id})")
        }

        while (true) {
            val input = readInput("Enter number: ")
            val index = input.toIntOrNull()?.minus(1)
            if (index != null && index in options.indices) {
                return options[index].module
            }
            echo("Enter a number between 1 and ${options.size}.")
        }
    }

    fun promptMultipleModules(catalog: CliModuleCatalog): List<StarterModules> {
        val required = catalog.options.filter { it.required }
        val optional = catalog.optionalOptions()

        echo("Required modules (always included):")
        required.forEach { option ->
            echo("  * ${option.label} (${option.id})")
        }
        echo("")

        if (optional.isEmpty()) {
            return catalog.requiredModules()
        }

        echo("Optional modules:")
        optional.forEachIndexed { index, option ->
            echo("  ${index + 1}. ${option.label} (${option.id})")
        }
        echo("  a. All optional modules")
        echo("")

        while (true) {
            val input = readInput(
                "Select optional modules by number (comma-separated), 'a' for all, " +
                    "or press Enter for required only: ",
                allowEmpty = true,
            )
            when {
                input.isEmpty() -> return catalog.requiredModules()
                input.equals("a", ignoreCase = true) || input.equals("all", ignoreCase = true) -> {
                    return catalog.options.map { it.module }
                }
                else -> {
                    val indexes = input.split(',')
                        .mapNotNull { it.trim().toIntOrNull()?.minus(1) }

                    if (indexes.isEmpty() || indexes.any { it !in optional.indices }) {
                        echo("Enter valid numbers from the list, 'a', or press Enter.")
                        continue
                    }

                    val selectedOptional = indexes.map { optional[it].module }
                    return catalog.ensureRequiredIncluded(selectedOptional)
                }
            }
        }
    }

    fun suggestPackageName(appName: String): String {
        val slug = appName
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), ".")
            .trim('.')
            .ifBlank { "myapp" }
        return "com.example.$slug"
    }

    fun suggestFeatureName(appName: String): String {
        val slug = appName
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
        return slug.ifBlank { "notes" }
    }

    private fun readInput(prompt: String, allowEmpty: Boolean = false): String {
        System.out.print(prompt)
        System.out.flush()

        val line = System.console()?.readLine()
            ?: runCatching { inputReader.readLine() }.getOrNull()

        if (line == null) {
            throw CliktError(
                buildString {
                    appendLine("Could not read input (stdin is not connected).")
                    appendLine("Pass options on the command line, for example:")
                    appendLine("  create --name MyApp --package com.example.myapp --feature myfeature --modules all")
                    appendLine()
                    appendLine("Or forward stdin from Gradle:")
                    appendLine("  ./gradlew :generator:cli:jvmRun --console=plain --args=\"create\"")
                },
            )
        }

        return line.trim()
    }

    private fun echo(message: String) {
        System.out.println(message)
    }
}
