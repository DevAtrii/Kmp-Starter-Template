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
import com.kmpstarter.generator_domain.ProjectMode
import com.kmpstarter.generator_domain.StarterJson
import com.kmpstarter.generator_domain.StarterModules
import com.kmpstarter.generator_domain.StarterProjectsRepository
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.STARTER_JSON_FILE
import kotlinx.serialization.json.Json

class ExcludeViewModel(
    private val repository: StarterProjectsRepository,
    private val fileManager: StarterProjectFileManager,
) : ViewModel() {

    suspend fun exclude(
        dir: String,
        module: StarterModules,
        mode: ProjectMode?,
        targetModule: String,
    ): Result<Unit> = runCatching {
        val workingDir = CliPaths.resolve(fileManager.getCurrentDir(), dir)
        val resolvedMode = mode ?: readStarterJson(workingDir)?.mode
            ?: error("Project mode is required. Pass --mode or create starter.json with init.")

        repository.excludeModule(
            workingDir = workingDir,
            module = module,
            mode = resolvedMode,
            targetModule = targetModule,
        ).getOrThrow()
    }

    suspend fun readStarterJson(dir: String): StarterJson? {
        val path = CliPaths.resolve(fileManager.getCurrentDir(), "$dir/$STARTER_JSON_FILE")
        val bytes = fileManager.getFile(path).getOrNull() ?: return null
        return Json.decodeFromString<StarterJson>(bytes.decodeToString())
    }
}
