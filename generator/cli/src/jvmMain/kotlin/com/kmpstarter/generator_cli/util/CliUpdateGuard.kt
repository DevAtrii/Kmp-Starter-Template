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

package com.kmpstarter.generator_cli.util

import com.github.ajalt.clikt.core.CliktError
import com.kmpstarter.generator_cli.CliInvocation
import com.kmpstarter.generator_cli.prompts.InteractivePrompts
import com.kmpstarter.generator_data.di.closeGeneratorResources
import com.kmpstarter.generator_data.interfaces.StarterProjectSourceCodeProvider
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatform
import java.io.IOException
import kotlin.system.exitProcess

object CliUpdateGuard {

    const val UPDATE_COMMAND = "npm update -g @devatrii/starter"
    const val SKIP_ENV = "STARTER_SKIP_UPDATE_CHECK"

    suspend fun offerUpdateIfNeeded() {
        if (System.getenv(SKIP_ENV) == "1") return

        val provider = KoinPlatform.getKoin().get<StarterProjectSourceCodeProvider>()
        val support = provider.getSourceVersionSupport().getOrNull() ?: return
        if (support.cliSupportsLatest) return

        echoYellow(
            "A newer starter release is available (${support.newestAvailable}), " +
                "but this CLI supports up to ${support.maxSupported}.",
        )
        echo("Update the CLI to get the latest features:")
        echo("  $UPDATE_COMMAND")
        echo("")

        val shouldUpdate = try {
            InteractivePrompts.promptYesNoChoice("Update CLI now?", default = true)
        } catch (_: CliktError) {
            echo("Could not prompt. Run `$UPDATE_COMMAND` then retry.")
            return
        }

        if (!shouldUpdate) return

        echo("")
        echo("Updating CLI...")
        runNpmUpdate()
        relaunchWithUpdatedCli()
    }

    private fun runNpmUpdate() {
        val command = if (isWindows()) {
            listOf("cmd.exe", "/c", "npm", "update", "-g", "@devatrii/starter")
        } else {
            listOf("npm", "update", "-g", "@devatrii/starter")
        }

        val exitCode = try {
            ProcessBuilder(command)
                .inheritIO()
                .start()
                .waitFor()
        } catch (error: IOException) {
            throw CliktError(
                "Failed to run npm (${error.message}). Install Node.js, then: $UPDATE_COMMAND",
            )
        }

        if (exitCode != 0) {
            throw CliktError("CLI update failed (exit $exitCode). Run: $UPDATE_COMMAND")
        }
    }

    private fun relaunchWithUpdatedCli() {
        val args = CliInvocation.args.toList()
        val command = if (isWindows()) {
            listOf("cmd.exe", "/c", "starter") + args
        } else {
            listOf("starter") + args
        }

        echo("")
        echo("CLI updated. Continuing project initialization with the new CLI...")
        echo("")

        val process = try {
            ProcessBuilder(command)
                .inheritIO()
                .apply { environment()[SKIP_ENV] = "1" }
                .start()
        } catch (error: IOException) {
            echo("CLI updated, but could not restart `starter` (${error.message}).")
            echo("Re-run your command to use the new CLI.")
            return
        }

        closeGeneratorResources()
        stopKoin()
        exitProcess(process.waitFor())
    }

    private fun isWindows(): Boolean =
        System.getProperty("os.name").orEmpty().lowercase().contains("win")

    private fun echo(message: String) {
        System.out.println(message)
    }

    private fun echoYellow(message: String) {
        System.out.println("\u001B[33m$message\u001B[0m")
    }
}
