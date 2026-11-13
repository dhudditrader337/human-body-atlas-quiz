package com.humanbody.atlasquiz.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humanbody.atlasquiz.core.navigation.Screen
import com.humanbody.atlasquiz.domain.repository.AtlasRepository
import com.humanbody.atlasquiz.presentation.app.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(private val repository: AtlasRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(MenuUiState(systems = repository.systems))
    val uiState = mutableState.asStateFlow()
    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events = channel.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.snapshot.collect { snapshot ->
                mutableState.update { it.copy(settings = snapshot.settings, progress = snapshot.progress,
                    loading = !snapshot.loaded, error = snapshot.readError) }
            }
        }
        viewModelScope.launch { repository.load() }
    }

    fun selectSystem(id: String) {
        if (repository.systems.none { it.id == id }) return
        mutableState.update { it.copy(settings = it.settings.copy(selectedSystem = id)) }
        viewModelScope.launch {
            val saved = repository.saveSettings(mutableState.value.settings)
            mutableState.update { it.copy(error = saved.isFailure) }
        }
    }

    fun play() = emit(UiEvent.StartGame(mutableState.value.settings.selectedSystem, mutableState.value.settings.difficulty))
    fun explore() = emit(UiEvent.Navigate(Screen.Diagram(mutableState.value.settings.selectedSystem)))
    fun settings() = emit(UiEvent.Navigate(Screen.Settings))
    fun result(id: String) {
        mutableState.value.progress.recentResults.firstOrNull { it.roundId == id }?.let { emit(UiEvent.Navigate(Screen.GameOver(it))) }
    }
    fun retry() { viewModelScope.launch { repository.load(true) } }
    private fun emit(event: UiEvent) { viewModelScope.launch { channel.send(event) } }
}
