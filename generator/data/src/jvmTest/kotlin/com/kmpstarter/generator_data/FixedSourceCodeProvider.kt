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

package com.kmpstarter.generator_data

import com.kmpstarter.generator_data.interfaces.SourceCode
import com.kmpstarter.generator_data.interfaces.SourceVersionSupport
import com.kmpstarter.generator_data.interfaces.StarterProjectSourceCodeProvider

internal class FixedSourceCodeProvider(
    private val zip: ByteArray,
    private val version: String = "0.6.0",
    private val support: SourceVersionSupport = SourceVersionSupport(
        newestAvailable = version,
        maxSupported = version,
        cliSupportsLatest = true,
    ),
) : StarterProjectSourceCodeProvider {
    var lastRequestedVersion: String? = null
        private set

    override suspend fun getSourceCode(version: String?): Result<SourceCode> {
        lastRequestedVersion = version
        return Result.success(SourceCode(version = version ?: this.version, content = zip))
    }

    override suspend fun getSourceVersionSupport(): Result<SourceVersionSupport> =
        Result.success(support)
}
