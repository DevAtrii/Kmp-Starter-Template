package com.kmpstarter.core.utils.di

import com.kmpstarter.core.utils.datastore.AppDataStore
import com.kmpstarter.core.utils.intents.IntentUtils
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformUtilsModule = module {
    singleOf(::AppDataStore)
    singleOf(::IntentUtils)
}