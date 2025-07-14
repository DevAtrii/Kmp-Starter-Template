package com.kmpstarter.features.auth.di

import com.kmpstarter.features.auth.data.repository.AuthRepositoryImpl
import com.kmpstarter.features.auth.domain.repository.AuthRepository
import com.kmpstarter.features.auth.presentation.viewmodels.AuthViewModel
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