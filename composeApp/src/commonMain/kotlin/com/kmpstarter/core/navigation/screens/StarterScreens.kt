package com.kmpstarter.core.navigation.screens

import kotlinx.serialization.Serializable

@Serializable
sealed class StarterScreens {
    @Serializable
    data object Root : StarterScreens()

    @Serializable
    data object WelcomeScreen : StarterScreens()
}