package com.humanbody.atlasquiz.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humanbody.atlasquiz.core.navigation.Screen
import com.humanbody.atlasquiz.domain.repository.AtlasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import kotlinx.coroutines.launch

class AppViewModel(repository: AtlasRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(AppUiState())
    val uiState = mutableState.asStateFlow()
    private val mutableScreen = MutableStateFlow<Screen>(Screen.Splash)
    val screen = mutableScreen.asStateFlow()

    init {
        viewModelScope.launch { repository.snapshot.collect { snapshot ->
            mutableState.update { it.copy(reducedMotion = snapshot.settings.reducedMotion) }
        } }
    }

    fun finishSplash() {
        if (mutableScreen.value == Screen.Splash) set(AppUiState(Screen.Menu))
    }

    fun navigate(destination: Screen) {
        val current = mutableState.value
        if (current.screen == destination || current.screen == Screen.Splash) return
        set(current.copy(screen = destination, backStack = current.backStack + current.screen))
    }

    fun back() {
        val state = mutableState.value
        if (state.backStack.isNotEmpty()) set(state.copy(screen = state.backStack.last(), backStack = state.backStack.dropLast(1)))
    }

    fun handle(event: UiEvent) {
        when (event) {
            is UiEvent.Navigate -> navigate(event.screen)
            is UiEvent.StartGame -> navigate(Screen.Game(UUID.randomUUID().toString(), event.systemId, event.difficulty))
            UiEvent.Menu -> set(AppUiState(Screen.Menu))
            UiEvent.Back -> back()
            UiEvent.Haptic -> Unit
        }
    }

    private fun set(state: AppUiState) {
        mutableState.update { state.copy(reducedMotion = it.reducedMotion) }
        mutableScreen.update { state.screen }
    }
}
