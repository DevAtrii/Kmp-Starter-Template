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
import com.kmpstarter.generator_data.impl.GithubSourceCodeProvider
import com.kmpstarter.generator_data.interfaces.StarterProjectFileManager
import com.kmpstarter.generator_data.interfaces.StarterProjectSourceCodeProvider
import com.kmpstarter.generator_domain.StarterProjectsRepository
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

val generatorDiModule = module {
    single { GithubSourceCodeProvider.defaultHttpClient() }
    factoryOf(::FileManagerImpl) bind StarterProjectFileManager::class
    factoryOf(::StarterProjectsRepositoryImpl) bind StarterProjectsRepository::class
    single {
        GithubSourceCodeProvider(
            fileManager = get(),
            httpClient = get(),
        )
    } bind StarterProjectSourceCodeProvider::class
}

fun closeGeneratorResources() {
    runCatching {
        KoinPlatform.getKoin().get<HttpClient>().close()
    }
}
