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

package com.kmpstarter.feature_core_domain.di

import com.kmpstarter.feature_core_domain.logics.OnboardingLogics
import com.kmpstarter.feature_core_domain.logics.onboarding.CheckIsOnboardedLogic
import com.kmpstarter.feature_core_domain.logics.onboarding.SetOnboardedLogic
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreDomainModule = module {
    singleOf(::CheckIsOnboardedLogic)
    singleOf(::SetOnboardedLogic)
    singleOf(::OnboardingLogics)
}