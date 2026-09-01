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
import com.kmpstarter.generator_data.interfaces.StarterProjectSourceCodeProvider
import com.kmpstarter.generator_domain.ProjectMode
import com.kmpstarter.generator_domain.StarterJson
import com.kmpstarter.generator_domain.StarterProjectsRepository
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.DEFAULT_PACKAGE_NAME
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.STARTER_JSON_FILE
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class InitResult(
    val starterJsonPath: String,
    val adopted: Boolean,
)

class InitViewModel(
    private val fileManager: StarterProjectFileManager,
    private val sourceCodeProvider: StarterProjectSourceCodeProvider,
    private val repository: StarterProjectsRepository,
) : ViewModel() {

    suspend fun init(
        dir: String,
        packageName: String,
        mode: ProjectMode,
        starterVersion: String?,
        sourceZipPath: String? = null,
    ): Result<InitResult> = runCatching {
        val workingDir = CliPaths.resolve(fileManager.getCurrentDir(), dir)
        val zip = sourceZipPath?.let { CliPaths.resolve(fileManager.getCurrentDir(), it) }

        if (fileManager.getFile("$workingDir/settings.gradle.kts").isSuccess) {
            repository.adoptExistingProject(
                workingDir = workingDir,
                mode = mode,
                packageName = packageName,
                sourceZipPath = zip,
                starterVersion = starterVersion,
            ).getOrThrow()
            return@runCatching InitResult(
                starterJsonPath = "$workingDir/$STARTER_JSON_FILE",
                adopted = true,
            )
        }

        val version = starterVersion ?: sourceCodeProvider.getSourceCode().getOrThrow().version
        val starterJson = StarterJson(
            packageName = packageName,
            starterVersion = version,
            mode = mode,
        )
        val path = "$workingDir/$STARTER_JSON_FILE"
        fileManager.writeFile(
            path = path,
            content = Json.encodeToString(starterJson).encodeToByteArray(),
        ).getOrThrow()

        InitResult(starterJsonPath = path, adopted = false)
    }

    suspend fun defaultStarterVersion(): String =
        sourceCodeProvider.getSourceCode().getOrThrow().version

    fun defaultPackageName(): String = DEFAULT_PACKAGE_NAME
}
