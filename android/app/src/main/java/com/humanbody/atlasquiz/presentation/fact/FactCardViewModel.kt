package com.humanbody.atlasquiz.presentation.fact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humanbody.atlasquiz.domain.repository.AtlasRepository
import com.humanbody.atlasquiz.presentation.app.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FactCardViewModel(private val repository: AtlasRepository, systemId: String, partId: String) : ViewModel() {
    private val system = repository.systems.firstOrNull { it.id == systemId }
    private val mutableState = MutableStateFlow(FactCardUiState(system, system?.parts?.indexOfFirst { it.id == partId } ?: -1))
    val uiState = mutableState.asStateFlow()
    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events = channel.receiveAsFlow()
    init { saveVisit() }
    fun move(delta: Int) {
        val parts = system?.parts ?: return
        val index = mutableState.value.index + delta
        if (index !in parts.indices || mutableState.value.saving) return
        mutableState.update { it.copy(index = index, saved = false, error = false) }
        saveVisit()
    }
    fun saveVisit() {
        val part = system?.parts?.getOrNull(mutableState.value.index) ?: return
        if (mutableState.value.saving) return
        mutableState.update { it.copy(saving = true, error = false) }
        viewModelScope.launch {
            val wasExplored = part.id in repository.snapshot.value.progress.exploredParts
            val result = repository.markExplored(part.id)
            mutableState.update { it.copy(saving = false, saved = result.isSuccess, error = result.isFailure) }
            if (result.isSuccess && !wasExplored && repository.snapshot.value.settings.haptics) channel.send(UiEvent.Haptic)
        }
    }
    fun play() { system?.let { emit(UiEvent.StartGame(it.id, repository.snapshot.value.settings.difficulty)) } }
    fun back() = emit(UiEvent.Back)
    private fun emit(event: UiEvent) { viewModelScope.launch { channel.send(event) } }
}
