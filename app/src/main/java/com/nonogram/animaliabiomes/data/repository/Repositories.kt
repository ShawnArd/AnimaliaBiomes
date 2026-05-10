package com.nonogram.animaliabiomes.data.repository

import android.content.Context

object Repositories {

    @Volatile
    private var puzzleRepo: PuzzleRepository? = null

    @Volatile
    private var progressRepo: ProgressRepository? = null

    fun puzzles(context: Context): PuzzleRepository {
        return puzzleRepo ?: synchronized(this) {
            puzzleRepo ?: AssetPuzzleRepository(context.applicationContext).also { puzzleRepo = it }
        }
    }

    fun progress(context: Context): ProgressRepository {
        return progressRepo ?: synchronized(this) {
            progressRepo ?: SharedPreferencesProgressRepository(context.applicationContext).also { progressRepo = it }
        }
    }
}
