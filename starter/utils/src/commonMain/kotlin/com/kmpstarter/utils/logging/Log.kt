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

package com.kmpstarter.utils.logging

import com.diamondedge.logging.logging
import com.kmpstarter.utils.logging.StarterLogger.log

object StarterLogger {

    const val APP_TAG = "[KMP_STARTER]"

    enum class LogLevel {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        OFF
    }

    private var level = LogLevel.DEBUG

    fun setLogLevel(level: LogLevel) {
        this.level = level
    }

    internal fun shouldLog(logLevel: LogLevel): Boolean {
        if (level == LogLevel.OFF) return false

        return logLevel.ordinal >= level.ordinal
    }

    internal val log = logging(
        tag = APP_TAG
    )
}


object Log {

    fun v(tag: String? = null, message: Any?) {
        if (!StarterLogger.shouldLog(StarterLogger.LogLevel.VERBOSE)) return

        log.v(
            tag = tag ?: StarterLogger.APP_TAG
        ) {
            message
        }
    }

    fun v(tag: String? = null, vararg message: Any?) {
        if (!StarterLogger.shouldLog(StarterLogger.LogLevel.VERBOSE)) return

        log.v(
            tag = tag ?: StarterLogger.APP_TAG
        ) {
            message.joinToString()
        }
    }

    fun d(tag: String? = null, message: Any?) {
        if (!StarterLogger.shouldLog(StarterLogger.LogLevel.DEBUG)) return

        log.d(
            tag = tag ?: StarterLogger.APP_TAG
        ) {
            message
        }
    }

    fun d(tag: String? = null, vararg message: Any?) {
        if (!StarterLogger.shouldLog(StarterLogger.LogLevel.DEBUG)) return

        log.d(
            tag = tag ?: StarterLogger.APP_TAG
        ) {
            message.joinToString()
        }
    }

    fun i(tag: String? = null, message: Any?) {
        if (!StarterLogger.shouldLog(StarterLogger.LogLevel.INFO)) return

        log.i(
            tag = tag ?: StarterLogger.APP_TAG
        ) {
            message
        }
    }

    fun i(tag: String? = null, vararg message: Any?) {
        if (!StarterLogger.shouldLog(StarterLogger.LogLevel.INFO)) return

        log.i(
            tag = tag ?: StarterLogger.APP_TAG
        ) {
            message.joinToString()
        }
    }

    fun w(tag: String? = null, message: Any?) {
        if (!StarterLogger.shouldLog(StarterLogger.LogLevel.WARN)) return

        log.w(
            tag = tag ?: StarterLogger.APP_TAG
        ) {
            message
        }
    }

    fun w(tag: String? = null, vararg message: Any?) {
        if (!StarterLogger.shouldLog(StarterLogger.LogLevel.WARN)) return

        log.w(
            tag = tag ?: StarterLogger.APP_TAG
        ) {
            message.joinToString()
        }
    }

    fun e(tag: String? = null, message: Any?) {
        if (!StarterLogger.shouldLog(StarterLogger.LogLevel.ERROR)) return

        log.e(
            tag = tag ?: StarterLogger.APP_TAG
        ) {
            message
        }
    }

    fun e(tag: String? = null, vararg message: Any?) {
        if (!StarterLogger.shouldLog(StarterLogger.LogLevel.ERROR)) return

        log.e(
            tag = tag ?: StarterLogger.APP_TAG
        ) {
            message.joinToString()
        }
    }
}