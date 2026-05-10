package com.nonogram.animaliabiomes.data

import android.content.Context

object ProgressManager {

    private const val PREFS_NAME = "puzzle_progress"
    private const val KEY_COMPLETED = "completed_puzzle_ids"

    fun markCompleted(context: Context, puzzleId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val completed = getCompletedIds(context).toMutableSet()
        completed.add(puzzleId.toString())
        prefs.edit().putStringSet(KEY_COMPLETED, completed).apply()
    }

    fun isCompleted(context: Context, puzzleId: Int): Boolean =
        getCompletedIds(context).contains(puzzleId.toString())

    private fun getCompletedIds(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_COMPLETED, emptySet()) ?: emptySet()
    }
}
