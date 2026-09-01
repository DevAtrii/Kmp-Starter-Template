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

package com.kmpstarter.generator_cli.di

import com.kmpstarter.generator_cli.commands.viewmodels.CreateViewModel
import com.kmpstarter.generator_cli.commands.viewmodels.ExcludeViewModel
import com.kmpstarter.generator_cli.commands.viewmodels.IncludeViewModel
import com.kmpstarter.generator_cli.commands.viewmodels.InitViewModel
import com.kmpstarter.generator_cli.commands.viewmodels.UpgradeViewModel
import com.kmpstarter.generator_data.di.generatorDiModule
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val cliDiModule = module {
    includes(generatorDiModule)
    factoryOf(::CreateViewModel)
    factoryOf(::IncludeViewModel)
    factoryOf(::ExcludeViewModel)
    factoryOf(::UpgradeViewModel)
    factoryOf(::InitViewModel)
}
