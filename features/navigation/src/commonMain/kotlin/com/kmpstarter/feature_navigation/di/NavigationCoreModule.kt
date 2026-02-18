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

package com.kmpstarter.feature_navigation.di

import com.kmpstarter.feature_navigation.StarterNavigator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val navigationCoreModule = module {
    singleOf(::StarterNavigator)
}