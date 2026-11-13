package com.humanbody.atlasquiz.data.local

import android.content.Context
import com.humanbody.atlasquiz.domain.model.*
import com.humanbody.atlasquiz.data.sample.SampleData
import org.json.JSONArray
import org.json.JSONObject

class AtlasPreferences(context: Context) {
    private val preferences = context.getSharedPreferences("human_body_atlas_quiz", Context.MODE_PRIVATE)

    fun read(): AtlasSnapshot {
        val raw = preferences.getString("atlas_v1", null) ?: return AtlasSnapshot(loaded = true)
        val root = JSONObject(raw)
        require(root.optInt("schema", 1) == 1)
        val settingsJson = root.optJSONObject("settings") ?: JSONObject()
        val selected = settingsJson.optString("system", "circulatory").takeIf { id -> SampleData.systems.any { it.id == id } } ?: "circulatory"
        val settings = AtlasSettings(
            selected,
            Difficulty.entries.firstOrNull { it.name == settingsJson.optString("difficulty") } ?: Difficulty.Beginner,
            settingsJson.optBoolean("haptics", true), settingsJson.optBoolean("motion", false)
        )
        val knownParts = SampleData.systems.flatMap { it.parts }.map { it.id }.toSet()
        val explored = strings(root.optJSONArray("explored")).intersect(knownParts)
        val scoresJson = root.optJSONObject("best") ?: JSONObject()
        val scores = buildMap {
            SampleData.systems.forEach { system -> Difficulty.entries.forEach { difficulty ->
                val key = "${system.id}:${difficulty.name}"
                val value = scoresJson.optInt(key, 0)
                if (value in 1..100) put(key, value)
            } }
        }
        val recentJson = root.optJSONArray("results") ?: JSONArray()
        val recent = buildList {
            for (index in 0 until minOf(20, recentJson.length())) {
                runCatching { decodeResult(recentJson.getJSONObject(index)) }.getOrNull()?.let { add(it) }
            }
        }
        return AtlasSnapshot(settings, LearningProgress(
            explored, scores, root.optInt("attempts", 0).coerceAtLeast(0), recent,
            strings(root.optJSONArray("saved_ids")) + recent.map { it.roundId }
        ), loaded = true)
    }

    fun write(snapshot: AtlasSnapshot) {
        val settings = snapshot.settings
        val progress = snapshot.progress
        val root = JSONObject().put("schema", 1)
            .put("settings", JSONObject().put("system", settings.selectedSystem).put("difficulty", settings.difficulty.name)
                .put("haptics", settings.haptics).put("motion", settings.reducedMotion))
            .put("explored", JSONArray(progress.exploredParts.toList()))
            .put("best", JSONObject(progress.bestScores))
            .put("attempts", progress.attempts)
            .put("saved_ids", JSONArray(progress.savedRoundIds.toList()))
            .put("results", JSONArray().apply { progress.recentResults.forEach { put(encodeResult(it)) } })
        check(preferences.edit().putString("atlas_v1", root.toString()).commit())
    }

    private fun strings(array: JSONArray?): Set<String> = buildSet {
        if (array != null) for (index in 0 until array.length()) {
            val value = array.optString(index)
            if (value.isNotBlank()) add(value)
        }
    }

    private fun encodeResult(result: QuizResult): JSONObject = JSONObject()
        .put("id", result.roundId).put("system", result.systemId).put("difficulty", result.difficulty.name)
        .put("reason", result.reason.name).put("time", result.timestamp)
        .put("entries", JSONArray().apply { result.entries.forEach { entry ->
            put(JSONObject().put("target", entry.targetId).put("submitted", entry.submittedId ?: JSONObject.NULL))
        } })

    private fun decodeResult(json: JSONObject): QuizResult {
        val system = SampleData.systems.first { it.id == json.getString("system") }
        val difficulty = Difficulty.valueOf(json.getString("difficulty"))
        val entriesJson = json.getJSONArray("entries")
        require(entriesJson.length() == difficulty.labelCount)
        val entries = system.parts.take(difficulty.labelCount).mapIndexed { index, part ->
            val entry = entriesJson.getJSONObject(index)
            require(entry.getString("target") == part.id)
            val submitted = if (entry.isNull("submitted")) null else entry.getString("submitted")
            require(submitted == null || system.parts.take(difficulty.labelCount).any { it.id == submitted })
            ReviewEntry(part.id, submitted, when (submitted) {
                null -> ReviewOutcome.Unattempted
                part.id -> ReviewOutcome.Correct
                else -> ReviewOutcome.Incorrect
            })
        }
        return QuizResult(json.getString("id"), system.id, difficulty, entries,
            CompletionReason.valueOf(json.getString("reason")), json.optLong("time", 0))
    }
}
