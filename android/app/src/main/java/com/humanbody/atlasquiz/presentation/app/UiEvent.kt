package com.humanbody.atlasquiz.presentation.app

import com.humanbody.atlasquiz.core.navigation.Screen
import com.humanbody.atlasquiz.domain.model.Difficulty

sealed interface UiEvent {
    data class Navigate(val screen: Screen) : UiEvent
    data class StartGame(val systemId: String, val difficulty: Difficulty) : UiEvent
    data object Menu : UiEvent
    data object Back : UiEvent
    data object Haptic : UiEvent
}
