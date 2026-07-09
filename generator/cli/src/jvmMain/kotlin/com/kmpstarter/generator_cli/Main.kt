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
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.kmpstarter.generator_cli.commands.CreateCommand
import com.kmpstarter.generator_cli.commands.IncludeCommand
import com.kmpstarter.generator_cli.commands.InitCommand
import com.kmpstarter.generator_cli.di.cliDiModule
import org.koin.core.context.startKoin

class KmpStarterCli : CliktCommand(name = "kmp-starter") {
    override fun run() = Unit
}

fun main(args: Array<String>): Unit {
    startKoin {
        modules(cliDiModule)
    }

    KmpStarterCli()
        .subcommands(CreateCommand(), InitCommand(), IncludeCommand())
        .main(args)
}
