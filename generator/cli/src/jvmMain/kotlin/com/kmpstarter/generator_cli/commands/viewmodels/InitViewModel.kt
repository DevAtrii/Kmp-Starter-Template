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
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.DEFAULT_PACKAGE_NAME
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.STARTER_JSON_FILE
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class InitResult(
    val starterJsonPath: String,
)

class InitViewModel(
    private val fileManager: StarterProjectFileManager,
    private val sourceCodeProvider: StarterProjectSourceCodeProvider,
) : ViewModel() {

    suspend fun init(
        dir: String,
        packageName: String,
        mode: ProjectMode,
        starterVersion: String?,
    ): Result<InitResult> = runCatching {
        val workingDir = CliPaths.resolve(fileManager.getCurrentDir(), dir)
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

        InitResult(starterJsonPath = path)
    }

    suspend fun defaultStarterVersion(): String =
        sourceCodeProvider.getSourceCode().getOrThrow().version

    fun defaultPackageName(): String = DEFAULT_PACKAGE_NAME
}
