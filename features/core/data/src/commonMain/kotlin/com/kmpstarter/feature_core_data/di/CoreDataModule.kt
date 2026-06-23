/*
 *
 *  *
 *  *  * Copyright (c) 2026
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

package com.kmpstarter.feature_core_data.di

import com.kmpstarter.feature_core_data.repositories.OnboardingRepositoryImpl
import com.kmpstarter.feature_core_data.repositories.TripRepositoryImpl
import com.kmpstarter.feature_core_domain.inference.InferenceEngine
import com.kmpstarter.feature_core_domain.repositories.OnboardingRepository
import com.kmpstarter.feature_core_domain.repositories.TripRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    singleOf(::OnboardingRepositoryImpl) bind OnboardingRepository::class
    singleOf(::InferenceEngine)
    singleOf(::TripRepositoryImpl) bind TripRepository::class
}