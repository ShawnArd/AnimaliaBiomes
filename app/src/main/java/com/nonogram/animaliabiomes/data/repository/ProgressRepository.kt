package com.nonogram.animaliabiomes.data.repository

import android.content.Context
import com.nonogram.animaliabiomes.data.model.Biome
import com.nonogram.animaliabiomes.data.model.Stage
import kotlin.math.ceil

interface ProgressRepository {
    fun markCompleted(biomeId: Int, puzzleId: Int)
    fun isCompleted(biomeId: Int, puzzleId: Int): Boolean
    fun stageCompletion(biomeId: Int, stage: Stage): Pair<Int, Int>
    fun isStageUnlocked(biome: Biome, stage: Stage): Boolean
}

fun requiredCompletedForUnlock(targetPuzzleCount: Int): Int =
    ceil(0.75 * targetPuzzleCount).toInt()

fun isStageUnlocked(
    biome: Biome,
    stage: Stage,
    isCompleted: (biomeId: Int, puzzleId: Int) -> Boolean
): Boolean {
    val index = biome.stages.indexOfFirst { it.id == stage.id }
    if (index <= 0) return true
    val previous = biome.stages[index - 1]
    val completedInPrevious = previous.puzzles.count { isCompleted(biome.id, it.id) }
    val required = requiredCompletedForUnlock(previous.targetPuzzleCount)
    return completedInPrevious >= required
}

class SharedPreferencesProgressRepository(context: Context) : ProgressRepository {

    private val appContext = context.applicationContext

    override fun markCompleted(biomeId: Int, puzzleId: Int) {
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val completed = getCompletedIds().toMutableSet()
        completed.add(compositeKey(biomeId, puzzleId))
        prefs.edit().putStringSet(KEY_COMPLETED, completed).apply()
    }

    override fun isCompleted(biomeId: Int, puzzleId: Int): Boolean =
        getCompletedIds().contains(compositeKey(biomeId, puzzleId))

    override fun stageCompletion(biomeId: Int, stage: Stage): Pair<Int, Int> {
        val completed = stage.puzzles.count { isCompleted(biomeId, it.id) }
        return completed to stage.targetPuzzleCount
    }

    override fun isStageUnlocked(biome: Biome, stage: Stage): Boolean =
        isStageUnlocked(biome, stage, ::isCompleted)

    private fun compositeKey(biomeId: Int, puzzleId: Int): String = "$biomeId:$puzzleId"

    private fun getCompletedIds(): Set<String> {
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_MIGRATION_V1, false)) {
            migrateLegacyKeys()
        }
        return prefs.getStringSet(KEY_COMPLETED, emptySet()) ?: emptySet()
    }

    private fun migrateLegacyKeys() {
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getStringSet(KEY_COMPLETED, emptySet()) ?: emptySet()
        val migrated = existing.map { id ->
            if (id.contains(":")) id else "$LEGACY_OCEAN_BIOME_ID:$id"
        }.toSet()
        prefs.edit()
            .putStringSet(KEY_COMPLETED, migrated)
            .putBoolean(KEY_MIGRATION_V1, true)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "puzzle_progress"
        private const val KEY_COMPLETED = "completed_puzzle_ids"
        private const val KEY_MIGRATION_V1 = "progress_migration_v1_done"
        private const val LEGACY_OCEAN_BIOME_ID = 1
    }
}
