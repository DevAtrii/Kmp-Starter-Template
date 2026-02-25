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

package com.kmpstarter.ui_utils.state_flows

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

context(viewmodel: ViewModel)
inline fun <reified T> SavedStateHandle.getMutableStateFlow(
    defaultValue: T,
    key: String? = null,
) = PropertyDelegateProvider<Any, ReadOnlyProperty<Any, MutableStateFlow<T>>> { thisRef, property ->
    val coroutineScope = viewmodel.viewModelScope
    val viewModelClassName = thisRef.let { it::class.simpleName } ?: "UnknownViewModel"
    val actualKey = key ?: "${viewModelClassName}_${property.name}"
    val json = Json { ignoreUnknownKeys = true }

    val stateFlow = MutableStateFlow(
        get<String>(actualKey)?.let { json.decodeFromString<T>(it) } ?: defaultValue
    ).also { flow ->
        flow.onEach { set(actualKey, json.encodeToString(it)) }.launchIn(coroutineScope)
    }

    ReadOnlyProperty { _, _ -> stateFlow }
}

context(viewmodel: ViewModel)
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> SavedStateHandle.getMutableStateFlow2(
    defaultValue: T,
    key: String? = null,
    transientProperties: Set<String> = emptySet(),
): PropertyDelegateProvider<Any, ReadOnlyProperty<Any, MutableStateFlow<T>>> {
    return PropertyDelegateProvider { owner, property ->
        val coroutineScope = viewmodel.viewModelScope

        // build a stable key for SavedStateHandle,
        val viewModelClassName = owner.let { it::class.simpleName } ?: "UnknownViewModel"
        val actualKey = key ?: "${viewModelClassName}_${property.name}"

        val serializer = serializer<T>()
        val json = Json { ignoreUnknownKeys = true }

        // ---------- initial value ----------
        val initial: T = get<String>(actualKey)?.let { stored ->
            if (transientProperties.isEmpty()) {
                // no transients in list, just decode the json
                runCatching { json.decodeFromString(serializer, stored) }
                    .getOrElse { defaultValue }
            } else {
                // transients in the list, merge with defaults
                val saved = json.parseToJsonElement(stored).jsonObject
                val fresh = json.encodeToJsonElement(serializer, defaultValue).jsonObject

                // rebuild JSON: keep everything except the transient keys,
                // then add the default values for those keys
                val patched = buildJsonObject {
                    saved.forEach { (k, v) -> if (k !in transientProperties) put(k, v) }
                    transientProperties.forEach { k -> fresh[k]?.let { put(k, it) } }
                }

                runCatching { json.decodeFromJsonElement(serializer, patched) }
                    .getOrElse { defaultValue }
            }
        } ?: defaultValue

        // ---------- Persist each StateFlow emission into SavedStateHandle ----------
        MutableStateFlow(initial).also { flow ->
            flow.onEach { value ->
                // initially encode all properties
                val element = json.encodeToJsonElement(serializer, value)
                //  then remove transient ones if "transientProperties" is not empty
                val cleanedObj = (element as? JsonObject)
                    ?.takeIf { transientProperties.isNotEmpty() }
                    ?.let { JsonObject(it.filterKeys { k -> k !in transientProperties }) }
                    ?: element

                // return the encoded value as SavedStateHandle key/value
                set(actualKey, cleanedObj.toString())
            }.launchIn(coroutineScope)
        }.let { ReadOnlyProperty { _, _ -> it } }
    }
}