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

import com.kmpstarter.generator_data.impl.FileManagerImpl
import com.kmpstarter.generator_domain.ProjectMode
import com.kmpstarter.generator_domain.StarterJson
import com.kmpstarter.generator_domain.StarterModules
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IncludeModuleTest {

    @Test
    fun libModeAddsLayoutsAsMavenLibrary() = runBlocking {
        val env = existingProject(mode = ProjectMode.LIB)
        env.repo.includeModule(
            workingDir = env.projectDir.toString(),
            module = StarterModules.Starter.Ui.Layouts,
            mode = ProjectMode.LIB,
            packageName = "com.example.notes",
            targetModule = "composeApp",
        ).getOrThrow()

        val composeApp = env.projectDir.resolve("composeApp/build.gradle.kts").readText()
        assertContains(composeApp, "implementation(libs.starter.ui.layouts)")
        assertContains(composeApp, "implementation(libs.starter.ui.components)")
        assertContains(composeApp, "implementation(libs.starter.core)")
        assertContains(composeApp, "implementation(libs.starter.utils)")
        assertContains(composeApp, "implementation(libs.starter.native.bindings)")

        val toml = env.projectDir.resolve("gradle/libs.versions.toml").readText()
        assertContains(toml, "starter-ui-layouts")
        assertContains(toml, """starter="0.6.0"""")
    }

    @Test
    fun libModeCopiesLocalDatabaseModule() = runBlocking {
        val env = existingProject(mode = ProjectMode.LIB)
        env.repo.includeModule(
            workingDir = env.projectDir.toString(),
            module = StarterModules.Features.Database,
            mode = ProjectMode.LIB,
        ).getOrThrow()

        assertTrue(Files.exists(env.projectDir.resolve("features/database/build.gradle.kts")))
        val settings = env.projectDir.resolve("settings.gradle.kts").readText()
        assertContains(settings, """include(":features:database")""")
        val composeApp = env.projectDir.resolve("composeApp/build.gradle.kts").readText()
        assertContains(composeApp, "implementation(projects.features.database)")
    }

    @Test
    fun libModeRequiresStarterJson() = runBlocking {
        val env = existingProject(mode = ProjectMode.LIB, writeStarterJson = false)
        val error = assertFailsWith<IllegalStateException> {
            env.repo.includeModule(
                workingDir = env.projectDir.toString(),
                module = StarterModules.Starter.Ui.Layouts,
                mode = ProjectMode.LIB,
            ).getOrThrow()
        }
        assertContains(error.message ?: "", "init")
    }

    @Test
    fun moduleModeCopiesLayoutsAndItsDependencies() = runBlocking {
        val env = existingProject(mode = ProjectMode.MODULE)
        env.repo.includeModule(
            workingDir = env.projectDir.toString(),
            module = StarterModules.Starter.Ui.Layouts,
            mode = ProjectMode.MODULE,
            packageName = "com.example.notes",
            targetModule = "composeApp",
        ).getOrThrow()

        assertTrue(Files.exists(env.projectDir.resolve("starter/ui/layouts/build.gradle.kts")))
        assertTrue(Files.exists(env.projectDir.resolve("starter/ui/components/build.gradle.kts")))
        assertTrue(Files.exists(env.projectDir.resolve("starter/core/build.gradle.kts")))
        assertTrue(Files.exists(env.projectDir.resolve("starter/native/bindings/build.gradle.kts")))

        val settings = env.projectDir.resolve("settings.gradle.kts").readText()
        assertContains(settings, """include(":starter:ui:layouts")""")
        assertContains(settings, """include(":starter:ui:components")""")
        assertContains(settings, """include(":starter:core")""")

        val composeApp = env.projectDir.resolve("composeApp/build.gradle.kts").readText()
        assertContains(composeApp, "implementation(projects.starter.ui.layouts)")
        assertEquals("0.6.0", env.provider.lastRequestedVersion)
    }

    @Test
    fun excludeLibRemovesLayoutsDep() = runBlocking {
        val env = existingProject(mode = ProjectMode.LIB)
        env.repo.includeModule(
            workingDir = env.projectDir.toString(),
            module = StarterModules.Starter.Ui.Layouts,
            mode = ProjectMode.LIB,
            targetModule = "composeApp",
        ).getOrThrow()

        env.repo.excludeModule(
            workingDir = env.projectDir.toString(),
            module = StarterModules.Starter.Ui.Layouts,
            mode = ProjectMode.LIB,
            targetModule = "composeApp",
        ).getOrThrow()

        val composeApp = env.projectDir.resolve("composeApp/build.gradle.kts").readText()
        assertFalse("libs.starter.ui.layouts" in composeApp)
        assertContains(composeApp, "implementation(libs.starter.ui.components)")
    }

    @Test
    fun excludeModuleRemovesSettingsIncludeAndDir() = runBlocking {
        val env = existingProject(mode = ProjectMode.MODULE)
        env.repo.includeModule(
            workingDir = env.projectDir.toString(),
            module = StarterModules.Starter.Ui.Layouts,
            mode = ProjectMode.MODULE,
            targetModule = "composeApp",
        ).getOrThrow()

        env.repo.excludeModule(
            workingDir = env.projectDir.toString(),
            module = StarterModules.Starter.Ui.Layouts,
            mode = ProjectMode.MODULE,
            targetModule = "composeApp",
        ).getOrThrow()

        val settings = env.projectDir.resolve("settings.gradle.kts").readText()
        assertFalse("""include(":starter:ui:layouts")""" in settings)
        assertTrue("""include(":starter:ui:components")""" in settings)
        assertFalse(Files.exists(env.projectDir.resolve("starter/ui/layouts/build.gradle.kts")))
        val composeApp = env.projectDir.resolve("composeApp/build.gradle.kts").readText()
        assertFalse("projects.starter.ui.layouts" in composeApp)
    }

    private suspend fun existingProject(
        mode: ProjectMode,
        writeStarterJson: Boolean = true,
    ): ExistingProject {
        val root = Files.createTempDirectory("starter-include")
        val template = root.resolve("template")
        MiniStarterTemplate.write(template)
        val zip = FileManagerImpl().createZip(template.toString()).getOrThrow()

        val projectDir = Files.createDirectories(root.resolve("app"))
        Files.writeString(
            projectDir.resolve("settings.gradle.kts").also {
                Files.createDirectories(it.parent)
            },
            """
            rootProject.name = "NotesApp"
            include(":composeApp")
            """.trimIndent() + "\n",
        )
        Files.createDirectories(projectDir.resolve("composeApp"))
        Files.writeString(
            projectDir.resolve("composeApp/build.gradle.kts"),
            """
            kotlin {
                sourceSets {
                    commonMain.dependencies {
                        implementation(projects.features.core.data)

                        // External Libraries

                    }
                }
            }
            """.trimIndent() + "\n",
        )
        Files.createDirectories(projectDir.resolve("gradle"))
        Files.writeString(
            projectDir.resolve("gradle/libs.versions.toml"),
            """
            [versions]
            kotlin = "2.0.0"

            [libraries]
            kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }

            [plugins]
            kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
            """.trimIndent() + "\n",
        )
        if (writeStarterJson) {
            Files.writeString(
                projectDir.resolve("starter.json"),
                Json.encodeToString(
                    StarterJson(
                        packageName = "com.example.notes",
                        starterVersion = "0.6.0",
                        mode = mode,
                    ),
                ),
            )
        }

        val fileManager = CwdFileManager(root.toString())
        val provider = FixedSourceCodeProvider(zip)
        val repo = StarterProjectsRepositoryImpl(
            fileManager = fileManager,
            sourceCodeProvider = provider,
        )
        return ExistingProject(projectDir = projectDir, repo = repo, provider = provider)
    }

    private class ExistingProject(
        val projectDir: Path,
        val repo: StarterProjectsRepositoryImpl,
        val provider: FixedSourceCodeProvider,
    )
}
