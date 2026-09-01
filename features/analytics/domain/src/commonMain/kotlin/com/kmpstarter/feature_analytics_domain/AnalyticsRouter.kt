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

import kotlinx.coroutines.flow.MutableStateFlow

internal class AnalyticsRouter(
    private val providerMap: Map<StarterAnalyticsProviderId, StarterAnalyticsProvider>,
    private val registrationOrder: List<StarterAnalyticsProviderId>,
    private val activeIds: MutableStateFlow<List<StarterAnalyticsProviderId>>,
    routed: CompositeEventsTracker,
) : Analytics, EventsTracker by routed {

    override val availableProviders: List<StarterAnalyticsProviderId>
        get() = registrationOrder

    override val activeProviders: List<StarterAnalyticsProviderId>
        get() = activeIds.value

    override fun provider(id: StarterAnalyticsProviderId): StarterAnalyticsProvider {
        return providerMap[id]
            ?: throw IllegalArgumentException("Unknown analytics provider: ${id.value}")
    }

    override fun combine(vararg ids: StarterAnalyticsProviderId): EventsTracker {
        val selected = resolve(ids.toList())
        return CompositeEventsTracker { selected }
    }

    override fun setActiveProviders(vararg ids: StarterAnalyticsProviderId) {
        activeIds.value = resolveIds(ids.toList())
    }

    private fun resolve(ids: List<StarterAnalyticsProviderId>): List<StarterAnalyticsProvider> {
        return resolveIds(ids).map { providerMap.getValue(it) }
    }

    private fun resolveIds(ids: List<StarterAnalyticsProviderId>): List<StarterAnalyticsProviderId> {
        val seen = HashSet<StarterAnalyticsProviderId>(ids.size)
        for (id in ids) {
            require(id in providerMap) {
                "Unknown analytics provider: ${id.value}"
            }
            require(seen.add(id)) {
                "Duplicate analytics provider: ${id.value}"
            }
        }
        return ids
    }

    companion object {
        fun create(providers: List<StarterAnalyticsProvider>): AnalyticsRouter {
            val seen = HashSet<StarterAnalyticsProviderId>(providers.size)
            for (provider in providers) {
                require(seen.add(provider.id)) {
                    "Duplicate analytics provider: ${provider.id.value}"
                }
            }
            val providerMap = providers.associateBy { it.id }
            val registrationOrder = providers.map { it.id }
            val activeIds = MutableStateFlow(registrationOrder)
            val routed = CompositeEventsTracker {
                activeIds.value.map { providerMap.getValue(it) }
            }
            return AnalyticsRouter(
                providerMap = providerMap,
                registrationOrder = registrationOrder,
                activeIds = activeIds,
                routed = routed,
            )
        }
    }
}
