package com.humanbody.atlasquiz.domain.model

data class AnatomyPart(val id: String, val x: Float, val y: Float)

data class AnatomySystem(val id: String, val parts: List<AnatomyPart>)

enum class Difficulty(val labelCount: Int) { Beginner(3), Explorer(4), Challenge(5) }

data class AtlasSettings(
    val selectedSystem: String = "circulatory",
    val difficulty: Difficulty = Difficulty.Beginner,
    val haptics: Boolean = true,
    val reducedMotion: Boolean = false
)

data class QuizRound(
    val id: String,
    val systemId: String,
    val difficulty: Difficulty,
    val targets: List<AnatomyPart>,
    val labels: List<AnatomyPart>,
    val submissions: Map<String, String> = emptyMap()
)

enum class ReviewOutcome { Correct, Incorrect, Unattempted }
enum class CompletionReason { Matched, Mismatch, FinishedEarly }

data class ReviewEntry(val targetId: String, val submittedId: String?, val outcome: ReviewOutcome)

data class QuizResult(
    val roundId: String,
    val systemId: String,
    val difficulty: Difficulty,
    val entries: List<ReviewEntry>,
    val reason: CompletionReason,
    val timestamp: Long
) {
    val correctCount: Int get() = entries.count { it.outcome == ReviewOutcome.Correct }
    val totalCount: Int get() = entries.size
    val accuracy: Int get() = if (totalCount == 0) 0 else 100 * correctCount / totalCount
    val bestKey: String get() = "$systemId:${difficulty.name}"
}

data class LearningProgress(
    val exploredParts: Set<String> = emptySet(),
    val bestScores: Map<String, Int> = emptyMap(),
    val attempts: Int = 0,
    val recentResults: List<QuizResult> = emptyList(),
    val savedRoundIds: Set<String> = emptySet()
)

data class AtlasSnapshot(
    val settings: AtlasSettings = AtlasSettings(),
    val progress: LearningProgress = LearningProgress(),
    val loaded: Boolean = false,
    val readError: Boolean = false
)

data class SaveReceipt(val previousBest: Int, val newBest: Boolean)
