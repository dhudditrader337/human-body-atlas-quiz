package com.humanbody.atlasquiz.presentation.game

import com.humanbody.atlasquiz.domain.model.*

enum class GamePhase { Ready, Feedback, Finished }

data class GameUiState(
    val system: AnatomySystem? = null,
    val round: QuizRound? = null,
    val selectedLabel: String? = null,
    val phase: GamePhase = GamePhase.Ready,
    val correctCount: Int = 0,
    val lastCorrect: String? = null,
    val result: QuizResult? = null,
    val finishDialog: Boolean = false,
    val pauseDialog: Boolean = false,
    val haptics: Boolean = true
)
