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

package com.kmpstarter.generator_cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption
import com.kmpstarter.generator_cli.commands.CreateCommand
import com.kmpstarter.generator_cli.commands.ExcludeCommand
import com.kmpstarter.generator_cli.commands.IncludeCommand
import com.kmpstarter.generator_cli.commands.InitCommand
import com.kmpstarter.generator_cli.commands.UpgradeCommand
import com.kmpstarter.generator_cli.commands.VersionCommand
import com.kmpstarter.generator_cli.di.cliDiModule
import com.kmpstarter.generator_data.di.closeGeneratorResources
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.system.exitProcess

object CliInvocation {
    var args: Array<String> = emptyArray()
        internal set
}

class KmpStarterCli : CliktCommand(name = "kmp-starter") {
    init {
        versionOption(CliVersion.VALUE, names = setOf("--version", "-v"))
    }

    override fun run() = Unit
}

fun main(args: Array<String>) {
    CliInvocation.args = args
    startKoin {
        modules(cliDiModule)
    }

    try {
        KmpStarterCli()
            .subcommands(
                CreateCommand(),
                InitCommand(),
                IncludeCommand(),
                ExcludeCommand(),
                UpgradeCommand(),
                VersionCommand(),
            )
            .main(args)
    } finally {
        closeGeneratorResources()
        stopKoin()
    }

    // OkHttp/Ktor keep non-daemon threads alive after a successful run.
    exitProcess(0)
}
