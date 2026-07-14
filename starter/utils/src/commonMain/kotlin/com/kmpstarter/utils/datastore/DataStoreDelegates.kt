package com.kmpstarter.utils.datastore

import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * Simple wrapper around a DataStore preference key.
 *
 * Example:
 * ```
 * val username = appDataStore.stringDataStore("username")
 *
 * username.set("Atrii")
 *
 * val value = username.get()
 *
 * username.flow.collect {
 *     println(it)
 * }
 *
 * username.clear()
 * ```
 */
class DataStoreDelegate<T>(
    private val appDataStore: AppDataStore,
    private val key: Preferences.Key<T>,
    private val default: T?,
) {

    /**
     * Flow of current value.
     */
    val flow: Flow<T?> = appDataStore.dataStore.data.map {
        it[key] ?: default
    }

    /**
     * Returns current value.
     */
    suspend fun get(): T? =
        appDataStore.dataStore.data
            .map { it[key] ?: default }
            .first()

    /**
     * Stores value.
     *
     * Passing `null` removes value from DataStore.
     */
    suspend fun set(value: T?) {
        appDataStore.dataStore.edit {
            if (value == null)
                it.remove(key)
            else
                it[key] = value
        }
    }

    /**
     * Removes stored value.
     */
    suspend fun clear() {
        appDataStore.dataStore.edit {
            it.remove(key)
        }
    }
}

/**
 * Wrapper for storing serializable objects in DataStore.
 *
 * Example:
 * ```
 * @Serializable
 * data class Settings(
 *     val darkMode: Boolean = false
 * )
 *
 * val settings = appDataStore.serializableDataStore(
 *     "settings",
 *     Settings()
 * )
 *
 * settings.set(Settings(true))
 *
 * val value = settings.get()
 *
 * settings.flow.collect {
 *     println(it)
 * }
 * ```
 */
class SerializableDataStoreDelegate<T>(
    private val delegate: DataStoreDelegate<String>,
    private val serializer: KSerializer<T>,
    private val default: T?,
    private val json: Json = Json.Default,
) {

    private val defaultJson = default?.let {
        json.encodeToString(serializer, it)
    }

    /**
     * Flow of decoded values.
     */
    val flow: Flow<T?> = delegate.flow.map(::decode)

    /**
     * Returns current value.
     */
    suspend fun get(): T? = decode(delegate.get())

    /**
     * Stores value.
     *
     * Passing `null` removes value from DataStore.
     */
    suspend fun set(value: T?) {
        delegate.set(
            value?.let {
                json.encodeToString(serializer, it)
            }
        )
    }

    /**
     * Removes stored value.
     */
    suspend fun clear() = delegate.clear()

    private fun decode(raw: String?): T? {
        if (raw == null)
            return default

        return runCatching {
            json.decodeFromString(serializer, raw)
        }.getOrDefault(default)
    }
}

/* ---------- Primitive factories ---------- */

/** Creates a String DataStore delegate. */
fun AppDataStore.stringDataStore(
    name: String,
    default: String? = null,
) = DataStoreDelegate(
    this,
    stringPreferencesKey(name),
    default
)

/** Creates an Int DataStore delegate. */
fun AppDataStore.intDataStore(
    name: String,
    default: Int? = null,
) = DataStoreDelegate(
    this,
    intPreferencesKey(name),
    default
)

/** Creates a Long DataStore delegate. */
fun AppDataStore.longDataStore(
    name: String,
    default: Long? = null,
) = DataStoreDelegate(
    this,
    longPreferencesKey(name),
    default
)

/** Creates a Boolean DataStore delegate. */
fun AppDataStore.booleanDataStore(
    name: String,
    default: Boolean? = null,
) = DataStoreDelegate(
    this,
    booleanPreferencesKey(name),
    default
)

/** Creates a Float DataStore delegate. */
fun AppDataStore.floatDataStore(
    name: String,
    default: Float? = null,
) = DataStoreDelegate(
    this,
    floatPreferencesKey(name),
    default
)

/** Creates a Double DataStore delegate. */
fun AppDataStore.doubleDataStore(
    name: String,
    default: Double? = null,
) = DataStoreDelegate(
    this,
    doublePreferencesKey(name),
    default
)

/** Creates a String Set DataStore delegate. */
fun AppDataStore.stringSetDataStore(
    name: String,
    default: Set<String>? = null,
) = DataStoreDelegate(
    this,
    stringSetPreferencesKey(name),
    default
)

/** Creates a ByteArray DataStore delegate. */
fun AppDataStore.byteArrayDataStore(
    name: String,
    default: ByteArray? = null,
) = DataStoreDelegate(
    this,
    byteArrayPreferencesKey(name),
    default
)

/* ---------- Serializable factory ---------- */

/**
 * Creates a DataStore delegate for any `@Serializable` type.
 */
inline fun <reified T> AppDataStore.serializableDataStore(
    name: String,
    default: T? = null,
    json: Json = Json.Default,
): SerializableDataStoreDelegate<T> {
    return SerializableDataStoreDelegate(
        delegate = stringDataStore(
            name = name,
            default = default?.let {
                json.encodeToString(serializer<T>(), it)
            }
        ),
        serializer = serializer<T>(),
        default = default,
        json = json
    )
}