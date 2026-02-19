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

package com.kmpstarter.feature_remote_config_data.di

import com.kmpstarter.feature_remote_config_data.FirebaseRemoteConfigRepository
import com.kmpstarter.feature_remote_config_data._FirebaseRemoteConfigInitializer
import com.kmpstarter.feature_remote_config_domain.RemoteConfig
import com.kmpstarter.feature_remote_config_domain.RemoteConfigRepository
import com.kmpstarter.feature_remote_config_domain._RemoteConfigInitializer
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.remoteConfig
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val remoteConfigDataModule = module {
    single<_RemoteConfigInitializer>{
        _FirebaseRemoteConfigInitializer(
            remoteConfig = Firebase.remoteConfig
        )
    }
    single<RemoteConfigRepository>{
        FirebaseRemoteConfigRepository(
            remoteConfig = Firebase.remoteConfig
        )
    }
 }