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

package com.kmpstarter.generator_cli.presentation

import com.kmpstarter.generator_domain.StarterModules
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class CliModuleCatalogTest {

    @Test
    fun allSelectsEveryCatalogModule() {
        val resolved = CliModuleCatalog.resolveModules("all")
        assertEquals(CliModuleCatalog.options.size, resolved.size)
    }

    @Test
    fun unknownModuleFailsFast() {
        val error = assertFails {
            CliModuleCatalog.resolveModules("not-a-module")
        }
        assertContains(error.message ?: "", "Unknown module")
    }

    @Test
    fun selectingOptionalModuleStillKeepsRequiredOnes() {
        val resolved = CliModuleCatalog.resolveModules("feature-database")
        val ids = resolved.map { it.mavenArtifactId() }.toSet()

        assertTrue("starter-core" in ids)
        assertTrue("feature-database" in ids)
        assertTrue("feature-resources" in ids)
        assertEquals(StarterModules.Features.Database, CliModuleCatalog.findById("feature-database")?.module)
    }

    @Test
    fun selectingAnalyticsDataAlsoIncludesAnalyticsDomain() {
        val resolved = CliModuleCatalog.resolveModules("feature-analytics-data")
        val ids = resolved.map { it.mavenArtifactId() }.toSet()

        assertTrue("feature-analytics-data" in ids)
        assertTrue("feature-analytics-domain" in ids)
        assertTrue("starter-core" in ids)
    }

    @Test
    fun layoutsIdMatchesPublishedArtifact() {
        val option = CliModuleCatalog.options.first { it.module == StarterModules.Starter.Ui.Layouts }
        assertEquals("starter-ui-layouts", option.id)
        assertEquals("UI Layouts", option.label)
        assertTrue(option.required)
    }
}
