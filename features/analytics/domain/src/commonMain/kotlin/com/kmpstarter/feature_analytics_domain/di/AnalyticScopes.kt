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

package com.kmpstarter.feature_analytics_domain.di

import com.kmpstarter.utils.di.ScopeProvider
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatform.getKoin


object AnalyticScopes {
    object MixPanel : ScopeProvider("mixpanel_scope") {
        override fun get(): Scope {
            val scope = getKoin().getOrCreateScope<MixPanel>(scopeId = scopeId)
            return scope
        }
    }

    object Firebase : ScopeProvider("firebase_scope") {
        override fun get(): Scope {
            val scope = getKoin().getOrCreateScope<Firebase>(scopeId = scopeId)
            return scope
        }
    }

}