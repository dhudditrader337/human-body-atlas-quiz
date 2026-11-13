package com.humanbody.atlasquiz.presentation.gameover

import com.humanbody.atlasquiz.domain.model.*

data class GameOverUiState(
    val result: QuizResult? = null,
    val system: AnatomySystem? = null,
    val saving: Boolean = true,
    val saved: Boolean = false,
    val error: Boolean = false,
    val previousBest: Int = 0,
    val newBest: Boolean = false,
    val best: Int = 0
)
