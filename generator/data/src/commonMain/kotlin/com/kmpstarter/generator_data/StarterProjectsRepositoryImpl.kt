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
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.LIB_MODE_DELETABLE_MODULES
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.STARTER_FOLDER
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.TOOLING_SETTINGS_GRADLE_MODULES
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.TOOLING_SOURCE_CODE_FILES
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.TOOLING_SOURCE_CODE_FOLDERS

class StarterProjectsRepositoryImpl(
    private val fileManager: StarterProjectFileManager,
    private val sourceCodeProvider: StarterProjectSourceCodeProvider,
) : StarterProjectsRepository {
    /** This directory will be used temporary for creating project
     * for example workingDir could be `/Users/ahmed/Coding/note-app/.starter/generation`
     * this should be used inside `generate` function**/
    private val workingDir = fileManager.getCurrentDir() + ".starter/generation"

    override suspend fun generate(project: StarterProject): Result<ProjectZip> {
        // Step 0: Getting SourceCode
        val sourceCode = sourceCodeProvider.getSourceCode().getOrThrow()
        val sourceCodeZipBytes = sourceCode.content

        // Step 1: Extracting ZipFile
        val zipPath =
            "${workingDir}/$STARTER_FOLDER/source_code/${sourceCode.version}/code.zip"
        fileManager.writeFile(
            path = zipPath,
            content = sourceCodeZipBytes
        ).getOrThrow()
        fileManager.extractZip(path = zipPath, output = workingDir).getOrThrow()

        // Step 2
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

        // todo zip everything inside workingDir & return as byte array

        /**
         * Step 0: Get SourceCodeZip
         * Step 1: Extract ZipFile using FileManager inside workingDir
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
         * Step 9: zip everything inside `workingDir`, return as byteArr & delete everything inside `workingDir`
         * */
        TODO("Not yet implemented")
    }


    private suspend fun createStarterJson(
        project: StarterProject,
        sourceCode: SourceCode,
    ): Result<Unit> =
        runCatching {
            TODO("Not yet implemented")
        }

    /**
     *  Step 5: rename your-feature inside:
     *   - settings-gradle.kts
     *   - feature dir
     *   - KOIN modules
     *   - imports
     *   */
    private suspend fun configureYourFeature(project: StarterProject): Result<Unit> = runCatching {
        if (project.featureName == null)
            return@runCatching


        listOf(
            ":features:your-feature:presentation",
            ":features:your-feature:domain",
            ":features:your-feature:data",
        ).forEach { module ->
            project.removeModuleFromSettingsGradleKts(
                module = module
            )
        }

        listOf(
            ":features:${project.featureName}:presentation",
            ":features:${project.featureName}:domain",
            ":features:${project.featureName}:data",
        ).forEach { module ->
            project.addModuleInsideSettingsGradleKts(
                module = module
            )
        }
        listOf(
            "${workingDir}/features/your-feature"
        ).forEach { dir ->
            fileManager.rename(
                path = dir,
                to = project.featureName!!.replace("-", "_").lowercase()
            ).getOrThrow()
        }

        // rename KOIN modules & file
        listOf(
            "${workingDir}/features/your-feature/data/src/commonMain/kotlin/com/kmpstarter/feature_your_feature_data/di/FeatureYourDataModule.kt",
            "${workingDir}/features/your-feature/domain/src/commonMain/kotlin/com/kmpstarter/feature_your_feature_domain/di/FeatureYourDomainModule.kt",
            "${workingDir}/features/your-feature/presentation/src/commonMain/kotlin/com/kmpstarter/feature_your_feature_presentation/di/FeatureYourPresentationModule.kt",
        ).forEach { filePath ->
            // rename module name inside
            val fileContent = fileManager.getFileAs(filePath).getOrThrow()
            // language="kotlin"
            val replacedContent = fileContent.replace(
                "val featureYour",
                "val feature${project.getFeatureNameAsPascalCasing()}"
            )
            fileManager.writeFile(
                path = filePath,
                content = replacedContent.encodeToByteArray()
            ).getOrThrow()

            // rename file names
            fileManager.rename(
                path = filePath,
                to = "Feature${project.getFeatureNameAsPascalCasing()}Module.kt"
            ).getOrThrow()

        }

        // fix KOIN modules import inside composeApp/di
        val initKtFilePath =
            "${workingDir}/composeApp/src/commonMain/kotlin/com/kmpstarter/core/di/InitKoin.kt"
        val initKtContent = fileManager.getFileAs(path = initKtFilePath).getOrThrow()
        val updated = initKtContent.replace(
            "featureYour",
            "feature${project.getFeatureNameAsPascalCasing()}",
        )

        fileManager.writeFile(
            path = initKtFilePath,
            content = updated.encodeToByteArray()
        )
    }

    private suspend fun configurePackageName(project: StarterProject): Result<Unit> = runCatching {
        /** replace package name across project including:
         *      - builds.gradle.kts
         *      - source files
         *      - source folders like composeApp/src/commonMain/kotlin/com/kmpstarter/ -> com/your/packageName etc
         *          - keep in mind the depth of source folders
         * */

        TODO("Not yet implemented")
    }

    private suspend fun configureProjectName(project: StarterProject): Result<Unit> = runCatching {
        val path = "${workingDir}/settings.gradle.kts"
        val settingsGradleKtsContent = fileManager.getFileAs(path = path).getOrThrow()
        val newSettingsGradleKtsContent = settingsGradleKtsContent
            .replace(
                "rootProject.name = \"KmpStarter\"",
                "rootProject.name = \"${project.projectName}\""
            )
        fileManager.writeFile(
            path = path,
            content = newSettingsGradleKtsContent.encodeToByteArray()
        )
    }

    /**Step 4: If Library mode then add dependencies to composeApp/build.gradle.kts & delete modules
     *         else keep selected modules @see com.kmpstarter.generator_domain.StarterModules*/
    private suspend fun configureProjectModules(
        project: StarterProject,
        sourceCode: SourceCode,
    ): Result<Unit> = runCatching {
        // replace starter version inside libs.versions.toml
        if (project.mode == ProjectMode.LIB) {
            // delete extra modules dirs
            val moduleDirs = LIB_MODE_DELETABLE_MODULES.map {
                it.replaceFirst(":", "")
                    .replace(":", "/")
            }

            moduleDirs.forEach { dir ->
                val path = workingDir + "/$dir"
                fileManager.delete(path = path).getOrThrow()
            }

            // removing modules from settings.gradle.kts
            LIB_MODE_DELETABLE_MODULES.forEach { module ->
                project.removeModuleFromSettingsGradleKts(module = module).getOrThrow()
            }

            // add selected modules inside composeApp/build.gradle.kts
            val deps = buildString {
                appendLine("// Added By KMP Starter Template")
                project.modules.forEach { module ->
                    appendLine("implementation(${module.getGradleDep()})")
                }
            }
            val path = workingDir + "/composeApp/build.gradle.kts"
            val buildGradleContent: String = fileManager.getFileAs(path = path).getOrThrow()

            val updated = buildGradleContent.replace(
                Regex(
                    """(?s)(commonMain\.dependencies\s*\{\s*)(.*?)(\s*// External Libraries)"""
                )
            ) { match ->
                buildString {
                    append(match.groupValues[1])      // commonMain.dependencies {
                    append(deps)
                    append(match.groupValues[3])      // // External Libraries
                }
            }

            fileManager.writeFile(
                path = path,
                content = updated.encodeToByteArray()
            ).getOrThrow()


            return@runCatching
        }
    }

    private suspend fun configureGitHubWorkflows(project: StarterProject): Result<Unit> =
        runCatching {
            val workflowsPath = workingDir + "/.github/workflows"
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
                    path = workingDir + folder
                ).getOrThrow()
            }
            // removing files
            TOOLING_SOURCE_CODE_FILES.forEach { file ->
                fileManager.delete(
                    path = workingDir + file
                ).getOrThrow()
            }

            // remove modules from settings.gradle.kts
            TOOLING_SETTINGS_GRADLE_MODULES.forEach { module ->
                project.removeModuleFromSettingsGradleKts(module = module).getOrThrow()
            }

        }

    /** enter module like `:starter:core`*/
    private suspend fun StarterProject.removeModuleFromSettingsGradleKts(module: String): Result<Unit> {
        val path = "$workingDir/settings.gradle.kts"
        val settingsGradleKtsContent = fileManager.getFileAs(path = path).getOrThrow()
        val textToReplace = "include(\"$module\")"
        val newSettingsGradleKtsContent = settingsGradleKtsContent.replace(textToReplace, "")
        return fileManager.writeFile(
            path = path,
            content = newSettingsGradleKtsContent.encodeToByteArray()
        )
    }

    /** enter module like `:starter:core`*/
    private suspend fun StarterProject.addModuleInsideSettingsGradleKts(module: String): Result<Unit> {
        val path = "$workingDir/settings.gradle.kts"
        val settingsGradleKtsContent = fileManager.getFileAs(path = path).getOrThrow()
        val textToAdd = "include(\"$module\")"
        val newSettingsGradleKtsContent = settingsGradleKtsContent + "\n$textToAdd"
        return fileManager.writeFile(
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