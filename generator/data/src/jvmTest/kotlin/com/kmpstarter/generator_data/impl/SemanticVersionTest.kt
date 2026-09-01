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

package com.kmpstarter.generator_data.impl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SemanticVersionTest {

    @Test
    fun parseStripsVPrefixAndPrerelease() {
        assertEquals(SemanticVersion(0, 6, 0), SemanticVersion.parse("0.6.0"))
        assertEquals(SemanticVersion(0, 6, 0), SemanticVersion.parse("v0.6.0"))
        assertEquals(SemanticVersion(1, 2, 3), SemanticVersion.parse("V1.2.3-beta.1"))
        assertEquals(SemanticVersion(1, 0, 0), SemanticVersion.parse("1.0.0+build.9"))
    }

    @Test
    fun parseRejectsIncompleteOrJunk() {
        assertNull(SemanticVersion.parse("0.6"))
        assertNull(SemanticVersion.parse("latest"))
        assertNull(SemanticVersion.parse(""))
    }

    @Test
    fun compareOrdersNewestReleaseLast() {
        val older = SemanticVersion.parse("0.5.7")!!
        val newer = SemanticVersion.parse("0.6.0")!!
        assertTrue(older < newer)
        assertTrue(newer > older)
        assertEquals(0, newer.compareTo(SemanticVersion(0, 6, 0)))
    }

    @Test
    fun toStringIsMajorMinorPatch() {
        assertEquals("0.6.0", SemanticVersion(0, 6, 0).toString())
    }
}
