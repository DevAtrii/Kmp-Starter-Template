/*
 *
 *  *
 *  *  * Copyright (c) 2025
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

package com.kmpstarter.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import kmpstarter.composeapp.generated.resources.Res
import kmpstarter.composeapp.generated.resources.poppins_regular
import kmpstarter.composeapp.generated.resources.poppins_thin
import org.jetbrains.compose.resources.Font

@Composable
fun getAppFontFamily() = FontFamily(
    Font(
        resource = Res.font.poppins_thin,
        weight = FontWeight.Thin
    ),
    Font(
        resource = Res.font.poppins_regular,
        weight = FontWeight.Normal
    ),

)




// Default Material 3 typography values
val baselineTypography = Typography()


