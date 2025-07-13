package com.kmpstarter.core.events.navigator

import androidx.navigation.NavOptionsBuilder
import com.kmpstarter.core.events.navigator.interfaces.NavigationAction
import com.kmpstarter.core.events.navigator.interfaces.Navigator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


class DefaultNavigator : Navigator {
    private val _navigationActions = Channel<NavigationAction>()
    override val navigationActions = _navigationActions.receiveAsFlow()

    override suspend fun navigate(
        destination: Any,
        navOptions: NavOptionsBuilder.() -> Unit,
    ) {
        _navigationActions.send(
            NavigationAction.Navigate(
                destination = destination,
                navOptions = navOptions,
            ),
        )
    }

    override suspend fun navigateTo(
        route: Any,
        popUpTo: Any?,
        inclusive: Boolean,
    ) {
        _navigationActions.send(
            NavigationAction.Navigate(
                destination = route,
                navOptions = {
                    popUpTo?.let {
                        this.popUpTo(popUpTo) {
                            this.inclusive = inclusive
                        }
                    }
                },
            ),
        )
    }

    override suspend fun navigateUp() {
        _navigationActions.send(NavigationAction.NavigateUp)
    }
}

