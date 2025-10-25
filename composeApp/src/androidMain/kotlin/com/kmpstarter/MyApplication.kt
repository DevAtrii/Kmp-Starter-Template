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

package com.kmpstarter

import android.app.Application
import com.kmpstarter.core.di.initKoin
import com.kmpstarter.core.initKmpApp
import com.kmpstarter.feature_purchases.initRevenueCat
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKmpApp()
        initKoin {
            androidLogger()
            androidContext(this@MyApplication)
        }
        initRevenueCat()
    }

}

