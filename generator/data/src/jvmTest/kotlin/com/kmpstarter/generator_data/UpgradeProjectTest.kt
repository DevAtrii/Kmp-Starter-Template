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
import kotlin.test.assertTrue

class UpgradeProjectTest {

    @Test
    fun libModeBumpsTomlAndStarterJson() = runBlocking {
        val env = existingProject(mode = ProjectMode.LIB)
        val result = env.repo.upgrade(
            workingDir = env.projectDir.toString(),
            targetVersion = "0.7.0",
        ).getOrThrow()

        assertEquals("0.6.0", result.fromVersion)
        assertEquals("0.7.0", result.toVersion)
        assertTrue(result.skippedBecauseDirty.isEmpty())

        val toml = env.projectDir.resolve("gradle/libs.versions.toml").readText()
        assertContains(toml, """starter="0.7.0"""")

        val starterJson = Json.decodeFromString<StarterJson>(
            env.projectDir.resolve("starter.json").readText(),
        )
        assertEquals("0.7.0", starterJson.starterVersion)
    }

    @Test
    fun moduleModeSkipsDirtyUnlessForced() = runBlocking {
        val env = existingProject(mode = ProjectMode.MODULE)
        env.repo.includeModule(
            workingDir = env.projectDir.toString(),
            module = StarterModules.Starter.Ui.Layouts,
            mode = ProjectMode.MODULE,
            targetModule = "composeApp",
        ).getOrThrow()

        val dummy = env.projectDir.resolve("starter/ui/layouts/src/commonMain/kotlin/com/kmpstarter/Dummy.kt")
        Files.writeString(dummy, "package com.kmpstarter\nclass DummyEdited\n")

        val skipped = env.repo.upgrade(
            workingDir = env.projectDir.toString(),
            modules = listOf(StarterModules.Starter.Ui.Layouts),
            targetVersion = "0.7.0",
            force = false,
        ).getOrThrow()

        assertEquals(listOf(":starter:ui:layouts"), skipped.skippedBecauseDirty)
        assertTrue(skipped.upgraded.isEmpty())
        assertContains(dummy.readText(), "DummyEdited")

        val forced = env.repo.upgrade(
            workingDir = env.projectDir.toString(),
            modules = listOf(StarterModules.Starter.Ui.Layouts),
            targetVersion = "0.7.0",
            force = true,
        ).getOrThrow()

        assertTrue(forced.skippedBecauseDirty.isEmpty())
        assertEquals(listOf(":starter:ui:layouts"), forced.upgraded)
        assertContains(dummy.readText(), "class Dummy")
        assertTrue("DummyEdited" !in dummy.readText())
    }

    private suspend fun existingProject(mode: ProjectMode): ExistingProject {
        val root = Files.createTempDirectory("starter-upgrade")
        val template = root.resolve("template")
        MiniStarterTemplate.write(template)
        val zip = FileManagerImpl().createZip(template.toString()).getOrThrow()

        val projectDir = Files.createDirectories(root.resolve("app"))
        Files.writeString(
            projectDir.resolve("settings.gradle.kts"),
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
            starter="0.6.0"

            [libraries]
            kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }
            """.trimIndent() + "\n",
        )
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

        val repo = StarterProjectsRepositoryImpl(
            fileManager = CwdFileManager(root.toString()),
            sourceCodeProvider = FixedSourceCodeProvider(zip),
        )
        return ExistingProject(projectDir = projectDir, repo = repo)
    }

    private class ExistingProject(
        val projectDir: Path,
        val repo: StarterProjectsRepositoryImpl,
    )
}
