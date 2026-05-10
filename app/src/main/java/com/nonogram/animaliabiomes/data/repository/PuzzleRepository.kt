package com.nonogram.animaliabiomes.data.repository

import com.nonogram.animaliabiomes.data.model.Biome
import com.nonogram.animaliabiomes.data.model.Puzzle
import com.nonogram.animaliabiomes.data.model.Stage

interface PuzzleRepository {
    suspend fun getBiomes(): List<Biome>
    suspend fun getBiome(biomeId: Int): Biome?
    suspend fun getStage(biomeId: Int, stageId: Int): Stage?
    suspend fun getPuzzle(biomeId: Int, puzzleId: Int): Puzzle?
}
