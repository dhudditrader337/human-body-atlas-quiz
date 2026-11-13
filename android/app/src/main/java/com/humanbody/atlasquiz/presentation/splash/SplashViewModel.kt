package com.humanbody.atlasquiz.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humanbody.atlasquiz.core.config.GameConfig
import com.humanbody.atlasquiz.domain.repository.AtlasRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(private val repository: AtlasRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(SplashUiState())
    val uiState = mutableState.asStateFlow()
    private val _finished = MutableStateFlow(false)
    val finished = _finished.asStateFlow()

    init {
        viewModelScope.launch {
            delay(GameConfig.LOADER_DURATION_MS)
            _finished.value = true
            mutableState.update { it.copy(finished = true) }
        }
        viewModelScope.launch {
            repository.load()
            mutableState.update { it.copy(reducedMotion = repository.snapshot.value.settings.reducedMotion) }
        }
    }
}
