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
import com.kmpstarter.generator_cli.commands.CreateCommand
import com.kmpstarter.generator_cli.commands.IncludeCommand
import com.kmpstarter.generator_cli.commands.InitCommand
import com.kmpstarter.generator_cli.di.cliDiModule
import com.kmpstarter.generator_data.di.closeGeneratorResources
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.system.exitProcess

class KmpStarterCli : CliktCommand(name = "kmp-starter") {
    override fun run() = Unit
}

fun main(args: Array<String>) {
    startKoin {
        modules(cliDiModule)
    }

    try {
        KmpStarterCli()
            .subcommands(CreateCommand(), InitCommand(), IncludeCommand())
            .main(args)
    } finally {
        closeGeneratorResources()
        stopKoin()
    }

    // OkHttp/Ktor keep non-daemon threads alive after a successful run.
    exitProcess(0)
}
