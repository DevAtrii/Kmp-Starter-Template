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

package com.kmpstarter.generator_data.impl

import com.kmpstarter.generator_data.interfaces.SourceCode
import com.kmpstarter.generator_data.interfaces.StarterProjectFileManager
import com.kmpstarter.generator_data.interfaces.StarterProjectSourceCodeProvider

class LocalSourceCodeProvider(
    private val fileManager: StarterProjectFileManager,
    private val zipPath: String? = null,
) : StarterProjectSourceCodeProvider {
    override suspend fun getSourceCode(version: String?): Result<SourceCode> = runCatching {
        val path = zipPath ?: (fileManager.getCurrentDir() + "/Archive.zip")
        val arr = fileManager.getFile(path).getOrThrow()

        SourceCode(
            version = version ?: StarterProjectSourceCodeProvider.MAX_VERSION,
            content = arr
        )
    }
}
