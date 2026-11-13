package com.humanbody.atlasquiz.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humanbody.atlasquiz.core.config.GameConfig
import com.humanbody.atlasquiz.core.navigation.Screen
import com.humanbody.atlasquiz.domain.model.*
import com.humanbody.atlasquiz.domain.repository.AtlasRepository
import com.humanbody.atlasquiz.domain.usecase.QuizRules
import com.humanbody.atlasquiz.presentation.app.UiEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(repository: AtlasRepository, private val rules: QuizRules, private val route: Screen.Game) : ViewModel() {
    private val system = repository.systems.firstOrNull { it.id == route.systemId }
    private val mutableState = MutableStateFlow(GameUiState(
        system, system?.let { rules.createRound(it, route.difficulty, route.roundId) },
        haptics = repository.snapshot.value.settings.haptics
    ))
    val uiState = mutableState.asStateFlow()
    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events = channel.receiveAsFlow()
    private var feedbackJob: Job? = null

    fun selectLabel(id: String) {
        val state = mutableState.value
        val round = state.round ?: return
        if (state.phase != GamePhase.Ready || id in round.submissions.values || state.pauseDialog || round.labels.none { it.id == id }) return
        mutableState.update { it.copy(selectedLabel = id, lastCorrect = null) }
    }

    fun clearSelection() { mutableState.update { it.copy(selectedLabel = null) } }

    fun target(id: String) { mutableState.value.selectedLabel?.let { place(it, id) } }

    fun place(labelId: String, targetId: String) {
        val state = mutableState.value
        val round = state.round ?: return
        if (state.phase != GamePhase.Ready || state.pauseDialog || state.finishDialog) return
        val updated = rules.place(round, labelId, targetId)
        if (updated == round) return
        val correct = targetId == labelId
        val count = rules.correctCount(updated)
        mutableState.update { it.copy(round = updated, selectedLabel = null, correctCount = count,
            lastCorrect = if (correct) labelId else null) }
        if (correct && state.haptics) emit(UiEvent.Haptic)
        when {
            !correct -> complete(CompletionReason.Mismatch)
            count == round.targets.size -> complete(CompletionReason.Matched)
        }
    }

    private fun complete(reason: CompletionReason, immediate: Boolean = false) {
        val round = mutableState.value.round ?: return
        val result = rules.result(round, reason)
        mutableState.update { it.copy(result = result, phase = GamePhase.Feedback, finishDialog = false, pauseDialog = false) }
        scheduleResult(if (immediate) 0 else GameConfig.FEEDBACK_DURATION_MS)
    }

    private fun scheduleResult(duration: Long) {
        feedbackJob?.cancel()
        feedbackJob = viewModelScope.launch {
            delay(duration)
            val result = mutableState.value.result ?: return@launch
            mutableState.update { it.copy(phase = GamePhase.Finished) }
            channel.send(UiEvent.Navigate(Screen.GameOver(result)))
        }
    }

    fun requestFinish() {
        if (mutableState.value.phase == GamePhase.Ready) mutableState.update { it.copy(finishDialog = true) }
    }
    fun dismissFinish() { mutableState.update { it.copy(finishDialog = false) } }
    fun finishEarly() { if (mutableState.value.phase == GamePhase.Ready) complete(CompletionReason.FinishedEarly, true) }
    fun showPause() {
        if (mutableState.value.phase == GamePhase.Ready) mutableState.update { it.copy(pauseDialog = true) }
    }
    fun dismissPause() { mutableState.update { it.copy(pauseDialog = false) } }
    fun pause() { feedbackJob?.cancel(); feedbackJob = null }
    fun resume() {
        if (mutableState.value.phase == GamePhase.Feedback && feedbackJob?.isActive != true) scheduleResult(GameConfig.FEEDBACK_DURATION_MS)
    }
    fun results() { mutableState.value.result?.let { emit(UiEvent.Navigate(Screen.GameOver(it))) } }
    fun back() { pause(); emit(UiEvent.Back) }
    fun menu() { pause(); emit(UiEvent.Menu) }
    fun retry() = emit(UiEvent.StartGame(route.systemId, route.difficulty))
    private fun emit(event: UiEvent) { viewModelScope.launch { channel.send(event) } }
}
