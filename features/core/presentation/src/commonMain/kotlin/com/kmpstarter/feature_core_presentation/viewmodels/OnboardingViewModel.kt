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

package com.kmpstarter.feature_core_presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.kmpstarter.feature_analytics_domain.AppEventsTracker
import com.kmpstarter.feature_core_domain.logics.OnboardingLogics
import com.kmpstarter.ui_utils.viewmodels.MviViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingState(
    val selectedTrafficSource: String? = null,
    val currentSlide: Int = 1,
    val totalSlides: Int = 3,
    val isOnboarded: Boolean = false,
)

sealed class OnboardingEvents {
    data object Finish : OnboardingEvents()
}

sealed class OnboardingActions {
    data object OnStepIncrement : OnboardingActions()
    data object OnStepDecrement : OnboardingActions()
    data object OnFinish : OnboardingActions()
    data class OnTrafficSourceChange(val value: String) : OnboardingActions()
}


class OnboardingViewModel(
    private val onboardingLogics: OnboardingLogics,
    private val eventsTracker: AppEventsTracker,
) : MviViewModel<OnboardingState, OnboardingActions, OnboardingEvents>() {

    companion object {
        private const val TAG = "OnboardingViewModel"
    }

    // jobs
    private var setOnboardedJob: Job? = null
    private var checkIsOnboardedJob: Job? = null


    init {
        checkIsOnboarded()
    }


    override val initialState: OnboardingState
        get() = OnboardingState()


    override fun onAction(action: OnboardingActions) {
        when (action) {
            OnboardingActions.OnStepIncrement -> onStepIncrement()
            OnboardingActions.OnStepDecrement -> onStepDecrement()
            OnboardingActions.OnFinish -> onFinish()
            is OnboardingActions.OnTrafficSourceChange -> onTrafficSourceChange(action.value)
        }
    }

    private fun onTrafficSourceChange(value: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedTrafficSource = value
                )
            }
        }
    }

    private fun checkIsOnboarded() {
        checkIsOnboardedJob?.cancel()
        checkIsOnboardedJob = viewModelScope.launch {
            val isOnboarded = onboardingLogics.checkIsOnboarded()
            _state.update {
                it.copy(
                    isOnboarded = isOnboarded
                )
            }
        }
    }

    private fun setOnboarded(value: Boolean) {
        setOnboardedJob?.cancel()
        setOnboardedJob = viewModelScope.launch {
            onboardingLogics.setOnboarded(value)
        }
    }

    private fun onFinish() {
        setOnboarded(true)
        viewModelScope.launch {
            eventsTracker.trackTrafficSource(source = _state.value.selectedTrafficSource ?: "--")
            emitEvent(OnboardingEvents.Finish)
        }
    }

    private fun onStepIncrement() {
        viewModelScope.launch {
            val currentSlide =
                (_state.value.currentSlide + 1).coerceAtMost(_state.value.totalSlides)

            _state.update {
                it.copy(
                    currentSlide = currentSlide
                )
            }
        }
    }

    private fun onStepDecrement() = _state.update {
        it.copy(
            currentSlide = (it.currentSlide - 1).coerceAtLeast(1)
        )
    }


}



















