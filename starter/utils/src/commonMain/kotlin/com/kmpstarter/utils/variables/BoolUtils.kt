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

package com.kmpstarter.utils.variables

infix fun <T> Boolean.ifTrue(value: T): T? = if (this) value else null
infix fun <T> Boolean.ifFalse(value: T): T? = if (!this) value else null

infix fun <T> Boolean.ifTrue(action: () -> Unit)  = if (this) action() else null
infix fun <T> Boolean.ifFalse(action: () -> Unit)  = if (!this) action() else null


fun Boolean.toggle() = !this

