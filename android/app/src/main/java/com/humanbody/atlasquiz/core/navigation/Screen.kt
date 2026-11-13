package com.humanbody.atlasquiz.core.navigation

import com.humanbody.atlasquiz.domain.model.Difficulty
import com.humanbody.atlasquiz.domain.model.QuizResult
import java.util.UUID

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data class Diagram(val systemId: String) : Screen
    data class FactCard(val systemId: String, val partId: String, val visitId: String = UUID.randomUUID().toString()) : Screen
    data class Game(val roundId: String, val systemId: String, val difficulty: Difficulty) : Screen
    data class GameOver(val result: QuizResult) : Screen
    data object Settings : Screen
}
