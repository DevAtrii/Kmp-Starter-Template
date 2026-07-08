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

package com.kmpstarter.feature_core_presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.kmpstarter.feature_core_domain.logics.onboarding.CheckIsOnboardedLogic
import org.koin.compose.koinInject

@Composable
fun rememberIsOnboarded(): State<Boolean> {
    val checkIsOnboardedLogic: CheckIsOnboardedLogic = koinInject()

    return produceState(initialValue = false) {
        value = checkIsOnboardedLogic()
    }

}