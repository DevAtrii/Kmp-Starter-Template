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
fun getPoppinsFontFamily() = FontFamily(
    Font(
        resource = Res.font.poppins_thin,
        weight = FontWeight.Thin
    ),
    Font(
        resource = Res.font.poppins_regular,
        weight = FontWeight.Normal
    ),
   /*
    Remove poppins medium & bold because they look ugly on the app
    but you can change them if you want
    Font(
        resource = Res.font.poppins_medium,
        weight = FontWeight.Medium
    ),
    Font(
        resource = Res.font.poppins_bold,
        weight = FontWeight.Bold
    )*/
)




// Default Material 3 typography values
val baselineTypography = Typography()


