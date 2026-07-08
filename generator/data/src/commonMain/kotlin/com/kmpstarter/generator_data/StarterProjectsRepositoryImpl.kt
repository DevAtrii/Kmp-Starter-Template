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
import com.kmpstarter.generator_domain.StarterJson
import com.kmpstarter.generator_domain.StarterModules
import com.kmpstarter.generator_domain.StarterProject
import com.kmpstarter.generator_domain.StarterProjectsRepository
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.DEFAULT_PACKAGE_NAME
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.LIB_MODE_DELETABLE_MODULES
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.LOCAL_MODULES
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.STARTER_FOLDER
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.STARTER_JSON_FILE
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.TOOLING_SETTINGS_GRADLE_MODULES
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.TOOLING_SOURCE_CODE_FILES
import com.kmpstarter.generator_domain.StarterProjectsRepository.Companion.TOOLING_SOURCE_CODE_FOLDERS
import kotlinx.serialization.json.Json

class StarterProjectsRepositoryImpl(
    private val fileManager: StarterProjectFileManager,
    private val sourceCodeProvider: StarterProjectSourceCodeProvider,
) : StarterProjectsRepository {
    /** This directory will be used temporary for creating project
     * for example workingDir could be `/Users/ahmed/Coding/note-app/.starter/generation`
     * this should be used inside `generate` function**/
    private val currentWorkingDir = fileManager.getCurrentDir() + "/.starter/generation"

    override suspend fun generate(project: StarterProject): Result<ProjectZip> = runCatching {
        // PreGeneration delete workingDir
        fileManager.delete(path = currentWorkingDir).getOrThrow()


        // Step 0: Getting SourceCode
        val sourceCode = sourceCodeProvider.getSourceCode().getOrThrow()
        val sourceCodeZipBytes = sourceCode.content

        // Step 1: Extracting ZipFile
        val zipPath =
            "${currentWorkingDir}/$STARTER_FOLDER/source_code/${sourceCode.version}/code.zip"
        fileManager.writeFile(
            path = zipPath,
            content = sourceCodeZipBytes
        ).getOrThrow()
        fileManager.extractZip(path = zipPath, output = currentWorkingDir).getOrThrow()

        // Step 2
        removeToolingSourceCode(project = project).getOrThrow()

        // Step 3:
        configureGitHubWorkflows(project = project).getOrThrow()

        // Step 4
        configureProjectModules(project = project, sourceCode = sourceCode).getOrThrow()
        configureKoinModules(project = project).getOrThrow()

        // Step 5
        configureYourFeature(project = project).getOrThrow()
        // Step 6
        configureProjectName(project = project).getOrThrow()
        // Step 7
        configurePackageName(project = project).getOrThrow()
        // Step 8
        createStarterJson(project = project, sourceCode = sourceCode).getOrThrow()

        val result = fileManager.createZip(
            path = currentWorkingDir
        ).getOrThrow()

        fileManager.delete(path = currentWorkingDir).getOrThrow()
        return@runCatching result


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
    }.onFailure { _ ->
        //fileManager.delete(path = currentWorkingDir).getOrThrow()
    }


    private suspend fun createStarterJson(
        project: StarterProject,
        sourceCode: SourceCode,
    ): Result<Unit> =
        runCatching {
            val content = StarterJson(
                packageName = project.packageName,
                starterVersion = sourceCode.version,
                mode = project.mode
            )
            val jsonStr = Json.encodeToString(content)
            fileManager.writeFile(
                path = "$currentWorkingDir/$STARTER_JSON_FILE",
                content = jsonStr.encodeToByteArray()
            ).getOrThrow()

        }

    /**remove unused KOIN modules inisde initKoin.kt**/
    private suspend fun configureKoinModules(project: StarterProject): Result<Unit> = runCatching {
        return@runCatching
//        val initKoinPath =
//            "$currentWorkingDir/composeApp/src/commonMain/kotlin/com/kmpstarterapp/core/di/InitKoin.kt"
//        val content = fileManager.getFileAs(initKoinPath).getOrThrow()
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
            removeModuleFromSettingsGradleKts(
                module = module
            )
        }

        listOf(
            ":features:${project.featureName}:presentation",
            ":features:${project.featureName}:domain",
            ":features:${project.featureName}:data",
        ).forEach { module ->
            addModuleInsideSettingsGradleKts(
                module = module
            )
        }

        // replacing gradle deps
        listOf(
            "${currentWorkingDir}/features/your-feature/data/build.gradle.kts",
            "${currentWorkingDir}/features/your-feature/presentation/build.gradle.kts",
        ).forEach { filePath ->
            val fileContent = fileManager.getFileAs(filePath).getOrThrow()
            val replacedContent = fileContent.replace(
                ".yourFeature",
                ".${project.featureName}"
            )
            fileManager.writeFile(
                path = filePath,
                content = replacedContent.encodeToByteArray()
            ).getOrThrow()
        }


        // rename KOIN modules & file
        listOf(
            "${currentWorkingDir}/features/your-feature/data/src/commonMain/kotlin/com/kmpstarter/feature_your_feature_data/di/FeatureYourDataModule.kt",
            "${currentWorkingDir}/features/your-feature/domain/src/commonMain/kotlin/com/kmpstarter/feature_your_feature_domain/di/FeatureYourDomainModule.kt",
            "${currentWorkingDir}/features/your-feature/presentation/src/commonMain/kotlin/com/kmpstarter/feature_your_feature_presentation/di/FeatureYourPresentationModule.kt",
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
            val toFileName = filePath.split("/").last().replace(
                "FeatureYour",
                "Feature${project.getFeatureNameAsPascalCasing()}"
            )
            fileManager.rename(
                path = filePath,
                to = toFileName
            ).getOrThrow()


        }

        // replacing package name inside sourceFiles
        listOf(
            "$currentWorkingDir/features/your-feature",
            "$currentWorkingDir/composeApp/src/commonMain/kotlin/com/kmpstarterapp/core/di"
        ).forEach { path ->
            fileManager.getFilesRecursively(
                path = path
            ).filter {
                it.endsWith(".kt") || it.endsWith(".kts")
            }.forEach { filePath ->
                val content = fileManager.getFileAs(path = filePath).getOrThrow()
                val updated = content
                    .replace(".feature_your_feature", ".feature_${project.featureName}")
                    .let {
                        if (filePath.endsWith(".kts")) {
                            it.replace(
                                ":featureYour",
                                ":feature${project.getFeatureNameAsPascalCasing()}"
                            )
                        } else {
                            it
                        }
                    }
                fileManager.writeFile(
                    path = filePath,
                    content = updated.encodeToByteArray()
                ).getOrThrow()
            }
        }


        // replace packageName inside sourceSets
        fileManager.getDirectoriesRecursively(
            path = "$currentWorkingDir/features/your-feature"
        ).filter {
            it.split("/").last().startsWith("feature_your_feature")
        }.forEach { path ->
            val newPath = path.replace("/feature_your_feature", "/feature_${project.featureName}")
            fileManager.rename(
                path = path,
                to = newPath
            )
        }
        // fix KOIN modules import inside composeApp/di
        val initKtFilePath =
            "${currentWorkingDir}/composeApp/src/commonMain/kotlin/com/kmpstarterapp/core/di/InitKoin.kt"
        val initKtContent = fileManager.getFileAs(path = initKtFilePath).getOrThrow()
        val updated = initKtContent.replace(
            "featureYour",
            "feature${project.getFeatureNameAsPascalCasing()}",
        )

        fileManager.writeFile(
            path = initKtFilePath,
            content = updated.encodeToByteArray()
        )


        // renaming module name
        listOf(
            "${currentWorkingDir}/features/your-feature"
        ).forEach { dir ->
            fileManager.rename(
                path = dir,
                to = project.featureName!!.replace("-", "_").lowercase()
            ).getOrThrow()
        }

        // update composeApp/build.gradle.kts

        val composeAppGradlePath = "$currentWorkingDir/composeApp/build.gradle.kts"
        val composeAppGradleContent = fileManager.getFileAs(composeAppGradlePath).getOrThrow()
        val yourFeatureDeps = buildString {
            appendLine("implementation(projects.features.${project.featureName}.data)")
            appendLine("implementation(projects.features.${project.featureName}.domain)")
            appendLine("implementation(projects.features.${project.featureName}.presentation)")
        }
        val updatedComposeAppGradleContent = buildString {
            val parts = composeAppGradleContent.split("// External Libraries")
            append(parts[0])
            append(yourFeatureDeps)
            append(parts[1])
        }
        fileManager.writeFile(
            path = composeAppGradlePath,
            content = updatedComposeAppGradleContent.encodeToByteArray()
        ).getOrThrow()
    }

    private suspend fun configurePackageName(project: StarterProject): Result<Unit> = runCatching {
        /** replace package name across project including:
         *      - builds.gradle.kts files
         *      - source files
         *      - sourceSet folders like composeApp/src/commonMain/kotlin/com/kmpstarterapp/ -> com/your/packageName etc
         *          - keep in mind the depth of source folders
         * */
        if (project.packageName == DEFAULT_PACKAGE_NAME)
            return@runCatching

        // rename from source-code files
        val allFiles = fileManager
            .getFilesRecursively(path = currentWorkingDir)
            .filter {
                val isTooling = it.startsWith("$currentWorkingDir/$STARTER_FOLDER") ||
                        it.startsWith("$currentWorkingDir/build-logic") ||
                        it.startsWith("$currentWorkingDir/build.gradle.kts") || it.startsWith(
                    "$currentWorkingDir/iosApp"
                )
                val isSource = it.endsWith("kt") || it.endsWith("kts")
                isSource && !isTooling
            }


        val nonLocalModulesPackageName = StarterModules.all().filter {
            it.moduleGradlePath() !in LOCAL_MODULES
        }.map {
            it.packageName
        }

        allFiles.forEach { codeFile ->
            val fileContent = fileManager.getFileAs(path = codeFile).getOrThrow()
            val updated = fileContent.split("\n").joinToString("\n") { line ->

                when {
                    line.startsWith("package ") -> {
                        val fromPackage = if (line.contains("${DEFAULT_PACKAGE_NAME}app")) {
                            "${DEFAULT_PACKAGE_NAME}app"
                        } else {
                            DEFAULT_PACKAGE_NAME
                        }

                        line.replace(fromPackage, project.packageName)
                    }

                    line.startsWith("import ") -> {
                        nonLocalModulesPackageName.find {
                            line.startsWith("import $it")
                        } ?: return@joinToString line.let {
                            val fromPackage = if (line.contains("${DEFAULT_PACKAGE_NAME}app")) {
                                "${DEFAULT_PACKAGE_NAME}app"
                            } else {
                                DEFAULT_PACKAGE_NAME
                            }

                            line.replace(fromPackage, project.packageName)
                        }
                        line
                    }

                    else -> line
                }

            }
            fileManager.writeFile(
                path = codeFile,
                content = updated.encodeToByteArray()
            ).getOrThrow()
        }

        // rename source-sets folders
        val oldPath = DEFAULT_PACKAGE_NAME.replace('.', '/')
        val newPath = project.packageName.replace('.', '/')


        fileManager.getDirectoriesRecursively(currentWorkingDir)
            .filter { dir ->
                dir.endsWith(oldPath) || dir.endsWith(oldPath + "app") // kmpstarterapp
            }
            .forEach { packageDir ->
                val suffix = if (packageDir.endsWith("app")) oldPath+"app" else oldPath
                val sourceRoot = packageDir.removeSuffix(suffix)
                val newPackageDir = sourceRoot + newPath
                if (suffix == "app" && !packageDir.contains("__MACOSX"))
                    println("suffix=$suffix,newPath=$newPath, newPackageDir=$newPackageDir,\nsource=$sourceRoot")
                fileManager.mkDirs(newPackageDir).getOrThrow()

                fileManager.moveFiles(
                    path = packageDir,
                    to = newPackageDir
                ).getOrThrow()

                fileManager.delete(packageDir).getOrThrow()
            }
    }

    private suspend fun configureProjectName(project: StarterProject): Result<Unit> = runCatching {
        val path = "${currentWorkingDir}/settings.gradle.kts"
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
        if (project.mode == ProjectMode.LIB) {
            // delete extra modules dirs
            val moduleDirs = LIB_MODE_DELETABLE_MODULES.map {
                it.replaceFirst(":", "")
                    .replace(":", "/")
            }

            moduleDirs.forEach { dir ->
                val path = "$currentWorkingDir/$dir"
                fileManager.delete(path = path).getOrThrow()
            }

            // removing modules from settings.gradle.kts
            LIB_MODE_DELETABLE_MODULES.forEach { module ->
                removeModuleFromSettingsGradleKts(module = module).getOrThrow()
            }

            // keep or remove non-lib modules i.e database
            val selectedModules = project.modules
                .map { it.moduleGradlePath() }
                .toSet()

            StarterModules.all().forEach { module ->
                val gradlePath = module.moduleGradlePath()

                // Keep every selected module.
                if (gradlePath in selectedModules) {
                    return@forEach
                }

                // Only local modules need to be physically removed.
                if (gradlePath !in LOCAL_MODULES) {
                    return@forEach
                }


                fileManager.delete("$currentWorkingDir/${module.moduleFilePath()}").getOrThrow()
                removeModuleFromSettingsGradleKts(gradlePath).getOrThrow()
            }
            // add selected modules inside composeApp/build.gradle.kts as libs
            val libsDeps = buildString {
                appendLine("/** Added By KMP Starter Template **/")
                project.modules.filter {
                    it.moduleGradlePath() !in LOCAL_MODULES
                }.forEach { module ->
                    appendLine("implementation(${module.moduleGradleDep(mode = project.mode)})")
                }
            }
            // local selected modules as module
            val localDeps = buildString {
                appendLine("// Local Modules")
                project.modules.filter { it.moduleGradlePath() in LOCAL_MODULES }
                    .forEach { module ->
                        appendLine("implementation(${module.moduleGradleDep(mode = ProjectMode.MODULE)})")
                    }
            }
            val deps = libsDeps + localDeps
            val path = "$currentWorkingDir/composeApp/build.gradle.kts"
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

            // updating other modules build gradle to add modules as library
            val ignoredBuildGradleFiles = setOf(
                "composeApp/build.gradle.kts",
                "build-logic/build.gradle.kts",
                "build-logic/plugins/build.gradle.kts",
                "build.gradle.kts",
            )

            val localModuleDeps = project.modules
                .filter { it.moduleGradlePath() in LOCAL_MODULES }
                .map { it.moduleGradleDep(ProjectMode.MODULE) }
                .toSet()

            fileManager.getFilesRecursively(currentWorkingDir)
                .filter { path ->
                    path.endsWith("build.gradle.kts") &&
                            path.removePrefix("$currentWorkingDir/")
                                .removePrefix(currentWorkingDir) !in ignoredBuildGradleFiles
                }
                .forEach { path ->
                    val content = fileManager.getFileAs(path).getOrThrow()

                    var updated = content

                    StarterModules.all().forEach { module ->
                        val projectDep = module.moduleGradleDep(ProjectMode.MODULE)

                        // Keep local modules as project dependencies.
                        if (projectDep in localModuleDeps) {
                            return@forEach
                        }

                        updated = updated.replace(
                            projectDep,
                            module.moduleGradleDep(ProjectMode.LIB)
                        )
                    }

                    if (updated != content) {
                        fileManager.writeFile(
                            path = path,
                            content = updated.encodeToByteArray()
                        ).getOrThrow()
                    }
                }

            fileManager.writeFile(
                path = path,
                content = updated.encodeToByteArray()
            ).getOrThrow()

            // update library version inside libs.versions.toml
            val libsFilePath = "$currentWorkingDir/gradle/libs.versions.toml"
            val updatedLibs = fileManager.getFileAs(libsFilePath).getOrThrow().replace(
                Regex("""(starter\s*=\s*")([^"]+)(")"""),
                """$1${sourceCode.version}$3"""
            )
            fileManager.writeFile(
                path = libsFilePath,
                content = updatedLibs.encodeToByteArray()
            )


            return@runCatching
        }
        throw NotImplementedError()
    }

    private suspend fun configureGitHubWorkflows(project: StarterProject): Result<Unit> =
        runCatching {
            val workflowsPath = "$currentWorkingDir/.github/workflows"
            if (!project.includeWorkflows) {
                fileManager.delete(
                    path = workflowsPath
                ).getOrThrow()
                fileManager.delete(
                    path = "$currentWorkingDir/publish"
                ).getOrThrow()
                return@runCatching
            }

            val files = fileManager.getFiles(path = workflowsPath)

            files.filter { it.startsWith("$workflowsPath/publish-") }.forEach { file ->
                fileManager.delete(
                    path = file
                ).getOrThrow()
            }


        }


    private suspend fun removeToolingSourceCode(project: StarterProject): Result<Unit> =
        runCatching {
            // Remove folders
            TOOLING_SOURCE_CODE_FOLDERS.forEach { folder ->
                fileManager.delete("$currentWorkingDir/$folder").getOrThrow()
            }

            // Remove files
            TOOLING_SOURCE_CODE_FILES.forEach { file ->
                fileManager.delete("$currentWorkingDir/$file").getOrThrow()
            }

            // Remove modules from settings.gradle.kts
            TOOLING_SETTINGS_GRADLE_MODULES.forEach { module ->
                removeModuleFromSettingsGradleKts(module).getOrThrow()
            }

            // Remove starter-libs Maven repository
            val settingsGradle = "$currentWorkingDir/settings.gradle.kts"
            val content = fileManager.getFileAs(settingsGradle)
                .getOrThrow()

            val updated = content.replace(
                Regex(
                    """(?s)\s*val\s+starterLibsDir\s*=\s*rootDir\.resolve\("\.starter-libs"\)\s*
if\s*\(\s*starterLibsDir\.exists\(\)\s*\)\s*\{
\s*maven\(starterLibsDir\.toURI\(\)\)\s*\{
\s*name\s*=\s*"starterLibsLocal"\s*
content\s*\{
\s*includeGroup\("io\.github\.devatrii"\)\s*
\}
\s*\}
\s*\}
"""
                ),
                ""
            )

            // todo remove build-logic KmpLibraryPublishPlugin

            fileManager.writeFile(
                path = settingsGradle,
                content = updated.encodeToByteArray()
            ).getOrThrow()
        }

    /** enter module like `:starter:core`*/
    private suspend fun removeModuleFromSettingsGradleKts(module: String): Result<Unit> {
        val path = "$currentWorkingDir/settings.gradle.kts"
        val settingsGradleKtsContent = fileManager.getFileAs(path = path).getOrThrow()
        val textToReplace = "include(\"$module\")"
        val newSettingsGradleKtsContent = settingsGradleKtsContent.replace(textToReplace, "")
        return fileManager.writeFile(
            path = path,
            content = newSettingsGradleKtsContent.encodeToByteArray()
        )
    }

    /** enter module like `:starter:core`*/
    private suspend fun addModuleInsideSettingsGradleKts(module: String): Result<Unit> {
        val path = "$currentWorkingDir/settings.gradle.kts"
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