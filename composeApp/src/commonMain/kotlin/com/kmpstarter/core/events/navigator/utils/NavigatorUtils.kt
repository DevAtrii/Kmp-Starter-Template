package com.kmpstarter.core.events.navigator.utils

import androidx.navigation.NavHostController
import com.kmpstarter.core.events.navigator.interfaces.NavigationAction

fun NavHostController.handleNavigationAction(action: NavigationAction) {
    when (action) {
        is NavigationAction.Navigate ->
            navigate(action.destination) {
                apply(action.navOptions)
            }

        NavigationAction.NavigateUp -> navigateUp()
    }
}
