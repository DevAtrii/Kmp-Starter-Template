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


    suspend fun getSourceCode(): Result<SourceCode>


}