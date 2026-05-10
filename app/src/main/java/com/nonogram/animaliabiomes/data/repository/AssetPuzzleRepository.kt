package com.nonogram.animaliabiomes.data.repository

import android.content.Context
import android.util.Log
import com.nonogram.animaliabiomes.BuildConfig
import com.nonogram.animaliabiomes.data.PuzzleSerializer
import com.nonogram.animaliabiomes.data.PuzzleValidationException
import com.nonogram.animaliabiomes.data.model.Biome
import com.nonogram.animaliabiomes.data.model.Puzzle
import com.nonogram.animaliabiomes.data.model.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class AssetPuzzleRepository(context: Context) : PuzzleRepository {

    private val appContext = context.applicationContext
    private val loadMutex = Mutex()

    @Volatile
    private var cachedBiomes: List<Biome>? = null

    override suspend fun getBiomes(): List<Biome> = ensureLoaded()

    override suspend fun getBiome(biomeId: Int): Biome? =
        ensureLoaded().find { it.id == biomeId }

    override suspend fun getStage(biomeId: Int, stageId: Int): Stage? =
        getBiome(biomeId)?.stages?.find { it.id == stageId }

    override suspend fun getPuzzle(biomeId: Int, puzzleId: Int): Puzzle? =
        getBiome(biomeId)?.puzzles?.find { it.id == puzzleId }

    private suspend fun ensureLoaded(): List<Biome> {
        cachedBiomes?.let { return it }
        return loadMutex.withLock {
            cachedBiomes?.let { return@withLock it }
            val loaded = withContext(Dispatchers.IO) { loadAll() }
            cachedBiomes = loaded
            loaded
        }
    }

    private fun loadAll(): List<Biome> {
        val assets = appContext.assets
        val files = assets.list(PUZZLES_DIR).orEmpty()
            .filter { it.endsWith(".json") }
            .sorted()
        return files.mapNotNull { fileName ->
            try {
                val json = assets.open("$PUZZLES_DIR/$fileName").bufferedReader().use { it.readText() }
                PuzzleSerializer.loadBiome(json)
            } catch (e: PuzzleValidationException) {
                Log.e(TAG, "Failed to load biome from $fileName: ${e.message}", e)
                if (BuildConfig.DEBUG) throw e
                null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load biome from $fileName: ${e.message}", e)
                if (BuildConfig.DEBUG) throw e
                null
            }
        }
    }

    companion object {
        private const val PUZZLES_DIR = "puzzles"
        private const val TAG = "AssetPuzzleRepository"
    }
}
