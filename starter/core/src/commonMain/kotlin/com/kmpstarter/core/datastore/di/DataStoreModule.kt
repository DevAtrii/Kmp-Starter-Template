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

package com.kmpstarter.core.datastore.di

import com.kmpstarter.core.datastore.common.CommonDataStore
import com.kmpstarter.core.datastore.onboarding.OnboardingDataStore
import com.kmpstarter.core.datastore.theme.ThemeDataStore
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val dataStoreModule = module {
    singleOf(::ThemeDataStore)
    singleOf(::OnboardingDataStore)
    singleOf(::CommonDataStore)
}