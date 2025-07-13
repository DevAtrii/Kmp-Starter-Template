package com.kmpstarter.core.utils.di

import com.kmpstarter.core.utils.datastore.AppDataStore
import org.koin.dsl.module

actual val platformUtilsModule =  module {
    single {
        AppDataStore()
    }
}