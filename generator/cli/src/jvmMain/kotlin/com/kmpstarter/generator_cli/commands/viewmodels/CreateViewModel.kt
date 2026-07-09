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

package com.kmpstarter.generator_cli.commands.viewmodels

import androidx.lifecycle.ViewModel
import com.kmpstarter.generator_cli.util.CliPaths
import com.kmpstarter.generator_data.interfaces.StarterProjectFileManager
import com.kmpstarter.generator_domain.StarterProject
import com.kmpstarter.generator_domain.StarterProjectsRepository

data class CreateResult(
    val zipPath: String,
    val extractPath: String?,
)

class CreateViewModel(
    private val repository: StarterProjectsRepository,
    private val fileManager: StarterProjectFileManager,
) : ViewModel() {

    suspend fun create(
        project: StarterProject,
        outputZipPath: String,
        extract: Boolean,
    ): Result<CreateResult> = runCatching {
        val zipBytes = repository.generate(project = project).getOrThrow()
        val zipPath = CliPaths.resolve(
            baseDir = fileManager.getCurrentDir(),
            path = outputZipPath,
        )

        fileManager.writeFile(path = zipPath, content = zipBytes).getOrThrow()

        val extractPath = if (extract) {
            val outputDir = CliPaths.extractDirForZip(zipPath)
            fileManager.extractZip(path = zipPath, output = outputDir).getOrThrow()
            outputDir
        } else {
            null
        }

        CreateResult(
            zipPath = zipPath,
            extractPath = extractPath,
        )
    }
}
