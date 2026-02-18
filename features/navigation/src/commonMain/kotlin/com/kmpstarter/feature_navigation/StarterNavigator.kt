/*
 *
 *  *
 *  *  * Copyright (c) 2025
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

package com.kmpstarter.feature_navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.koin.compose.koinInject

@DslMarker
annotation class StarterNavigatorDsl // just to highlight code in IDE

@StarterNavigatorDsl
class StarterNavigator {

    private lateinit var _backStack: NavBackStack<NavKey>

    companion object {
        @Composable
        fun getCurrent(): StarterNavigator {
            return koinInject<StarterNavigator>()
        }
    }

    @StarterNavigatorDsl
    fun provideBackStack(backStack: NavBackStack<NavKey>) {
        _backStack = backStack
    }

    @StarterNavigatorDsl
    fun navigateTo(route: NavKey) {
        if (!::_backStack.isInitialized) {
            return
        }

        _backStack.add(route)
    }

    @StarterNavigatorDsl
    fun popAndNavigate(route: NavKey) {
        navigateUp()
        navigateTo(route = route)
    }

    @StarterNavigatorDsl
    fun popAllAndNavigate(route: NavKey) {
        _backStack.clear()
        navigateTo(route = route)
    }

    @StarterNavigatorDsl
    fun navigateOrBringToTop(route: NavKey) {
        if (!::_backStack.isInitialized) {
            return
        }

        val index = _backStack.indexOf(route)

        if (index != -1) {
            _backStack.removeAt(index)
        }

        navigateTo(route)
    }


    @StarterNavigatorDsl
    fun remove(route: NavKey) {
        if (!::_backStack.isInitialized) {
            return
        }
        if (route in _backStack)
            _backStack.remove(route)
    }

    @StarterNavigatorDsl
    fun navigateUp() {
        if (!::_backStack.isInitialized) {
            return
        }

        val removed = _backStack.removeLastOrNull()
    }
}