package com.kmpstarter.core.events.di

import com.kmpstarter.core.datastore.onboarding.OnboardingDataStore
import com.kmpstarter.core.datastore.theme.ThemeDataStore
import com.kmpstarter.core.events.navigator.DefaultNavigator
import com.kmpstarter.core.events.navigator.interfaces.Navigator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val eventsModule = module {
    singleOf(::ThemeDataStore)
    singleOf(::OnboardingDataStore)
    singleOf(::DefaultNavigator).bind<Navigator>()
}