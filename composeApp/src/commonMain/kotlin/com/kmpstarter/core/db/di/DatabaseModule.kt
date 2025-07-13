package com.kmpstarter.core.db.di

import com.kmpstarter.core.db.KmpStarterDatabase
import com.kmpstarter.core.db.getKmpDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformDatabaseModule: Module

val databaseModule = module {
    includes(platformDatabaseModule)
    single<KmpStarterDatabase> {
        getKmpDatabase(
            databaseProvider = get()
        )
    }
}

