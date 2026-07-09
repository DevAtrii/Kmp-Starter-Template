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
        configureGradlePlugins(project = project).getOrThrow()

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

    /**remove plugins when not needed like google-services etc**/
    private suspend fun configureGradlePlugins(project: StarterProject): Result<Unit> =
        runCatching {

            // removing google-services plugin
            configureRemoteConfigForPlugins(project = project).getOrThrow()


            // later add more plugins logic
        }

    private suspend fun configureRemoteConfigForPlugins(project: StarterProject): Result<Unit> =
        runCatching {
            if (StarterModules.Features.RemoteConfig.Data in project.modules)
                return@runCatching

            val content = fileManager.getFileAs(
                path = "$currentWorkingDir/androidApp/build.gradle.kts"
            ).getOrThrow()
            val updated = content.replace(
                "alias(libs.plugins.google.services)",
                ""
            )
            fileManager.writeFile(
                path = "$currentWorkingDir/androidApp/build.gradle.kts",
                content = updated.encodeToByteArray()
            ).getOrThrow()

            if (StarterModules.Features.RemoteConfig.Domain in project.modules)
                return@runCatching

            // remote config doesn't exist even remove initialization code
            val initKmpPath =
                "$currentWorkingDir/composeApp/src/commonMain/kotlin/com/kmpstarterapp/core/InitKmpApp.kt"
            val initKmpContent = fileManager.getFileAs(path = initKmpPath).getOrThrow()
            val updatedInitKmpContent = initKmpContent
                .replace(
                    Regex(
                        """private\s+fun\s+initRemoteConfig\(\)\s*\{\s*CoroutineScope\(Dispatchers\.IO\)\.launch\s*\{[\s\S]*?^\s*}\s*^\s*}""",
                        setOf(RegexOption.MULTILINE)
                    ),
                    ""
                )
                .replace(
                    Regex("""^\s*initRemoteConfig\(\)\s*$""", setOf(RegexOption.MULTILINE)),
                    ""
                )
            fileManager.writeFile(
                path = initKmpPath,
                content = updatedInitKmpContent.encodeToByteArray()
            ).getOrThrow()
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

    private fun replacePackageInLine(
        line: String,
        targetPackageName: String,
        preserveStarterImports: Boolean,
    ): String {
        if (preserveStarterImports && line.startsWith("import ")) {
            val isStarterImport = StarterModules.all()
                .filter { it.moduleGradlePath() !in LOCAL_MODULES }
                .any { line.startsWith("import ${it.packageName}") }
            if (isStarterImport) return line
        }

        val fromPackage = if (line.contains("${DEFAULT_PACKAGE_NAME}app")) {
            "${DEFAULT_PACKAGE_NAME}app"
        } else {
            DEFAULT_PACKAGE_NAME
        }
        return line.replace(fromPackage, targetPackageName)
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

        val preserveStarterImports = project.mode == ProjectMode.LIB

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

        allFiles.forEach { codeFile ->
            val fileContent = fileManager.getFileAs(path = codeFile).getOrThrow()
            val updated = fileContent.split("\n").joinToString("\n") { line ->
                replacePackageInLine(
                    line = line,
                    targetPackageName = project.packageName,
                    preserveStarterImports = preserveStarterImports,
                )
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
                val suffix = if (packageDir.endsWith("app")) oldPath + "app" else oldPath
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
            configureProjectModulesForLibMode(project = project, sourceCode = sourceCode).getOrThrow()
            return@runCatching
        }

        configureProjectModulesForModuleMode(project = project).getOrThrow()
    }

    private suspend fun configureProjectModulesForLibMode(
        project: StarterProject,
        sourceCode: SourceCode,
    ): Result<Unit> = runCatching {
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
    }

    private suspend fun configureProjectModulesForModuleMode(
        project: StarterProject,
    ): Result<Unit> = runCatching {
        val selectedModules = expandSelectedModules(project.modules)
        val selectedDeps = selectedModules
            .map { it.moduleGradleDep(ProjectMode.MODULE) }
            .toSet()

        StarterModules.all().forEach { module ->
            if (module in selectedModules) return@forEach

            fileManager.delete("$currentWorkingDir/${module.moduleFilePath()}").getOrThrow()
            removeModuleFromSettingsGradleKts(module = module.moduleGradlePath()).getOrThrow()
        }

        val composeAppGradlePath = "$currentWorkingDir/composeApp/build.gradle.kts"
        val composeAppGradleContent = fileManager.getFileAs(composeAppGradlePath).getOrThrow()
        val updatedComposeApp = composeAppGradleContent.replace(
            Regex("""(?s)(commonMain\.dependencies\s*\{\s*)(.*?)(\s*// External Libraries)""")
        ) { match ->
            buildString {
                append(match.groupValues[1])
                append(filterProjectDependencies(match.groupValues[2], selectedDeps))
                append(match.groupValues[3])
            }
        }
        fileManager.writeFile(
            path = composeAppGradlePath,
            content = updatedComposeApp.encodeToByteArray()
        ).getOrThrow()

        configureKoinModulesForModuleMode(selectedModules).getOrThrow()

        val libsFilePath = "$currentWorkingDir/gradle/libs.versions.toml"
        val updatedLibs = removeStarterLibrariesFromToml(
            fileManager.getFileAs(libsFilePath).getOrThrow()
        )
        fileManager.writeFile(
            path = libsFilePath,
            content = updatedLibs.encodeToByteArray()
        ).getOrThrow()
    }

    private fun expandSelectedModules(modules: List<StarterModules>): Set<StarterModules> {
        val expanded = linkedSetOf<StarterModules>()
        modules.forEach { module ->
            collectModulesWithDependencies(module).forEach(expanded::add)
        }
        return expanded
    }

    private fun filterProjectDependencies(
        dependenciesBlock: String,
        selectedDeps: Set<String>,
    ): String =
        dependenciesBlock.lineSequence()
            .filter { line ->
                val projectRef = Regex("""projects\.[\w.]+""").find(line)?.value
                projectRef == null || projectRef in selectedDeps
            }
            .joinToString("\n")

    private fun removeStarterLibrariesFromToml(toml: String): String =
        toml
            .replace(Regex("""(?m)^starter\s*=.*\n"""), "")
            .replace(Regex("""(?s)\n# STARTER LIBRARIES.*?(?=\n\[plugins\])"""), "")

    private suspend fun configureKoinModulesForModuleMode(
        selectedModules: Set<StarterModules>,
    ): Result<Unit> = runCatching {
        val initKoinPath =
            "$currentWorkingDir/composeApp/src/commonMain/kotlin/com/kmpstarterapp/core/di/InitKoin.kt"
        var content = fileManager.getFileAs(initKoinPath).getOrThrow()

        KOIN_MODULE_ENTRIES.forEach { entry ->
            if (entry.starterModule in selectedModules) return@forEach

            content = content.lineSequence()
                .filterNot { line -> line.trim() == entry.importLine.trim() }
                .joinToString("\n")

            content = content.replace(
                Regex("""^\s*${Regex.escape(entry.moduleSymbol)},?\s*$""", RegexOption.MULTILINE),
                ""
            )
        }

        fileManager.writeFile(
            path = initKoinPath,
            content = content.encodeToByteArray()
        ).getOrThrow()
    }

    private data class KoinModuleEntry(
        val importLine: String,
        val moduleSymbol: String,
        val starterModule: StarterModules,
    )

    private companion object {
        val KOIN_MODULE_ENTRIES = listOf(
            KoinModuleEntry(
                importLine = "import com.kmpstarter.core.datastore.di.dataStoreModule",
                moduleSymbol = "dataStoreModule",
                starterModule = StarterModules.Starter.Core,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.core.events.di.eventsModule",
                moduleSymbol = "eventsModule",
                starterModule = StarterModules.Starter.Core,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.utils.di.utilsModule",
                moduleSymbol = "utilsModule",
                starterModule = StarterModules.Starter.Utils,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.feature_analytics_data.di.analyticsDataModule",
                moduleSymbol = "analyticsDataModule",
                starterModule = StarterModules.Features.Analytics.Data,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.feature_core_data.di.coreDataModule",
                moduleSymbol = "coreDataModule",
                starterModule = StarterModules.Features.Core.Data,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.feature_core_domain.di.coreDomainModule",
                moduleSymbol = "coreDomainModule",
                starterModule = StarterModules.Features.Core.Domain,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.feature_core_presentation.di.corePresentationModule",
                moduleSymbol = "corePresentationModule",
                starterModule = StarterModules.Features.Core.Presentation,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.feature_database.di.databaseModule",
                moduleSymbol = "databaseModule",
                starterModule = StarterModules.Features.Database,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.feature_notifications_core.notificationsCoreModule",
                moduleSymbol = "notificationsCoreModule",
                starterModule = StarterModules.Features.Notifications.Core,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.feature_notifications_local.notificationsLocalModule",
                moduleSymbol = "notificationsLocalModule",
                starterModule = StarterModules.Features.Notifications.Local,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.feature_notifications_push.notificationsPushModule",
                moduleSymbol = "notificationsPushModule",
                starterModule = StarterModules.Features.Notifications.Push,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.feature_purchases_data.di.purchasesDataModule",
                moduleSymbol = "purchasesDataModule",
                starterModule = StarterModules.Features.Purchases.Data,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.feature_purchases_domain.di.purchasesDomainModule",
                moduleSymbol = "purchasesDomainModule",
                starterModule = StarterModules.Features.Purchases.Domain,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.feature_purchases_presentation.di.purchasesPresentationModule",
                moduleSymbol = "purchasesPresentationModule",
                starterModule = StarterModules.Features.Purchases.Presentation,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.feature_remote_config_data.di.remoteConfigDataModule",
                moduleSymbol = "remoteConfigDataModule",
                starterModule = StarterModules.Features.RemoteConfig.Data,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarter.feature_remote_config_domain.di.remoteConfigDomainModule",
                moduleSymbol = "remoteConfigDomainModule",
                starterModule = StarterModules.Features.RemoteConfig.Domain,
            ),
            KoinModuleEntry(
                importLine = "import com.kmpstarterapp.core.navigation.appNavigationModule",
                moduleSymbol = "appNavigationModule",
                starterModule = StarterModules.Features.Navigation,
            ),
        )
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
    private suspend fun addModuleInsideSettingsGradleKts(
        module: String,
        workingDir: String = currentWorkingDir,
    ): Result<Unit> {
        val path = "$workingDir/settings.gradle.kts"
        val settingsGradleKtsContent = fileManager.getFileAs(path = path).getOrThrow()
        val textToAdd = "include(\"$module\")"
        if (textToAdd in settingsGradleKtsContent) {
            return Result.success(Unit)
        }
        val newSettingsGradleKtsContent = settingsGradleKtsContent + "\n$textToAdd"
        return fileManager.writeFile(
            path = path,
            content = newSettingsGradleKtsContent.encodeToByteArray()
        )
    }

    private fun addDependencyToCommonMain(
        content: String,
        dependency: String,
        targetModule: String,
    ): String {
        val externalLibrariesRegex = Regex(
            """(?s)(commonMain\.dependencies\s*\{\s*)(.*?)(\s*// External Libraries)"""
        )
        externalLibrariesRegex.find(content)?.let { match ->
            return content.replaceRange(match.range, buildString {
                append(match.groupValues[1])
                append(match.groupValues[2].trimEnd())
                appendLine()
                appendLine("            $dependency")
                appendLine()
                append(match.groupValues[3])
            })
        }

        val dotNotationRegex = Regex(
            """(?s)(commonMain\.dependencies\s*\{\s*)([\s\S]*?)(\n\s*\})"""
        )
        dotNotationRegex.find(content)?.let { match ->
            return content.replaceRange(match.range, buildString {
                append(match.groupValues[1])
                append(match.groupValues[2].trimEnd())
                appendLine()
                append("            $dependency")
                append(match.groupValues[3])
            })
        }

        val nestedRegex = Regex(
            """(?s)(commonMain\s*\{\s*dependencies\s*\{\s*)([\s\S]*?)(\n\s*\})"""
        )
        nestedRegex.find(content)?.let { match ->
            return content.replaceRange(match.range, buildString {
                append(match.groupValues[1])
                append(match.groupValues[2].trimEnd())
                appendLine()
                append("                $dependency")
                append(match.groupValues[3])
            })
        }

        throw IllegalStateException(
            "Could not find commonMain.dependencies block in '$targetModule/build.gradle.kts'."
        )
    }

    private fun collectModulesWithDependencies(module: StarterModules): List<StarterModules> {
        val ordered = linkedSetOf<StarterModules>()
        fun visit(current: StarterModules) {
            current.dependencies().forEach(::visit)
            ordered.add(current)
        }
        visit(module)
        return ordered.toList()
    }

    private suspend fun extractSourceCodeTo(
        workingDir: String,
        version: String,
        zipBytes: ByteArray,
    ): String {
        val zipPath = "${workingDir}/$STARTER_FOLDER/source_code/$version/code.zip"
        val sourceCodePath = "${workingDir}/$STARTER_FOLDER/source_code/$version/code"
        fileManager.writeFile(path = zipPath, content = zipBytes).getOrThrow()
        fileManager.extractZip(path = zipPath, output = sourceCodePath).getOrThrow()
        return sourceCodePath
    }

    private suspend fun copyDirectory(from: String, to: String): Result<Unit> = runCatching {
        fileManager.mkDirs(to).getOrThrow()
        fileManager.getFilesRecursively(from).forEach { filePath ->
            val relativePath = filePath.removePrefix(from).trimStart('/')
            val destination = "$to/$relativePath"
            val parent = destination.substringBeforeLast('/', missingDelimiterValue = "")
            if (parent.isNotEmpty()) {
                fileManager.mkDirs(parent).getOrThrow()
            }
            val bytes = fileManager.getFile(filePath).getOrThrow()
            fileManager.writeFile(destination, bytes).getOrThrow()
        }
    }

    private suspend fun resolveTargetPackageName(
        workingDir: String,
        packageName: String?,
    ): String {
        if (packageName != null) return packageName
        val starterJsonPath = "$workingDir/$STARTER_JSON_FILE"
        val starterJsonBytes = fileManager.getFile(starterJsonPath).getOrNull() ?: return DEFAULT_PACKAGE_NAME
        return Json.decodeFromString<StarterJson>(starterJsonBytes.decodeToString()).packageName
    }

    private suspend fun updatePackageNameInModulePaths(
        modulePaths: List<String>,
        targetPackageName: String,
    ): Result<Unit> = runCatching {
        if (targetPackageName == DEFAULT_PACKAGE_NAME) return@runCatching

        val allFiles = modulePaths.flatMap { modulePath ->
            fileManager.getFilesRecursively(modulePath).filter {
                it.endsWith(".kt") || it.endsWith(".kts")
            }
        }

        allFiles.forEach { codeFile ->
            val fileContent = fileManager.getFileAs(path = codeFile).getOrThrow()
            val updated = fileContent.split("\n").joinToString("\n") { line ->
                replacePackageInLine(
                    line = line,
                    targetPackageName = targetPackageName,
                    preserveStarterImports = true,
                )
            }
            fileManager.writeFile(
                path = codeFile,
                content = updated.encodeToByteArray()
            ).getOrThrow()
        }

        val oldPath = DEFAULT_PACKAGE_NAME.replace('.', '/')
        val newPath = targetPackageName.replace('.', '/')

        modulePaths.flatMap { modulePath ->
            fileManager.getDirectoriesRecursively(modulePath)
        }.filter { dir ->
            dir.endsWith(oldPath) || dir.endsWith(oldPath + "app")
        }.forEach { packageDir ->
            val suffix = if (packageDir.endsWith("app")) oldPath + "app" else oldPath
            val sourceRoot = packageDir.removeSuffix(suffix)
            val newPackageDir = sourceRoot + newPath
            fileManager.mkDirs(newPackageDir).getOrThrow()
            fileManager.moveFiles(path = packageDir, to = newPackageDir).getOrThrow()
            fileManager.delete(packageDir).getOrThrow()
        }
    }

    private fun extractCatalogRefs(content: String): Set<String> =
        Regex("""libs\.[A-Za-z][\w.]*""")
            .findAll(content)
            .map { match ->
                match.value
                    .removeSuffix(".get")
                    .substringBefore(".pluginId")
            }
            .filterNot { it.startsWith("libs.starter") }
            .toSet()

    private fun catalogRefToTomlKey(ref: String): Pair<String, String>? {
        val path = ref.removePrefix("libs.")
        return when {
            path.startsWith("plugins.") ->
                "plugins" to path.removePrefix("plugins.").replace('.', '-')

            path.startsWith("versions.") ->
                "versions" to path.removePrefix("versions.").replace('.', '-')

            else ->
                "libraries" to path.replace('.', '-')
        }
    }

    private fun tomlHasEntry(toml: String, section: String, key: String): Boolean =
        Regex("""(?m)^${Regex.escape(key)}\s*=""").containsMatchIn(extractTomlSectionBody(toml, section))

    private fun extractTomlSectionBody(toml: String, section: String): String {
        val sectionHeader = "[$section]"
        val sectionIndex = toml.indexOf(sectionHeader)
        if (sectionIndex < 0) return ""
        val afterSection = toml.substring(sectionIndex + sectionHeader.length)
        val nextSection = Regex("""(?m)^\[[^\]]+\]\s*$""").find(afterSection)
        return if (nextSection != null) {
            afterSection.substring(0, nextSection.range.first)
        } else {
            afterSection
        }
    }

    private fun findTomlEntry(toml: String, section: String, key: String): Pair<String, String>? {
        val sectionBody = extractTomlSectionBody(toml, section)
        val match = Regex("""(?m)^${Regex.escape(key)}\s*=\s*(.+)$""").find(sectionBody)
            ?: return null
        return key to match.groupValues[1].trim()
    }

    private fun extractVersionRefs(entryValue: String): List<String> =
        Regex("""version\.ref\s*=\s*"([^"]+)"""")
            .findAll(entryValue)
            .map { it.groupValues[1] }
            .toList()

    private fun appendTomlEntry(
        toml: String,
        section: String,
        key: String,
        value: String,
    ): String {
        if (tomlHasEntry(toml, section, key)) return toml

        val sectionHeader = "[$section]"
        val line = "$key = $value\n"
        if (!toml.contains(sectionHeader)) {
            return toml.trimEnd() + "\n\n$sectionHeader\n$line"
        }

        val sectionIndex = toml.indexOf(sectionHeader)
        val afterHeader = toml.substring(sectionIndex + sectionHeader.length)
        val nextSection = Regex("""(?m)^\[[^\]]+\]\s*$""").find(afterHeader)
        val insertAt = if (nextSection != null) {
            sectionIndex + sectionHeader.length + nextSection.range.first
        } else {
            toml.length
        }
        return toml.substring(0, insertAt) + line + toml.substring(insertAt)
    }

    private suspend fun mergeExternalCatalogEntries(
        workingDir: String,
        sourceTomlPath: String,
        gradleFilePaths: List<String>,
    ): Result<Unit> = runCatching {
        val sourceToml = fileManager.getFileAs(sourceTomlPath).getOrThrow()
        val targetTomlPath = "$workingDir/gradle/libs.versions.toml"
        var targetToml = fileManager.getFileAs(targetTomlPath).getOrThrow()

        val catalogRefs = gradleFilePaths
            .flatMap { path -> fileManager.getFileAs(path).getOrThrow().let(::extractCatalogRefs) }
            .toSet()

        val keysToMerge = catalogRefs.mapNotNull(::catalogRefToTomlKey).toMutableSet()
        val pendingVersionRefs = ArrayDeque<String>()

        keysToMerge.forEach { (section, key) ->
            if (tomlHasEntry(targetToml, section, key)) return@forEach
            val entry = findTomlEntry(sourceToml, section, key) ?: return@forEach
            targetToml = appendTomlEntry(targetToml, section, entry.first, entry.second)
            if (section == "libraries" || section == "plugins") {
                pendingVersionRefs.addAll(extractVersionRefs(entry.second))
            }
        }

        while (pendingVersionRefs.isNotEmpty()) {
            val versionKey = pendingVersionRefs.removeFirst()
            if (tomlHasEntry(targetToml, "versions", versionKey)) continue
            val entry = findTomlEntry(sourceToml, "versions", versionKey) ?: continue
            targetToml = appendTomlEntry(targetToml, "versions", entry.first, entry.second)
        }

        fileManager.writeFile(
            path = targetTomlPath,
            content = targetToml.encodeToByteArray()
        ).getOrThrow()
    }

    private suspend fun addModuleDependencyToTarget(
        workingDir: String,
        targetModule: String,
        dependency: String,
    ) {
        val targetGradlePath = "$workingDir/$targetModule/build.gradle.kts"
        val targetModuleGradleContent = fileManager.getFileAs(path = targetGradlePath).getOrThrow()
        val updated = if (dependency in targetModuleGradleContent) {
            targetModuleGradleContent
        } else {
            addDependencyToCommonMain(
                content = targetModuleGradleContent,
                dependency = dependency,
                targetModule = targetModule,
            )
        }

        check(dependency in updated) {
            "Failed to add '$dependency' to commonMain.dependencies in '$targetModule/build.gradle.kts'."
        }

        if (updated != targetModuleGradleContent) {
            fileManager.writeFile(
                path = targetGradlePath,
                content = updated.encodeToByteArray()
            ).getOrThrow()
        }
    }


    override suspend fun includeModule(
        workingDir: String,
        module: StarterModules,
        mode: ProjectMode,
        packageName: String?,
        targetModule: String,
    ): Result<Unit> = runCatching {
        if (mode == ProjectMode.LIB) {
            if (LOCAL_MODULES.find { it == module.moduleGradlePath() } != null)
                throw IllegalStateException("Local modules can't be included in Library Mode")

            val sourceCode = sourceCodeProvider.getSourceCode().getOrThrow()
            val sourceCodePath = extractSourceCodeTo(
                workingDir = workingDir,
                version = sourceCode.version,
                zipBytes = sourceCode.content,
            )

            // check if libraries exist inside libs.versions.toml, if not add them
            val tomlFilePath = "$workingDir/gradle/libs.versions.toml"
            var tomlFileContent = fileManager.getFileAs(tomlFilePath).getOrThrow()

            // Ensure starter version exists.
            if (!Regex("""(?m)^starter\s*=""").containsMatchIn(tomlFileContent)) {
                tomlFileContent = tomlFileContent.replaceFirst(
                    Regex("""(?m)^\[versions]$"""),
                    """
        [versions]
        starter="${sourceCode.version}"
        """.trimIndent()
                )
            }

            // Generate missing starter library entries.
            val missingEntries = StarterModules.all()
                .filter { it.moduleGradlePath() !in LOCAL_MODULES }
                .map { starterModule ->
                    val alias = starterModule.moduleGradleDep(ProjectMode.LIB)
                        .removePrefix("libs.")
                        .replace('.', '-')

                    val artifact = starterModule.mavenArtifactId()

                    alias to """$alias = { module = "io.github.devatrii:$artifact", version.ref = "starter" }"""
                }
                .filter { (alias, _) ->
                    !Regex("""(?m)^${Regex.escape(alias)}\s*=""")
                        .containsMatchIn(tomlFileContent)
                }
                .map { it.second }

            if (missingEntries.isNotEmpty()) {
                tomlFileContent = tomlFileContent.replaceFirst(
                    Regex("""(?m)^\[libraries]$"""),
                    buildString {
                        appendLine("[libraries]")
                        missingEntries.forEach(::appendLine)
                    }
                )
            }

            fileManager.writeFile(
                path = tomlFilePath,
                content = tomlFileContent.encodeToByteArray()
            ).getOrThrow()

            addModuleDependencyToTarget(
                workingDir = workingDir,
                targetModule = targetModule,
                dependency = "implementation(${module.moduleGradleDep(mode)})",
            )
            return@runCatching
        }

        // ProjectMode.MODULE
        val sourceCode = sourceCodeProvider.getSourceCode().getOrThrow()
        val sourceCodePath = extractSourceCodeTo(
            workingDir = workingDir,
            version = sourceCode.version,
            zipBytes = sourceCode.content,
        )

        val modulesToInclude = collectModulesWithDependencies(module)
        val targetPackageName = resolveTargetPackageName(workingDir, packageName)
        val newlyCopiedLocalModulePaths = mutableListOf<String>()

        modulesToInclude.forEach { starterModule ->
            val moduleDir = "$workingDir/${starterModule.moduleFilePath()}"
            val sourceModuleDir = "$sourceCodePath/${starterModule.moduleFilePath()}"
            val moduleGradlePath = "$moduleDir/build.gradle.kts"

            if (fileManager.getFile(moduleGradlePath).isFailure) {
                if (fileManager.getFile("$sourceModuleDir/build.gradle.kts").isFailure) {
                    throw IllegalStateException(
                        "Module '${starterModule.moduleGradlePath()}' not found in source code."
                    )
                }
                copyDirectory(from = sourceModuleDir, to = moduleDir).getOrThrow()
                if (starterModule.moduleGradlePath() in LOCAL_MODULES) {
                    newlyCopiedLocalModulePaths += moduleDir
                }
            }

            addModuleInsideSettingsGradleKts(
                module = starterModule.moduleGradlePath(),
                workingDir = workingDir,
            ).getOrThrow()
        }

        if (newlyCopiedLocalModulePaths.isNotEmpty()) {
            updatePackageNameInModulePaths(
                modulePaths = newlyCopiedLocalModulePaths,
                targetPackageName = targetPackageName,
            ).getOrThrow()
        }

        val gradleFiles = modulesToInclude.flatMap { starterModule ->
            fileManager.getFilesRecursively("$workingDir/${starterModule.moduleFilePath()}")
                .filter { it.endsWith("build.gradle.kts") }
        }
        mergeExternalCatalogEntries(
            workingDir = workingDir,
            sourceTomlPath = "$sourceCodePath/gradle/libs.versions.toml",
            gradleFilePaths = gradleFiles,
        ).getOrThrow()

        addModuleDependencyToTarget(
            workingDir = workingDir,
            targetModule = targetModule,
            dependency = "implementation(${module.moduleGradleDep(ProjectMode.MODULE)})",
        )
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