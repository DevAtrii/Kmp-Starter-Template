package com.kmpstarter.core.events.di

import com.kmpstarter.core.events.OnBoardingEvents
import com.kmpstarter.core.events.ThemeEvents
import com.kmpstarter.core.events.navigator.DefaultNavigator
import com.kmpstarter.core.events.navigator.interfaces.Navigator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val eventsModule = module {
    singleOf(::ThemeEvents)
    singleOf(::OnBoardingEvents)
    singleOf(::DefaultNavigator).bind<Navigator>()
}