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

package com.kmpstarter.feature_remote_config_domain.di

import com.kmpstarter.feature_remote_config_domain.logics.GetConfigLogic
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val remoteConfigDomainModule = module {
    singleOf(::GetConfigLogic)
}