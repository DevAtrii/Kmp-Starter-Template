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

package com.kmpstarter.feature_notifications_local

import com.tweener.alarmee.model.Alarmee
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface StarterNotificationsManager {

    companion object Companion {
        @OptIn(ExperimentalUuidApi::class)
        fun randomUuid() = Uuid.random().toHexString()
    }

    val scheduledUUIDs: Flow<Set<String>>

    /**
     * Schedules an alarm to be triggered at a specific time of the day. When the alarm is triggered, a notification will be displayed.
     *
     * @param alarmee The [Alarmee] object containing the configuration for the alarm.
     */
    fun schedule(alarmee: Alarmee)

    /**
     * Sends a notification immediately to the device without scheduling an alarm.
     *
     * @param alarmee The [Alarmee] object containing the configuration for the alarm.
     */
    fun immediate(alarmee: Alarmee)

    /**
     * Cancels an existing alarm based on its unique identifier.
     * If an alarm with the specified identifier is found, it will be canceled, preventing any future notifications from being triggered for that alarm.
     *
     * @param uuid The unique identifier for the alarm to be canceled.
     */
    fun cancel(uuid: String)


}

