package com.kmpstarter

import android.app.Application
import com.kmpstarter.core.di.initKoin
import com.kmpstarter.core.firebase.auth.AuthUtils
import com.kmpstarter.core.purchases.initRevenueCat
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthUtils.initGoogleAuthProvider()
        initKoin {
            androidLogger()
            androidContext(this@MyApplication)
        }
        initRevenueCat()
    }

}

