package com.kmpstarter.core.events.navigator.interfaces

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.flow.Flow

interface Navigator {
    val navigationActions: Flow<NavigationAction>

    suspend fun navigate(
        destination: Any,
        navOptions: NavOptionsBuilder.() -> Unit = {},
    )

    suspend fun navigateTo(
        route: Any,
        popUpTo: Any? = null,
        inclusive: Boolean = false,
    )

    suspend fun navigateUp()
}