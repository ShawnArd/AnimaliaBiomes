package com.nonogram.animaliabiomes.data

import com.nonogram.animaliabiomes.data.model.Biome
import com.nonogram.animaliabiomes.data.model.Puzzle
import com.nonogram.animaliabiomes.data.model.Stage
import com.nonogram.animaliabiomes.data.repository.isStageUnlocked
import com.nonogram.animaliabiomes.data.repository.requiredCompletedForUnlock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StageProgressTest {

    private fun puzzle(id: Int): Puzzle = Puzzle(
        id = id,
        name = "P$id",
        gridSize = 5,
        palette = listOf(0xFFFF0000.toInt()),
        solution = listOf(listOf(1)),
        funFact = "fact"
    )

    private fun makeStage(id: Int, name: String, target: Int, puzzleIds: List<Int>): Stage =
        Stage(
            id = id,
            name = name,
            targetPuzzleCount = target,
            puzzles = puzzleIds.map { puzzle(it) }
        )

    private fun makeBiome(stages: List<Stage>): Biome =
        Biome(id = 1, name = "Ocean", stages = stages)

    @Test
    fun firstStageAlwaysUnlocked() {
        val biome = makeBiome(
            listOf(
                makeStage(1, "Shores", 4, listOf(1, 2)),
                makeStage(2, "Shallows", 4, emptyList())
            )
        )
        assertTrue(isStageUnlocked(biome, biome.stages[0]) { _, _ -> false })
    }

    @Test
    fun requiredCountFor4IsThree() {
        assertEquals(3, requiredCompletedForUnlock(4))
    }

    @Test
    fun requiredCountFor8IsSix() {
        assertEquals(6, requiredCompletedForUnlock(8))
    }

    @Test
    fun threeOfFourUnlocksNextStage() {
        val biome = makeBiome(
            listOf(
                makeStage(1, "Shores", 4, listOf(1, 2, 3, 4)),
                makeStage(2, "Shallows", 4, emptyList())
            )
        )
        val completed = setOf(1 to 1, 1 to 2, 1 to 3)
        val lookup: (Int, Int) -> Boolean = { b, p -> (b to p) in completed }
        assertTrue(isStageUnlocked(biome, biome.stages[1], lookup))
    }

    @Test
    fun twoOfFourDoesNotUnlockNextStage() {
        val biome = makeBiome(
            listOf(
                makeStage(1, "Shores", 4, listOf(1, 2, 3, 4)),
                makeStage(2, "Shallows", 4, emptyList())
            )
        )
        val completed = setOf(1 to 1, 1 to 2)
        val lookup: (Int, Int) -> Boolean = { b, p -> (b to p) in completed }
        assertFalse(isStageUnlocked(biome, biome.stages[1], lookup))
    }

    @Test
    fun sixOfEightUnlocksNextStage() {
        val biome = makeBiome(
            listOf(
                makeStage(1, "First", 8, listOf(1, 2, 3, 4, 5, 6, 7, 8)),
                makeStage(2, "Second", 4, emptyList())
            )
        )
        val completed = (1..6).map { 1 to it }.toSet()
        val lookup: (Int, Int) -> Boolean = { b, p -> (b to p) in completed }
        assertTrue(isStageUnlocked(biome, biome.stages[1], lookup))
    }

    @Test
    fun fiveOfEightDoesNotUnlockNextStage() {
        val biome = makeBiome(
            listOf(
                makeStage(1, "First", 8, listOf(1, 2, 3, 4, 5, 6, 7, 8)),
                makeStage(2, "Second", 4, emptyList())
            )
        )
        val completed = (1..5).map { 1 to it }.toSet()
        val lookup: (Int, Int) -> Boolean = { b, p -> (b to p) in completed }
        assertFalse(isStageUnlocked(biome, biome.stages[1], lookup))
    }

    @Test
    fun stageWithEmptyPuzzlesStillRequiresPreviousStageProgress() {
        val biome = makeBiome(
            listOf(
                makeStage(1, "Shores", 4, listOf(1, 2)),
                makeStage(2, "Shallows", 4, emptyList()),
                makeStage(3, "Depths", 4, emptyList())
            )
        )
        // Shallows has 0 puzzles, so it can never satisfy 3-of-4 for Depths.
        val completed = setOf(1 to 1, 1 to 2)
        val lookup: (Int, Int) -> Boolean = { b, p -> (b to p) in completed }
        assertFalse(isStageUnlocked(biome, biome.stages[2], lookup))
    }
}
