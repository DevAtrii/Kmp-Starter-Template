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

import com.kmpstarter.generator_data.StarterProjectsRepositoryImpl
import com.kmpstarter.generator_data.impl.FileManagerImpl
import com.kmpstarter.generator_data.interfaces.SourceCode
import com.kmpstarter.generator_data.interfaces.StarterProjectSourceCodeProvider
import com.kmpstarter.generator_domain.ProjectMode
import com.kmpstarter.generator_domain.StarterJson
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InitViewModelTest {

    @Test
    fun initWritesStarterJsonForExistingProject() = runBlocking {
        val cwd = Files.createTempDirectory("starter-init")
        val project = Files.createDirectories(cwd.resolve("app"))
        val fileManager = object : com.kmpstarter.generator_data.interfaces.StarterProjectFileManager by FileManagerImpl() {
            override fun getCurrentDir() = cwd.toString()
        }
        val viewModel = InitViewModel(
            fileManager = fileManager,
            sourceCodeProvider = object : StarterProjectSourceCodeProvider {
                override suspend fun getSourceCode(version: String?) =
                    Result.success(SourceCode(version = version ?: "0.6.0", content = ByteArray(0)))
            },
            repository = StarterProjectsRepositoryImpl(
                fileManager = fileManager,
                sourceCodeProvider = object : StarterProjectSourceCodeProvider {
                    override suspend fun getSourceCode(version: String?) =
                        Result.success(SourceCode(version = version ?: "0.6.0", content = ByteArray(0)))
                },
            ),
        )

        val result = viewModel.init(
            dir = "app",
            packageName = "com.example.notes",
            mode = ProjectMode.LIB,
            starterVersion = "0.6.0",
        ).getOrThrow()

        assertTrue(result.starterJsonPath.endsWith("starter.json"))
        val parsed = Json.decodeFromString<StarterJson>(Files.readString(project.resolve("starter.json")))
        assertEquals("com.example.notes", parsed.packageName)
        assertEquals("0.6.0", parsed.starterVersion)
        assertEquals(ProjectMode.LIB, parsed.mode)
    }

    @Test
    fun defaultPackageNameIsTemplatePackage() {
        val viewModel = InitViewModel(
            fileManager = FileManagerImpl(),
            sourceCodeProvider = object : StarterProjectSourceCodeProvider {
                override suspend fun getSourceCode(version: String?) =
                    Result.success(SourceCode("0.6.0", ByteArray(0)))
            },
            repository = StarterProjectsRepositoryImpl(
                fileManager = FileManagerImpl(),
                sourceCodeProvider = object : StarterProjectSourceCodeProvider {
                    override suspend fun getSourceCode(version: String?) =
                        Result.success(SourceCode("0.6.0", ByteArray(0)))
                },
            ),
        )
        assertEquals("com.kmpstarter", viewModel.defaultPackageName())
    }
}
