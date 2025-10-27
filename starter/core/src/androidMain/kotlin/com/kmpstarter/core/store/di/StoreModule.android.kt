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

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory
import com.kmpstarter.core.KmpStarter
import com.kmpstarter.core.store.KmpInAppReview
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformStoreModule: Module = module {
    single {
        ReviewManagerFactory.create(KmpStarter.PLATFORM_HOST as Activity)
    }
    single {
        KmpInAppReview(
            activity = KmpStarter.PLATFORM_HOST as Activity,
            manager = get()
        )
    }
}