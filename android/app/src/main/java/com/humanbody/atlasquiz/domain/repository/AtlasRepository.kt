package com.humanbody.atlasquiz.domain.repository

import com.humanbody.atlasquiz.domain.model.*
import kotlinx.coroutines.flow.StateFlow

interface AtlasRepository {
    val snapshot: StateFlow<AtlasSnapshot>
    val systems: List<AnatomySystem>
    suspend fun load(force: Boolean = false)
    suspend fun saveSettings(settings: AtlasSettings): Result<Unit>
    suspend fun markExplored(partId: String): Result<Unit>
    suspend fun saveResult(result: QuizResult): Result<SaveReceipt>
    suspend fun resetProgress(): Result<Unit>
}
