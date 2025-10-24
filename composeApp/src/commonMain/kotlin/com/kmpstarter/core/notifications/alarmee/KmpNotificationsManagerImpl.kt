/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Author: Athar Gul
 *  * GitHub: https://github.com/DevAtrii
 *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *
 *  * All rights reserved.
 *
 *
 */

package com.kmpstarter.core.notifications.alarmee

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.kmpstarter.core.utils.datastore.AppDataStore
import com.kmpstarter.core.utils.logging.Log
import com.tweener.alarmee.LocalNotificationService
import com.tweener.alarmee.model.Alarmee
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.collections.get

class KmpNotificationsManagerImpl(
    private val localNotificationService: LocalNotificationService,
    private val appDataStore: AppDataStore,
) : KmpNotificationsManager {

    companion object {
        private val KEY_SCHEDULED_ALARM_UUIDS = stringSetPreferencesKey("kmp_starter_alarms_uuids")
    }

    private val dataStore = appDataStore.dataStore
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())


    override val scheduledUUIDs: Flow<Set<String>> = dataStore.data.map {
        it[KEY_SCHEDULED_ALARM_UUIDS] ?: emptySet()
    }

    suspend fun addScheduledAlarmUUID(uuid: String) {
        dataStore.edit {
            val set = it[KEY_SCHEDULED_ALARM_UUIDS] ?: emptySet()
            it[KEY_SCHEDULED_ALARM_UUIDS] = set.plus(uuid)
        }
    }

    suspend fun removeScheduledAlarmUUID(uuid: String) {
        dataStore.edit {
            Log.d("APP_NOTIFICATIONS","Removing scheduled alarm UUID: $uuid")
            val set = it[KEY_SCHEDULED_ALARM_UUIDS] ?: emptySet()
            it[KEY_SCHEDULED_ALARM_UUIDS] = set.minus(uuid)
        }
    }

    override fun schedule(alarmee: Alarmee) {
        localNotificationService.schedule(
            alarmee = alarmee
        )
        coroutineScope.launch {
            addScheduledAlarmUUID(uuid = alarmee.uuid)
        }
    }

    override fun immediate(alarmee: Alarmee) = localNotificationService.immediate(
        alarmee = alarmee
    )

    override fun cancel(uuid: String) {
        localNotificationService.cancel(
            uuid = uuid
        )
        coroutineScope.launch {
            val scheduledUUIDs = scheduledUUIDs.first()
            if (uuid in scheduledUUIDs) {
                removeScheduledAlarmUUID(uuid)
            }
        }
    }
}



















