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
    private val providerMap: Map<AnalyticsProviderId, AnalyticsProvider>,
    private val registrationOrder: List<AnalyticsProviderId>,
    private val activeIds: MutableStateFlow<List<AnalyticsProviderId>>,
    routed: CompositeEventsTracker,
) : Analytics, EventsTracker by routed {

    override val availableProviders: List<AnalyticsProviderId>
        get() = registrationOrder

    override val activeProviders: List<AnalyticsProviderId>
        get() = activeIds.value

    override fun provider(id: AnalyticsProviderId): AnalyticsProvider {
        return providerMap[id]
            ?: throw IllegalArgumentException("Unknown analytics provider: ${id.value}")
    }

    override fun combine(vararg ids: AnalyticsProviderId): EventsTracker {
        val selected = resolve(ids.toList())
        return CompositeEventsTracker { selected }
    }

    override fun setActiveProviders(vararg ids: AnalyticsProviderId) {
        activeIds.value = resolveIds(ids.toList())
    }

    private fun resolve(ids: List<AnalyticsProviderId>): List<AnalyticsProvider> {
        return resolveIds(ids).map { providerMap.getValue(it) }
    }

    private fun resolveIds(ids: List<AnalyticsProviderId>): List<AnalyticsProviderId> {
        val seen = HashSet<AnalyticsProviderId>(ids.size)
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
        fun create(providers: List<AnalyticsProvider>): AnalyticsRouter {
            val seen = HashSet<AnalyticsProviderId>(providers.size)
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
