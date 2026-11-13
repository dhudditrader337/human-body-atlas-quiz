package com.humanbody.atlasquiz.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humanbody.atlasquiz.core.navigation.Screen
import com.humanbody.atlasquiz.domain.model.QuizResult
import com.humanbody.atlasquiz.domain.repository.AtlasRepository
import com.humanbody.atlasquiz.presentation.app.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameOverViewModel(private val repository: AtlasRepository, result: QuizResult) : ViewModel() {
    private val mutableState = MutableStateFlow(GameOverUiState(result, repository.systems.firstOrNull { it.id == result.systemId }))
    val uiState = mutableState.asStateFlow()
    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events = channel.receiveAsFlow()
    init { save() }
    fun save() {
        val result = mutableState.value.result ?: return
        mutableState.update { it.copy(saving = true, error = false) }
        viewModelScope.launch {
            val saved = repository.saveResult(result)
            val receipt = saved.getOrNull()
            mutableState.update { it.copy(saving = false, saved = saved.isSuccess, error = saved.isFailure,
                previousBest = receipt?.previousBest ?: 0, newBest = receipt?.newBest ?: false,
                best = repository.snapshot.value.progress.bestScores[result.bestKey] ?: 0) }
            if (receipt?.newBest == true && repository.snapshot.value.settings.haptics) channel.send(UiEvent.Haptic)
        }
    }
    fun playAgain() { mutableState.value.result?.let { emit(UiEvent.StartGame(it.systemId, it.difficulty)) } }
    fun readFact(id: String) { mutableState.value.result?.let { emit(UiEvent.Navigate(Screen.FactCard(it.systemId, id))) } }
    fun menu() = emit(UiEvent.Menu)
    fun back() = emit(UiEvent.Back)
    private fun emit(event: UiEvent) { viewModelScope.launch { channel.send(event) } }
}
