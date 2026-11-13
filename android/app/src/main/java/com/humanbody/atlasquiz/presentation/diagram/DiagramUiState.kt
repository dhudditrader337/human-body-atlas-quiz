package com.humanbody.atlasquiz.presentation.diagram

import com.humanbody.atlasquiz.domain.model.AnatomySystem

data class DiagramUiState(val system: AnatomySystem? = null, val explored: Set<String> = emptySet(), val selectedPart: String? = null)
