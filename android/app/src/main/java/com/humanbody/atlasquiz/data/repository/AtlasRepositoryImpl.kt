package com.humanbody.atlasquiz.data.repository

import com.humanbody.atlasquiz.data.local.AtlasPreferences
import com.humanbody.atlasquiz.data.sample.SampleData
import com.humanbody.atlasquiz.domain.model.*
import com.humanbody.atlasquiz.domain.repository.AtlasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class AtlasRepositoryImpl(private val preferences: AtlasPreferences) : AtlasRepository {
    private val mutableSnapshot = MutableStateFlow(AtlasSnapshot())
    override val snapshot = mutableSnapshot.asStateFlow()
    override val systems = SampleData.systems
    private val mutex = Mutex()

    override suspend fun load(force: Boolean) = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (!mutableSnapshot.value.loaded || force) {
                val read = runCatching { preferences.read() }
                mutableSnapshot.update { current -> read.getOrElse { current.copy(loaded = true, readError = true) } }
            }
        }
    }

    override suspend fun saveSettings(settings: AtlasSettings): Result<Unit> = write { it.copy(settings = settings) }

    override suspend fun markExplored(partId: String): Result<Unit> = write {
        it.copy(progress = it.progress.copy(exploredParts = it.progress.exploredParts + partId))
    }

    override suspend fun resetProgress(): Result<Unit> = write { it.copy(progress = LearningProgress()) }

    private suspend fun write(transform: (AtlasSnapshot) -> AtlasSnapshot): Result<Unit> = withContext(Dispatchers.IO) {
        mutex.withLock {
            runCatching {
                val next = transform(mutableSnapshot.value).copy(loaded = true, readError = false)
                preferences.write(next)
                mutableSnapshot.update { next }
            }
        }
    }

    override suspend fun saveResult(result: QuizResult): Result<SaveReceipt> = withContext(Dispatchers.IO) {
        mutex.withLock {
            runCatching {
                val current = mutableSnapshot.value
                val progress = current.progress
                val best = progress.bestScores[result.bestKey] ?: 0
                if (result.roundId in progress.savedRoundIds) return@runCatching SaveReceipt(best, false)
                val isBest = result.accuracy > best
                val next = current.copy(progress = progress.copy(
                    bestScores = if (isBest) progress.bestScores + (result.bestKey to result.accuracy) else progress.bestScores,
                    attempts = progress.attempts + 1,
                    recentResults = (listOf(result) + progress.recentResults).take(20),
                    savedRoundIds = progress.savedRoundIds + result.roundId
                ), loaded = true, readError = false)
                preferences.write(next)
                mutableSnapshot.update { next }
                SaveReceipt(best, isBest)
            }
        }
    }
}
