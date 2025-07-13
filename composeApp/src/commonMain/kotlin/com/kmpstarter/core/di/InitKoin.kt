package com.kmpstarter.core.di

import com.kmpstarter.core.db.di.databaseModule
import com.kmpstarter.core.events.di.eventsModule
import com.kmpstarter.core.utils.di.utilsModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
           coreModule,
            /*Todo add modules here*/
        )
    }
}