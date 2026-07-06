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
import com.kmpstarter.generator_data.interfaces.StarterProjectFileManager
import com.kmpstarter.generator_data.interfaces.StarterProjectFileManager.Companion.getFileAs
import com.kmpstarter.generator_data.interfaces.StarterProjectSourceCodeProvider
import com.kmpstarter.generator_domain.ProjectMode
import com.kmpstarter.generator_domain.ProjectZip
import com.kmpstarter.generator_domain.StarterModules
import com.kmpstarter.generator_domain.StarterProject
import com.kmpstarter.generator_domain.StarterProjectsRepository
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.STARTER_FOLDER
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.TOOLING_SETTINGS_GRADLE_MODULES
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.TOOLING_SOURCE_CODE_FILES
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.TOOLING_SOURCE_CODE_FOLDERS

class StarterProjectsRepositoryImpl(
    private val fileManager: StarterProjectFileManager,
    private val sourceCodeProvider: StarterProjectSourceCodeProvider,
) : StarterProjectsRepository {
    override suspend fun generate(project: StarterProject): Result<ProjectZip> {
        // Step 0: Getting SourceCode
        val sourceCode = sourceCodeProvider.getSourceCode().getOrThrow()
        val sourceCodeZipBytes = sourceCode.content

        // Step 1: Extracting ZipFile
        val zipPath =
            "${project.workingDir}/$STARTER_FOLDER/source_code/${sourceCode.version}/code.zip"
        fileManager.writeFile(
            path = zipPath,
            content = sourceCodeZipBytes
        ).getOrThrow()
        fileManager.extractZip(path = zipPath, output = project.workingDir).getOrThrow()

        // Step 2: Remove tooling source code
        removeToolingSourceCode(project = project).getOrThrow()

        // Step 3:
        configureGitHubWorkflows(project = project).getOrThrow()

        // Step 4
        configureProjectModules(project = project, sourceCode = sourceCode).getOrThrow()

        // Step 5
        configureYourFeature(project = project).getOrThrow()
        // Step 6
        configureProjectName(project = project).getOrThrow()
        // Step 7
        configurePackageName(project = project).getOrThrow()
        // Step 8
        createStarterJson(project = project, sourceCode = sourceCode).getOrThrow()

        /**
         * Step 0: Get SourceCodeZip
         * Step 1: Extract ZipFile using FileManager inside project.workingDir
         * Step 2: Remove modules related to tooling i.e. generator, docs, site,
         *         zensical.toml, mkdocs.yml etc.
         * Step 3: If project.includeWorkflows=false then delete .github folder else
         *         .github/workflows/publish-maven* workflows etc.
         * Step 4: If Library mode then add dependencies to composeApp/build.gradle.kts & delete modules
         *         else keep selected modules @see com.kmpstarter.generator_domain.StarterModules
         * Step 5: rename your-feature inside:
         *         - settings-gradle.kts
         *         - feature dir
         *         - KOIN modules
         *         - imports
         * Step 6: Rename project name inside `settings.gradle.kts` (get file using fileManager and edit it)
         * Step 7: Rename packageName allOver the project including:
         *  - modules Gradle
         *  - modules sourceCode
         * Step 8: create starter.json to keep track of starter-template version, packageName etc.
         * */
        TODO("Not yet implemented")
    }


    private fun createStarterJson(project: StarterProject, sourceCode: SourceCode): Result<Unit> =
        runCatching {
            TODO("Not yet implemented")
        }

    private fun configureYourFeature(project: StarterProject): Result<Unit> = runCatching {
        TODO("Not yet implemented")
    }

    private fun configurePackageName(project: StarterProject): Result<Unit> = runCatching {
        TODO("Not yet implemented")
    }

    private fun configureProjectName(project: StarterProject): Result<Unit> = runCatching {
        TODO("Not yet implemented")
    }

    private suspend fun configureProjectModules(
        project: StarterProject,
        sourceCode: SourceCode,
    ): Result<Unit> = runCatching {

    }

    private suspend fun configureGitHubWorkflows(project: StarterProject): Result<Unit> =
        runCatching {
            val workflowsPath = project.workingDir + "/.github/workflows"
            if (!project.includeWorkflows) {
                fileManager.delete(
                    path = workflowsPath
                ).getOrThrow()
                return@runCatching
            }

            val files = fileManager.getFiles(path = workflowsPath)

            files.filter { it.startsWith("publish-") }.forEach { file ->
                fileManager.delete(
                    path = file
                ).getOrThrow()
            }


        }


    private suspend fun removeToolingSourceCode(project: StarterProject): Result<Unit> =
        runCatching {
            // removing folders
            TOOLING_SOURCE_CODE_FOLDERS.forEach { folder ->
                fileManager.delete(
                    path = project.workingDir + folder
                ).getOrThrow()
            }
            // removing files
            TOOLING_SOURCE_CODE_FILES.forEach { file ->
                fileManager.delete(
                    path = project.workingDir + file
                ).getOrThrow()
            }

            // remove modules from settings.gradle.kts
            val path = project.workingDir + "/settings.gradle.kts"
            val settingsGradleKtsContent = fileManager.getFileAs(path = path).getOrThrow()
            val newSettingsGradleKtsContent =
                TOOLING_SETTINGS_GRADLE_MODULES.fold(settingsGradleKtsContent) { _, item ->
                    val textToReplace = "include(\"$item\")"
                    settingsGradleKtsContent.replace(textToReplace, "")
                }
            fileManager.writeFile(
                path = path,
                content = newSettingsGradleKtsContent.encodeToByteArray()
            )
        }


    override suspend fun includeModule(
        module: StarterModules,
        mode: ProjectMode,
        packageName: String?,
        targetModule: String,
    ): Result<Unit> {

        /** if mode==ProjectMode.Module
         * Step 0: Get SourceCodeZip from .starter cache if exist else SourceCodeProvider
         * Step 1: Get the selected module & it's dependencies from SourceCodeZip
         * Step 2: Update PackageName
         * Step 3: Add dependency to targetModule/build.gradle.kts
         * */

        /** if mode==ProjectMode.Lib
         * Step 0: Get version from starter.json if exist else latest version from SourceCodeProvider
         * Step 1: Add the dependency inside gradle/libs.versions.toml
         * Step 3: Add dependency to targetModule/build.gradle.kts
         * */
        TODO("Not yet implemented")
    }

    override suspend fun excludeModule(
        module: StarterModules,
        mode: ProjectMode,
        packageName: String?,
        targetModule: String,
    ): Result<Unit> {
        // IDK maybe remove this one for now
        TODO("Not yet implemented")
    }
}