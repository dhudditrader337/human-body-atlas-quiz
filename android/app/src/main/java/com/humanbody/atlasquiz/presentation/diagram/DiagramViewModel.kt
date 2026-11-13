package com.humanbody.atlasquiz.presentation.diagram

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

class DiagramViewModel(private val repository: AtlasRepository, systemId: String) : ViewModel() {
    private val mutableState = MutableStateFlow(DiagramUiState(repository.systems.firstOrNull { it.id == systemId }))
    val uiState = mutableState.asStateFlow()
    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events = channel.receiveAsFlow()
    init {
        viewModelScope.launch { repository.snapshot.collect { snapshot ->
            mutableState.update { it.copy(explored = snapshot.progress.exploredParts) }
        } }
    }
    fun openFact(id: String) {
        val system = mutableState.value.system ?: return
        if (system.parts.none { it.id == id }) return
        mutableState.update { it.copy(selectedPart = id) }
        emit(UiEvent.Navigate(Screen.FactCard(system.id, id)))
    }
    fun play() {
        mutableState.value.system?.let { emit(UiEvent.StartGame(it.id, repository.snapshot.value.settings.difficulty)) }
    }
    fun back() = emit(UiEvent.Back)
    private fun emit(event: UiEvent) { viewModelScope.launch { channel.send(event) } }
}
