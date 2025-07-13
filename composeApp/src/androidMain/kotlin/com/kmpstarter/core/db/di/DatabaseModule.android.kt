package com.kmpstarter.core.db.di

import com.kmpstarter.core.db.DatabaseProvider
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDatabaseModule: Module = module {
    single {
        DatabaseProvider(
            context = get()
        )
    }
}