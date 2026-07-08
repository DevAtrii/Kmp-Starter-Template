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
) : StarterProjectSourceCodeProvider {
    override suspend fun getSourceCode(): Result<SourceCode> = runCatching {
        val arr = fileManager.getFile(
            fileManager.getCurrentDir() + "/Archive.zip"
        ).getOrThrow()

        SourceCode(
            version = "0.4.7",
            content = arr
        )
    }
}