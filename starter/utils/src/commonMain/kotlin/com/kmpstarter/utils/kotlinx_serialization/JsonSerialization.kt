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

package com.kmpstarter.utils.kotlinx_serialization

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Recursively converts any supported Kotlin type into a [JsonElement].
 *
 * Supported types:
 * - **Nulls** -> [JsonNull]
 * - **JsonElement** -> Returns itself
 * - **Maps** -> [JsonObject] (keys stringified)
 * - **Iterables (List, Set)** -> [JsonArray]
 * - **Arrays (Object & Primitive)** -> [JsonArray]
 * - **Sequences** -> [JsonArray]
 * - **Primitives (Number, Boolean, String, Char)** -> [JsonPrimitive]
 * - **Others** -> Fallback to [JsonPrimitive] via `.toString()`
 *
 * @return A [JsonElement] representation of the object.
 */
fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is JsonElement -> this
    is Map<*, *> -> JsonObject(this.entries.associate { it.key.toString() to it.value.toJsonElement() })
    is Iterable<*> -> JsonArray(this.map { it.toJsonElement() })
    is Array<*> -> JsonArray(this.map { it.toJsonElement() })
    is IntArray -> JsonArray(this.map { JsonPrimitive(it) })
    is LongArray -> JsonArray(this.map { JsonPrimitive(it) })
    is DoubleArray -> JsonArray(this.map { JsonPrimitive(it) })
    is FloatArray -> JsonArray(this.map { JsonPrimitive(it) })
    is BooleanArray -> JsonArray(this.map { JsonPrimitive(it) })
    is Sequence<*> -> JsonArray(this.map { it.toJsonElement() }.toList())
    is Number -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Char -> JsonPrimitive(this.toString())
    else -> JsonPrimitive(this.toString())
}
















