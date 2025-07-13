package com.kmpstarter.core.events.navigator.interfaces


import androidx.navigation.NavOptionsBuilder

sealed interface NavigationAction {
    data class Navigate(
        val destination: Any,
        val navOptions: NavOptionsBuilder.() -> Unit = {},
    ) : NavigationAction

    data object NavigateUp : NavigationAction
}
