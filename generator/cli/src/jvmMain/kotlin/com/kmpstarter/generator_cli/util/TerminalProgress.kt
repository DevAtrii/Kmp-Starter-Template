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

package com.kmpstarter.generator_cli.util

import com.kmpstarter.generator_domain.GenerateProgress
import com.kmpstarter.generator_domain.GenerateStep

class TerminalProgress : GenerateProgress {

    private var currentStep: GenerateStep? = null
    private var completed = 0

    override fun onStep(step: GenerateStep) {
        finishCurrent()
        currentStep = step
        print("\r  · ${step.message}...")
        System.out.flush()
    }

    fun finish(success: Boolean) {
        finishCurrent(success)
        println()
    }

    private fun finishCurrent(success: Boolean = true) {
        val step = currentStep ?: return
        completed += 1
        val mark = if (success) "✓" else "✗"
        print("\r  $mark ${step.message}".padEnd(72))
        println()
        System.out.flush()
        currentStep = null
    }
}
