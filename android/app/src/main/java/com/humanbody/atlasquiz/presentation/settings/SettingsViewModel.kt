package com.humanbody.atlasquiz.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humanbody.atlasquiz.domain.model.*
import com.humanbody.atlasquiz.domain.repository.AtlasRepository
import com.humanbody.atlasquiz.presentation.app.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: AtlasRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(SettingsUiState(repository.snapshot.value.settings,
        if (repository.snapshot.value.readError) SettingsStatus.ReadError else SettingsStatus.Ready))
    val uiState = mutableState.asStateFlow()
    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events = channel.receiveAsFlow()
    init {
        viewModelScope.launch { repository.snapshot.collect { snapshot ->
            if (mutableState.value.status != SettingsStatus.Saving && mutableState.value.status != SettingsStatus.Error) {
                mutableState.update { it.copy(settings = snapshot.settings) }
            }
        } }
    }
    fun difficulty(value: Difficulty) = change(mutableState.value.settings.copy(difficulty = value))
    fun haptics(value: Boolean) = change(mutableState.value.settings.copy(haptics = value))
    fun motion(value: Boolean) = change(mutableState.value.settings.copy(reducedMotion = value))
    private fun change(settings: AtlasSettings) {
        if (mutableState.value.status == SettingsStatus.Saving) return
        mutableState.update { it.copy(settings = settings) }
        save()
    }
    fun save() {
        if (mutableState.value.status == SettingsStatus.Saving) return
        val settings = mutableState.value.settings
        mutableState.update { it.copy(status = SettingsStatus.Saving) }
        viewModelScope.launch {
            val result = repository.saveSettings(settings)
            mutableState.update { it.copy(status = if (result.isSuccess) SettingsStatus.Saved else SettingsStatus.Error) }
            if (result.isSuccess && settings.haptics) channel.send(UiEvent.Haptic)
        }
    }
    fun requestReset() { mutableState.update { it.copy(resetDialog = true) } }
    fun cancelReset() { mutableState.update { it.copy(resetDialog = false) } }
    fun reset() {
        mutableState.update { it.copy(resetDialog = false, status = SettingsStatus.Saving) }
        viewModelScope.launch {
            val result = repository.resetProgress()
            mutableState.update { it.copy(status = if (result.isSuccess) SettingsStatus.Cleared else SettingsStatus.ResetError) }
        }
    }
    fun back() { viewModelScope.launch { channel.send(UiEvent.Back) } }
}
