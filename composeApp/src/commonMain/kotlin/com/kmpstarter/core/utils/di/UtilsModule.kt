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

package com.kmpstarter.core.utils.di

import org.koin.core.module.Module
import org.koin.dsl.module

val commonUtilsModule = module {

}

expect val platformUtilsModule: Module

val utilsModule = module {
    includes(
        commonUtilsModule,
        platformUtilsModule
    )
}