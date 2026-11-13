package com.humanbody.atlasquiz.presentation.app

import com.humanbody.atlasquiz.core.navigation.Screen

data class AppUiState(val screen: Screen = Screen.Splash, val backStack: List<Screen> = emptyList(), val reducedMotion: Boolean = false)
