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
import com.github.ajalt.mordant.input.interactiveSelectList
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.widgets.SelectList
import com.kmpstarter.generator_cli.presentation.CliModuleCatalog
import com.kmpstarter.generator_cli.presentation.CliModuleOption
import com.kmpstarter.generator_domain.ProjectMode
import com.kmpstarter.generator_domain.StarterModules

object InteractivePrompts {

    private val inputReader = System.`in`.bufferedReader()
    private val terminal = Terminal()

    private const val ALL_OPTION = "All optional modules"
    private const val CONFIRM_OPTION = "Confirm"
    private const val REQUIRED_ONLY_OPTION = "Continue with required only"

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

    /** Arrow-key Yes/No. [default] option is labeled Recommended and listed first. */
    fun promptYesNoChoice(message: String, default: Boolean = true): Boolean {
        val yesLabel = if (default) "Yes (Recommended)" else "Yes"
        val noLabel = if (!default) "No (Recommended)" else "No"
        val first = if (default) yesLabel else noLabel
        val second = if (default) noLabel else yesLabel

        if (!supportsArrowSelect()) {
            return promptYesNo(message, default)
        }

        val selected = try {
            terminal.interactiveSelectList(
                entries = listOf(first, second),
                title = message,
            )
        } catch (_: IllegalStateException) {
            return promptYesNo(message, default)
        } ?: return default

        return selected.startsWith("Yes")
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
        if (!supportsArrowSelect()) {
            return promptSingleModuleByNumber(options)
        }

        val labels = options.map { "${it.label} (${it.id})" }
        val selected = terminal.interactiveSelectList(
            entries = labels,
            title = "Select a module to include",
        ) ?: throw CliktError("Module selection cancelled.")

        val index = labels.indexOf(selected)
        if (index !in options.indices) {
            throw CliktError("Invalid module selection.")
        }
        return options[index].module
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

        if (!supportsArrowSelect()) {
            echo("Arrow-key selection needs a real TTY. Falling back to number input.")
            echo("Tip: run the installed CLI (`starter create`) or:")
            echo("  ./generator/cli/npm-package/bin/starter.js create")
            echo("")
            return promptMultipleModulesByNumber(catalog, optional)
        }

        echo("Use ↑/↓ to move, Enter to select/toggle. Esc cancels.")
        echo("")

        val selectedIds = linkedSetOf<String>()
        val optionByTitle = optional.associateBy { "${it.label} (${it.id})" }

        while (true) {
            val entries = buildList {
                add(
                    SelectList.Entry(
                        title = ALL_OPTION,
                        description = "Include every optional module and continue",
                    ),
                )
                optional.forEach { option ->
                    val title = "${option.label} (${option.id})"
                    add(
                        SelectList.Entry(
                            title = title,
                            selected = option.id in selectedIds,
                        ),
                    )
                }
                if (selectedIds.isNotEmpty()) {
                    add(
                        SelectList.Entry(
                            title = CONFIRM_OPTION,
                            description = "Continue with ${selectedIds.size} selected optional module(s)",
                        ),
                    )
                } else {
                    add(
                        SelectList.Entry(
                            title = REQUIRED_ONLY_OPTION,
                            description = "Skip optional modules",
                        ),
                    )
                }
            }

            val choice = try {
                terminal.interactiveSelectList(
                    entries = entries,
                    title = "Optional modules",
                )
            } catch (_: IllegalStateException) {
                echo("Arrow-key selection unavailable. Falling back to number input.")
                echo("")
                return promptMultipleModulesByNumber(catalog, optional)
            } ?: return catalog.requiredModules()

            when (choice) {
                ALL_OPTION -> return catalog.options.map { it.module }
                CONFIRM_OPTION -> {
                    val selectedOptional = optional
                        .filter { it.id in selectedIds }
                        .map { it.module }
                    return catalog.ensureRequiredIncluded(selectedOptional)
                }
                REQUIRED_ONLY_OPTION -> return catalog.requiredModules()
                else -> {
                    val option = optionByTitle[choice] ?: continue
                    if (option.id in selectedIds) {
                        selectedIds.remove(option.id)
                    } else {
                        selectedIds.add(option.id)
                    }
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

    private fun supportsArrowSelect(): Boolean =
        terminal.terminalInfo.inputInteractive && System.console() != null

    private fun promptSingleModuleByNumber(options: List<CliModuleOption>): StarterModules {
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

    private fun promptMultipleModulesByNumber(
        catalog: CliModuleCatalog,
        optional: List<CliModuleOption>,
    ): List<StarterModules> {
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
                    appendLine("Or run the CLI outside Gradle for arrow-key selection:")
                    appendLine("  ./gradlew :generator:cli:assembleStarterCliNpm")
                    appendLine("  node generator/cli/npm-package/bin/starter.js create")
                },
            )
        }

        return line.trim()
    }

    private fun echo(message: String) {
        System.out.println(message)
    }
}
