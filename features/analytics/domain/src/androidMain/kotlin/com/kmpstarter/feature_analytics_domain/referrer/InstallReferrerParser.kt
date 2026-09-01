package com.kmpstarter.feature_analytics_domain.referrer

import java.net.URLDecoder

/**
 * Turns a Play [InstallReferrerSnapshot] into event params + user properties.
 *
 * Event map: raw referrer, Play timestamps, then known UTM / click ids, then
 * any extra query keys. Capped at [MAX_EVENT_PROPERTIES] (Firebase's per-event
 * param limit). Zero timestamps and blank strings are dropped.
 *
 * User properties: known campaign keys + `install_version` only — not the raw
 * referrer string (too long for Firebase's 36-char user-property values).
 */
internal object InstallReferrerParser {
    private const val MAX_EVENT_PROPERTIES = 25

    private val knownQueryKeys = listOf(
        "utm_source",
        "utm_medium",
        "utm_campaign",
        "utm_term",
        "utm_content",
        "utm_id",
        "gclid",
        "gbraid",
        "wbraid",
    )
    private val knownQueryKeySet = knownQueryKeys.toSet()
    private val userPropertyKeys = knownQueryKeys + "install_version"

    fun eventProperties(snapshot: InstallReferrerSnapshot): Map<String, String> {
        val out = LinkedHashMap<String, String>(MAX_EVENT_PROPERTIES)
        fun put(key: String, value: Any?) {
            if (out.size >= MAX_EVENT_PROPERTIES) return
            when (value) {
                null -> return
                is String -> if (value.isNotBlank()) out[key] = value
                is Long -> if (value != 0L) out[key] = value.toString()
                is Boolean -> out[key] = value.toString()
                else -> out[key] = value.toString()
            }
        }

        put("install_referrer", snapshot.installReferrer)
        put("referrer_click_ts", snapshot.referrerClickTs)
        put("install_begin_ts", snapshot.installBeginTs)
        put("referrer_click_server_ts", snapshot.referrerClickServerTs)
        put("install_begin_server_ts", snapshot.installBeginServerTs)
        put("google_play_instant", snapshot.googlePlayInstant)
        put("install_version", snapshot.installVersion)

        val query = parseQuery(snapshot.installReferrer)
        if (query.isNotEmpty()) {
            knownQueryKeys.forEach { key -> put(key, query[key]) }
            query.forEach { (key, value) ->
                if (key !in knownQueryKeySet) put(key, value)
            }
        }
        return out
    }

    fun userProperties(eventProps: Map<String, String>): Map<String, String> {
        val out = LinkedHashMap<String, String>(userPropertyKeys.size)
        userPropertyKeys.forEach { key ->
            val value = eventProps[key]?.takeIf { it.isNotBlank() } ?: return@forEach
            out[key] = value
        }
        return out
    }

    internal fun parseQuery(raw: String): Map<String, String> {
        val query = normalizeQuery(raw)
        if (query.isEmpty()) return emptyMap()
        val out = LinkedHashMap<String, String>()
        query.split('&').forEach { part ->
            if (part.isBlank()) return@forEach
            val eq = part.indexOf('=')
            if (eq <= 0) return@forEach
            val key = decode(part.substring(0, eq)).trim()
            val value = decode(part.substring(eq + 1)).trim()
            if (key.isNotEmpty() && value.isNotEmpty()) out[key] = value
        }
        return out
    }

    /**
     * Play usually returns a raw query string. Some campaigns percent-encode
     * the whole thing (`utm_source%3Dgoogle%26…`) so there is no `=` until
     * decoded. A leading `?` is stripped.
     */
    private fun normalizeQuery(raw: String): String {
        val trimmed = raw.trim().removePrefix("?")
        if (trimmed.isEmpty()) return ""
        if (trimmed.contains('=')) return trimmed
        val decoded = decode(trimmed)
        return if (decoded.contains('=')) decoded else ""
    }

    private fun decode(value: String): String =
        runCatching { URLDecoder.decode(value, Charsets.UTF_8.name()) }.getOrDefault(value)
}
