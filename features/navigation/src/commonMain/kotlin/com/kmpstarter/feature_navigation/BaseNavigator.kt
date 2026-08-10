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

package com.kmpstarter.feature_navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

@DslMarker
annotation class StarterNavigatorDsl // just to highlight code in IDE


class BackstackAlreadyProvided :
    IllegalStateException("Backstack must be provided once. make sure you aren't calling navigator.provideBackStack function more than once")

@Suppress("DSL_MARKER_APPLIED_TO_WRONG_TARGET")
@StarterNavigatorDsl
/**
 * Base implementation for navigation operations.
 *
 * Holds reference to navigation back stack and provides common APIs for
 * pushing, removing, and replacing destinations.
 *
 * Before any navigation method can be used, a back stack must be provided
 * via [provideBackStack].
 *
 * ## Creating a navigator
 *
 * Extend this class to expose navigation APIs specific to your application.
 *
 * ```kotlin
 * class AppNavigator : BaseNavigator() {
 *
 *    companion object {
 *         @Composable
 *         fun getCurrent(): AppNavigator {
 *             return koinInject()
 *         }
 *     }
 *
 *     fun openHome() {
 *         navigateTo(HomeScreen)
 *     }
 *
 *     fun openSettings() {
 *         navigateTo(SettingsScreen)
 *     }
 * }
 * ```
 *
 * The navigator can then be provided through your preferred dependency
 * injection solution (such as Koin) or any other state holder.
 */
abstract class BaseNavigator {

    /**
     * Current navigation back stack.
     *
     * Accessible to subclasses after initialization through
     * [provideBackStack].
     */
    protected lateinit var backStack: NavBackStack<NavKey>
        private set

    /**
     * Provides navigation back stack used by this navigator.
     *
     * Must be called before invoking any navigation operation.
     *
     * Child implementations may override this method
     *
     *
     * @param backStack Navigation back stack to operate on.
     */
    @StarterNavigatorDsl
    open fun provideBackStack(
        backStack: NavBackStack<NavKey>,
    ) {
        _setBackStack(backStack = backStack)
    }

    /**
     * Sets navigator back stack.
     *
     * Intended only for subclasses of [BaseNavigator]. Call this during navigator
     * initialization so navigation operations can access correct [NavBackStack].
     *
     * This is part of navigator's internal setup and should not be exposed or
     * called from outside inheritors.
     *
     * @param backStack Back stack instance used by this navigator.
     */
    @StarterNavigatorDsl
    protected fun _setBackStack(
        backStack: NavBackStack<NavKey>,
    ) {
        this.backStack = backStack
    }

    /**
     * Returns `true` if a back stack has been provided.
     */
    val isInitialized: Boolean
        get() = ::backStack.isInitialized

    /**
     * Pushes [route] onto top of back stack.
     *
     * No-op if navigator has not been initialized.
     *
     * @param route Destination to navigate to.
     */
    @StarterNavigatorDsl
    fun navigateTo(route: NavKey) {
        if (!isInitialized) return
        backStack.add(route)
    }

    /**
     * Removes current destination, then navigates to [route].
     *
     * Equivalent to calling [navigateUp] followed by [navigateTo].
     *
     * @param route Destination to navigate to.
     */
    @StarterNavigatorDsl
    fun popAndNavigate(route: NavKey) {
        navigateUp()
        navigateTo(route)
    }

    /**
     * Clears entire back stack, then navigates to [route].
     *
     * No-op if navigator has not been initialized.
     *
     * @param route Destination to become root destination.
     */
    @StarterNavigatorDsl
    fun popAllAndNavigate(route: NavKey) {
        if (!isInitialized) return
        backStack.clear()
        navigateTo(route)
    }

    /**
     * Navigates to [route], ensuring only one instance exists in back stack.
     *
     * If [route] already exists, previous instance is removed before
     * navigating to it again, effectively bringing it to top.
     *
     * No-op if navigator has not been initialized.
     *
     * @param route Destination to bring to top.
     */
    @StarterNavigatorDsl
    fun navigateOrBringToTop(route: NavKey) {
        if (!isInitialized) return

        val index = backStack.indexOf(route)
        if (index != -1) {
            backStack.removeAt(index)
        }

        navigateTo(route)
    }

    /**
     * Removes [route] from back stack.
     *
     * No-op if navigator has not been initialized.
     *
     * @param route Destination to remove.
     */
    @StarterNavigatorDsl
    fun remove(route: NavKey) {
        if (!isInitialized) return
        backStack.remove(route)
    }

    /**
     * Removes current destination from top of back stack.
     *
     * No-op if navigator has not been initialized or back stack is empty.
     */
    @StarterNavigatorDsl
    fun navigateUp() {
        if (!isInitialized) return
        backStack.removeLastOrNull()
    }
}