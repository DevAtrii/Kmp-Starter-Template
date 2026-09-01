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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Production-shaped adopt: unzip the real JetBrains KMP wizard project
 * (`generator/KotlinProject.zip`, module is `shared` not `composeApp`) and add
 * the starter template in both library and module modes.
 */
class AdoptExistingJetbrainsProjectTest {

    @Test
    fun libModeAddsStarterLibrariesAndLocalModulesIntoShared() = runBlocking {
        val env = unzipJetbrainsProject()
        val result = env.repo.adoptExistingProject(
            workingDir = env.projectDir.toString(),
            mode = ProjectMode.LIB,
        ).getOrThrow()

        assertEquals("shared", result.targetModule)
        assertEquals("org.example.project", result.packageName)
        assertTrue(result.included.containsAll(StarterModules.required().map { it.moduleGradlePath() }))

        val starterJson = Json.decodeFromString<StarterJson>(env.read("starter.json"))
        assertEquals("org.example.project", starterJson.packageName)
        assertEquals(ProjectMode.LIB, starterJson.mode)
        assertEquals("0.6.0", starterJson.starterVersion)

        val shared = env.read("shared/build.gradle.kts")
        assertContains(shared, "commonMain.dependencies")
        assertContains(shared, "implementation(libs.starter.core)")
        assertContains(shared, "implementation(libs.starter.utils)")
        assertContains(shared, "implementation(libs.starter.native.bindings)")
        assertContains(shared, "implementation(libs.starter.ui.utils)")
        assertContains(shared, "implementation(libs.starter.ui.components)")
        assertContains(shared, "implementation(libs.starter.ui.layouts)")
        assertContains(shared, "implementation(libs.starter.feature.navigation)")
        assertContains(shared, "implementation(libs.starter.feature.locale)")
        assertContains(shared, "implementation(projects.features.core.data)")
        assertContains(shared, "implementation(projects.features.core.domain)")
        assertContains(shared, "implementation(projects.features.core.presentation)")
        assertContains(shared, "implementation(projects.features.resources)")
        assertFalse("composeApp" in shared)
        assertFalse("libs.starter.feature.database" in shared)
        assertContains(shared, "implementation(libs.compose.runtime)")

        val settings = env.read("settings.gradle.kts")
        assertContains(settings, """rootProject.name = "KotlinProject"""")
        assertContains(settings, """include(":shared")""")
        assertContains(settings, """include(":androidApp")""")
        assertContains(settings, "TYPESAFE_PROJECT_ACCESSORS")
        assertContains(settings, """include(":features:core:data")""")
        assertContains(settings, """include(":features:core:domain")""")
        assertContains(settings, """include(":features:core:presentation")""")
        assertContains(settings, """include(":features:resources")""")
        assertFalse("""include(":starter:core")""" in settings)
        assertFalse("""include(":features:navigation")""" in settings)
        assertFalse("""include(":composeApp")""" in settings)

        assertTrue(env.exists("features/core/data/build.gradle.kts"))
        assertTrue(env.exists("features/core/domain/build.gradle.kts"))
        assertTrue(env.exists("features/core/presentation/build.gradle.kts"))
        assertTrue(env.exists("features/resources/build.gradle.kts"))
        assertFalse(env.exists("starter/core/build.gradle.kts"))
        assertFalse(env.exists("features/navigation/build.gradle.kts"))
        assertFalse(env.exists("composeApp/build.gradle.kts"))

        val coreDummy = env.read("features/core/data/src/commonMain/kotlin/org/example/project/Dummy.kt")
        assertContains(coreDummy, "package org.example.project")
        assertFalse("com.kmpstarter" in coreDummy)

        val toml = env.read("gradle/libs.versions.toml")
        assertContains(toml, """starter="0.6.0"""")
        assertContains(toml, "starter-core")
        assertContains(toml, "starter-ui-layouts")
        assertContains(toml, "kotlin-test")

        assertContains(env.read(".gitignore"), ".starter")
        assertTrue(env.exists("androidApp/src/main/kotlin/org/example/project/MainActivity.kt"))
        assertContains(env.read("shared/src/commonMain/kotlin/org/example/project/App.kt"), "fun App()")
    }

    @Test
    fun moduleModeCopiesStarterSourcesAndWiresProjectAccessorsOnShared() = runBlocking {
        val env = unzipJetbrainsProject()
        val result = env.repo.adoptExistingProject(
            workingDir = env.projectDir.toString(),
            mode = ProjectMode.MODULE,
        ).getOrThrow()

        assertEquals("shared", result.targetModule)
        assertEquals("org.example.project", result.packageName)

        val shared = env.read("shared/build.gradle.kts")
        assertContains(shared, "implementation(projects.starter.core)")
        assertContains(shared, "implementation(projects.starter.ui.layouts)")
        assertContains(shared, "implementation(projects.features.core.data)")
        assertContains(shared, "implementation(projects.features.navigation)")
        assertContains(shared, "implementation(projects.features.locale)")
        assertContains(shared, "implementation(projects.features.resources)")
        assertFalse("libs.starter.core" in shared)
        assertContains(shared, "implementation(libs.compose.material3)")

        val settings = env.read("settings.gradle.kts")
        assertContains(settings, "TYPESAFE_PROJECT_ACCESSORS")
        assertContains(settings, """include(":starter:core")""")
        assertContains(settings, """include(":starter:ui:layouts")""")
        assertContains(settings, """include(":features:core:data")""")
        assertContains(settings, """include(":features:navigation")""")
        assertContains(settings, """include(":features:locale")""")
        assertContains(settings, """include(":shared")""")

        assertTrue(env.exists("starter/core/build.gradle.kts"))
        assertTrue(env.exists("starter/ui/layouts/build.gradle.kts"))
        assertTrue(env.exists("features/core/data/build.gradle.kts"))
        assertTrue(env.exists("features/navigation/build.gradle.kts"))
        assertTrue(env.exists("features/locale/build.gradle.kts"))
        assertFalse(env.exists("composeApp/build.gradle.kts"))

        val localDummy = env.read("features/core/data/src/commonMain/kotlin/org/example/project/Dummy.kt")
        assertContains(localDummy, "package org.example.project")

        val starterDummy = env.read("starter/core/src/commonMain/kotlin/com/kmpstarter/Dummy.kt")
        assertContains(starterDummy, "package com.kmpstarter")

        val starterJson = Json.decodeFromString<StarterJson>(env.read("starter.json"))
        assertEquals(ProjectMode.MODULE, starterJson.mode)
        assertEquals("org.example.project", starterJson.packageName)
        assertContains(env.read(".gitignore"), ".starter")
    }

    @Test
    fun includeOnJetbrainsProjectDefaultsToSharedInsteadOfComposeApp() = runBlocking {
        val env = unzipJetbrainsProject()
        Files.writeString(
            env.projectDir.resolve("starter.json"),
            Json.encodeToString(
                StarterJson(
                    packageName = "org.example.project",
                    starterVersion = "0.6.0",
                    mode = ProjectMode.LIB,
                ),
            ),
        )

        env.repo.includeModule(
            workingDir = env.projectDir.toString(),
            module = StarterModules.Starter.Ui.Layouts,
            mode = ProjectMode.LIB,
        ).getOrThrow()

        val shared = env.read("shared/build.gradle.kts")
        assertContains(shared, "implementation(libs.starter.ui.layouts)")
        assertFalse(env.exists("composeApp/build.gradle.kts"))
    }

    private suspend fun unzipJetbrainsProject(): AdoptEnv {
        val root = withContext(Dispatchers.IO) {
            Files.createTempDirectory("jb-kmp-adopt")
        }
        val zip = locateKotlinProjectZip()
        val fileManager = FileManagerImpl()
        fileManager.extractZip(zip.toString(), root.toString()).getOrThrow()
        val projectDir = root.resolve("KotlinProject")
        check(Files.exists(projectDir.resolve("settings.gradle.kts"))) {
            "Expected JetBrains project at $projectDir"
        }
        check(Files.exists(projectDir.resolve("shared/build.gradle.kts"))) {
            "JetBrains fixture must use shared/, not composeApp/"
        }

        val template = root.resolve("starter-template")
        MiniStarterTemplate.write(template)
        val starterZip = fileManager.createZip(template.toString()).getOrThrow()
        val repo = StarterProjectsRepositoryImpl(
            fileManager = CwdFileManager(root.toString()),
            sourceCodeProvider = FixedSourceCodeProvider(starterZip),
        )
        return AdoptEnv(projectDir = projectDir, repo = repo)
    }

    private fun locateKotlinProjectZip(): Path {
        var dir = Path.of(System.getProperty("user.dir")).toAbsolutePath()
        repeat(8) {
            val candidates = listOf(
                dir.resolve("KotlinProject.zip"),
                dir.resolve("generator/KotlinProject.zip"),
            )
            candidates.firstOrNull { Files.isRegularFile(it) }?.let { return it }
            dir = dir.parent ?: return@repeat
        }
        error("KotlinProject.zip not found. Expected generator/KotlinProject.zip")
    }

    private class AdoptEnv(
        val projectDir: Path,
        val repo: StarterProjectsRepositoryImpl,
    ) {
        fun read(relative: String): String = projectDir.resolve(relative).readText()
        fun exists(relative: String): Boolean = projectDir.resolve(relative).exists()
    }
}
