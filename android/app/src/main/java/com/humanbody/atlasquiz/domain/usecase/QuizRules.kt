package com.humanbody.atlasquiz.domain.usecase

import com.humanbody.atlasquiz.domain.model.*
import java.util.UUID
import kotlin.random.Random

class QuizRules {
    fun createRound(system: AnatomySystem, difficulty: Difficulty, id: String = UUID.randomUUID().toString()): QuizRound {
        val targets = system.parts.take(difficulty.labelCount)
        return QuizRound(id, system.id, difficulty, targets, targets.shuffled(Random(id.hashCode())))
    }

    fun place(round: QuizRound, labelId: String, targetId: String): QuizRound {
        if (targetId in round.submissions || round.submissions.values.contains(labelId)) return round
        if (round.targets.none { it.id == targetId } || round.labels.none { it.id == labelId }) return round
        return round.copy(submissions = round.submissions + (targetId to labelId))
    }

    fun correctCount(round: QuizRound): Int = round.submissions.count { it.key == it.value }

    fun result(round: QuizRound, reason: CompletionReason): QuizResult = QuizResult(
        round.id, round.systemId, round.difficulty,
        round.targets.map { part ->
            val submitted = round.submissions[part.id]
            ReviewEntry(part.id, submitted, when {
                submitted == null -> ReviewOutcome.Unattempted
                submitted == part.id -> ReviewOutcome.Correct
                else -> ReviewOutcome.Incorrect
            })
        }, reason, System.currentTimeMillis()
    )
}
