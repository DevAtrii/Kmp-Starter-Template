package com.kmpstarter.core.events.di

import com.kmpstarter.core.events.OnBoardingEvents
import com.kmpstarter.core.events.ThemeEvents
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val eventsModule  = module {
singleOf(::ThemeEvents)
singleOf(::OnBoardingEvents)
}