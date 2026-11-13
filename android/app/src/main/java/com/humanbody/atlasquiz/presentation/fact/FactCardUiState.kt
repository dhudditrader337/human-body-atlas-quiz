package com.humanbody.atlasquiz.presentation.fact

import com.humanbody.atlasquiz.domain.model.AnatomySystem

data class FactCardUiState(
    val system: AnatomySystem? = null,
    val index: Int = -1,
    val saving: Boolean = false,
    val saved: Boolean = false,
    val error: Boolean = false
)
