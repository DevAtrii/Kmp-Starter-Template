/*
 *
 *  *
 *  *  * Copyright (c) 2025
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

package com.kmpstarter.starter_features.auth.di

import com.kmpstarter.starter_features.auth.data.repository.AuthRepositoryImpl
import com.kmpstarter.starter_features.auth.domain.repository.AuthRepository
import com.kmpstarter.starter_features.auth.presentation.viewmodels.AuthViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    single {
        AuthViewModel(
            repository = get(),
            navigator = get(),
        )
    }
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}