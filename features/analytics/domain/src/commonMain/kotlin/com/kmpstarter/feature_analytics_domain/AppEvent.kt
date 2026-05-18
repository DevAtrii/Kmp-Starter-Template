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

package com.kmpstarter.feature_analytics_domain

/**
 * Base analytics event model.
 *
 * Recommended approach:
 * Create single sealed hierarchy in app's core/shared module
 * to manage all analytics events from one place.
 *
 * Example:
 *
 * ```kotlin
 * sealed class AppEvents(
 *     event: String,
 *     properties: Map<String, Any>? = null,
 * ) : AppEvent(event, properties) {
 *
 *     constructor(
 *         event: String,
 *     ) : this(
 *         event = event,
 *         properties = null,
 *     )
 *
 *     constructor(
 *         event: String,
 *         pair: Pair<String, Any>? = null,
 *     ) : this(
 *         event = event,
 *         properties = if (pair != null) mapOf(pair) else mapOf(),
 *     )
 *
 *     data object DummyEvent : AppEvents(
 *         event = "dummy_event",
 *     )
 * }
 * ```
 *
 * Usage:
 *
 * ```kotlin
 * eventsTracker.track(
 *     AppEvents.DummyEvent,
 * )
 * ```
 *
 * Benefits:
 * - Centralized analytics management
 * - Type-safe event definitions
 * - Consistent naming across app
 * - Easier analytics maintenance
 * - Better discoverability/autocomplete
 */
abstract class AppEvent(
    val event: String,
    val properties: Map<String, Any>? = null,
)