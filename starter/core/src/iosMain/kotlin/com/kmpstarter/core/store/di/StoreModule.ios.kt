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

package com.kmpstarter.core.store.di

import com.kmpstarter.core.store.KmpInAppReview
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformStoreModule: Module = module {
    single {
        KmpInAppReview()
    }
}