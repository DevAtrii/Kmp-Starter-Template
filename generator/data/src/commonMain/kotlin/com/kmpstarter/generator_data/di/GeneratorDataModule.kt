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
import com.kmpstarter.generator_domain.ProjectMode
import com.kmpstarter.generator_domain.StarterModules
import com.kmpstarter.generator_domain.StarterProject
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
    val repo: StarterProjectsRepository = KoinPlatform.getKoin().get()
    val fileManager: StarterProjectFileManager = KoinPlatform.getKoin().get()

    repo.includeModule(
        workingDir = "/Users/athargul/Coding_2025/App/KMP/KMP-Starter/Code/generator/data/.starter/code",
        module = StarterModules.Features.RemoteConfig.Domain,
        mode = ProjectMode.LIB,
        packageName = null,
        targetModule = "composeApp"
    )

    return

    fileManager.delete(path = "${fileManager.getCurrentDir()}/.starter/generate-code")
    val project = StarterProject(
        projectName = "Notes",
        packageName = "com.atrii.notes",
        mode = ProjectMode.LIB,
        featureName = "notes",
        includeWorkflows = true,
        modules = StarterModules.all() - setOf(
            StarterModules.Features.Database,
            StarterModules.Features.RemoteConfig.Data,
            StarterModules.Features.RemoteConfig.Domain,
        )
    )

    println("filePaths ${StarterModules.Features.RemoteConfig.Data.moduleFilePath()}, ${StarterModules.Features.Core.Data.moduleFilePath()}")

    repo.generate(
        project = project
    ).onSuccess { zipBytes ->
        println("Got Source Code: ${zipBytes.size}")
        val path = "${fileManager.getCurrentDir()}/.starter/generate-code.zip"
        fileManager.writeFile(
            path = path,
            content = zipBytes
        )
        fileManager.extractZip(
            path = path,
            output = "${fileManager.getCurrentDir()}/.starter/code"
        )
    }.onFailure { err ->
        err.printStackTrace()
    }

}



















