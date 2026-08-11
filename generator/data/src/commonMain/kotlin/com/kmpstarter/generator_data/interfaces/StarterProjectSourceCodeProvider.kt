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

package com.kmpstarter.generator_data.interfaces

class SourceCode(
    val version: String,
    val content: ByteArray,
) {
    override fun toString(): String {
        return "SourceCode[version=$version, contentSize=${content.size}]"
    }
}

interface StarterProjectSourceCodeProvider {
    companion object {
        const val MIN_VERSION = "0.4.9"
        const val MAX_VERSION = "0.5.6"
    }

    suspend fun getSourceCode(version: String? = null): Result<SourceCode>


}