/*
 *
 *  *
 *  *  * Copyright (c) 2025
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

package com.kmpstarter.core.utils.logging

import com.kmpstarter.core.AppConstants
import org.lighthousegames.logging.logging

object Logger {
    const val APP_TAG = "[KMP_STARTER]"
}

val log = logging(
    tag = Logger.APP_TAG
)


object Log {

    init {
        log.d {
            "DEBUGGER MODE: ${AppConstants.IS_DEBUG}"
        }
    }

    fun d(tag: String?, message: Any?) {
        if (!AppConstants.IS_DEBUG)
            return

        log.d(
            tag = tag ?: Logger.APP_TAG
        ) {
            message
        }
    }

    fun d(tag: String?, vararg message: Any?) {
        if (!AppConstants.IS_DEBUG)
            return

        log.d(
            tag = tag ?: Logger.APP_TAG
        ) {
            val str = message.map {
                it.toString()
            }
            str.joinToString()
        }
    }

    fun e(tag: String?, vararg message: Any?) {
        if (!AppConstants.IS_DEBUG)
            return

        log.e(
            tag = tag ?: Logger.APP_TAG
        ) {
            val str = message.map {
                it.toString()
            }
            str.joinToString()
        }
    }

    fun i(tag: String?, message: Any?) {
        if (!AppConstants.IS_DEBUG)
            return

        log.i(
            tag = tag ?: Logger.APP_TAG
        ) {
            message
        }
    }


    fun i(tag: String?, vararg message: Any?) {
        if (!AppConstants.IS_DEBUG)
            return

        log.i(
            tag = tag ?: Logger.APP_TAG
        ) {
            val str = message.map {
                it.toString()
            }
            str.joinToString()
        }
    }

}