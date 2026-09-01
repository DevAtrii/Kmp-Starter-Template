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
import com.kmpstarter.generator_domain.StarterModules
import com.kmpstarter.generator_domain.StarterProjectsRepository
import com.kmpstarter.generator_domain.UpgradeResult

class UpgradeViewModel(
    private val repository: StarterProjectsRepository,
    private val fileManager: StarterProjectFileManager,
) : ViewModel() {

    suspend fun upgrade(
        dir: String,
        module: StarterModules?,
        targetVersion: String?,
        force: Boolean,
        sourceZipPath: String?,
    ): Result<UpgradeResult> = runCatching {
        val workingDir = CliPaths.resolve(fileManager.getCurrentDir(), dir)
        repository.upgrade(
            workingDir = workingDir,
            modules = module?.let(::listOf),
            targetVersion = targetVersion,
            force = force,
            sourceZipPath = sourceZipPath?.let {
                CliPaths.resolve(fileManager.getCurrentDir(), it)
            },
        ).getOrThrow()
    }
}
