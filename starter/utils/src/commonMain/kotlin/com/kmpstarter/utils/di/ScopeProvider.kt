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

package com.kmpstarter.utils.di

import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID
import org.koin.mp.KoinPlatform.getKoin

abstract class ScopeProvider(
    val scopeId: ScopeID,
) {

    protected fun qualifier() = named(scopeId)

    open fun get(): Scope {
        val scope = getKoin().getOrCreateScope(scopeId = scopeId, qualifier = named(scopeId))
        return scope
    }


}



















