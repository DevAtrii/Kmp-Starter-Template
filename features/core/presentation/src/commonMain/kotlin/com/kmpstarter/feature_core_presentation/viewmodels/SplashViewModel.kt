package com.kmpstarter.feature_core_presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.kmpstarter.feature_core_domain.logics.OnboardingLogics
import com.kmpstarter.ui_utils.viewmodels.DummyActions
import com.kmpstarter.ui_utils.viewmodels.MviViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
internal data class SplashState(
    val isOnboarded: Boolean = false,
    val duration: Long = Random.nextLong(1500, 2500),
) {
    init {
        require(duration > 500L) {
            "Duration must be greater than 500ms"
        }
    }
}

internal sealed class SplashEvents {
    data object OnFinish : SplashEvents()
}

internal class SplashViewModel(
    private val onboardingLogics: OnboardingLogics,
) : MviViewModel<SplashState, DummyActions, SplashEvents>() {

    companion object {
        private const val TAG = "SplashViewModel"
    }


    override val initialState: SplashState
        get() = SplashState()

    override fun onAction(action: DummyActions) {
        // do nothing
    }

    override fun onStateStart() {
        checkIfOnboarded()
        onSplashStarted()
    }

    private fun onSplashStarted() {
        viewModelScope.launch {
            delay(_state.value.duration)
            onSplashFinished()
        }
    }

    private suspend fun onSplashFinished() {
        emitEvent(SplashEvents.OnFinish)
        onboardingLogics.setOnboarded(true) // when using auth set it inside auth when signed in
    }

    private var checkIfOnboardedJob: Job? = null

    private fun checkIfOnboarded() {
        checkIfOnboardedJob?.cancel()
        checkIfOnboardedJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    isOnboarded = onboardingLogics.checkIsOnboarded()
                )
            }
        }
    }


}



















