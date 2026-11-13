package com.humanbody.atlasquiz.presentation.menu

import com.humanbody.atlasquiz.domain.model.*

data class MenuUiState(
    val systems: List<AnatomySystem> = emptyList(),
    val settings: AtlasSettings = AtlasSettings(),
    val progress: LearningProgress = LearningProgress(),
    val loading: Boolean = true,
    val error: Boolean = false
)
