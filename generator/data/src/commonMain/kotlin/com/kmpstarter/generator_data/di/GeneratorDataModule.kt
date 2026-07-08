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

package com.kmpstarter.generator_data.di

import com.kmpstarter.generator_data.StarterProjectsRepositoryImpl
import com.kmpstarter.generator_data.impl.FileManagerImpl
import com.kmpstarter.generator_data.impl.LocalSourceCodeProvider
import com.kmpstarter.generator_data.interfaces.StarterProjectFileManager
import com.kmpstarter.generator_data.interfaces.StarterProjectSourceCodeProvider
import com.kmpstarter.generator_domain.StarterProjectsRepository
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

val generatorDiModule = module {
    factoryOf(::FileManagerImpl) bind StarterProjectFileManager::class
    factoryOf(::StarterProjectsRepositoryImpl) bind StarterProjectsRepository::class
    factoryOf(::LocalSourceCodeProvider) bind StarterProjectSourceCodeProvider::class
}

suspend fun main() {
    startKoin {
        modules(generatorDiModule)
    }
    val fileManager: StarterProjectFileManager = KoinPlatform.getKoin().get()
    val sourceCodeProvider: StarterProjectSourceCodeProvider = KoinPlatform.getKoin().get()
    val code = sourceCodeProvider.getSourceCode().getOrThrow()
    println("code=$code")
    println("Directory===")
    println(fileManager.getCurrentDir())
}