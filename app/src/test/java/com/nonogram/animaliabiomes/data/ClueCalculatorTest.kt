package com.nonogram.animaliabiomes.data

import com.nonogram.animaliabiomes.data.model.ColorClue
import org.junit.Assert.assertEquals
import org.junit.Test

class ClueCalculatorTest {

    @Test
    fun emptyRowReturnsZeroClue() {
        val solution = listOf(listOf(0, 0, 0, 0, 0))
        val clues = ClueCalculator.rowClues(solution)
        assertEquals(listOf(listOf(ColorClue(0, 0))), clues)
    }

    @Test
    fun singleColorRun() {
        val solution = listOf(listOf(0, 1, 1, 1, 0))
        val clues = ClueCalculator.rowClues(solution)
        assertEquals(listOf(listOf(ColorClue(3, 1))), clues)
    }

    @Test
    fun twoSameColorGroupsWithGap() {
        val solution = listOf(listOf(1, 1, 0, 1, 1))
        val clues = ClueCalculator.rowClues(solution)
        assertEquals(listOf(listOf(ColorClue(2, 1), ColorClue(2, 1))), clues)
    }

    @Test
    fun twoDifferentColorsNoGap() {
        val solution = listOf(listOf(1, 1, 2, 2, 0))
        val clues = ClueCalculator.rowClues(solution)
        assertEquals(listOf(listOf(ColorClue(2, 1), ColorClue(2, 2))), clues)
    }

    @Test
    fun mixedFullRow() {
        val solution = listOf(listOf(1, 2, 0, 2, 1))
        val clues = ClueCalculator.rowClues(solution)
        assertEquals(
            listOf(
                listOf(
                    ColorClue(1, 1),
                    ColorClue(1, 2),
                    ColorClue(1, 2),
                    ColorClue(1, 1)
                )
            ),
            clues
        )
    }

    @Test
    fun colCluesSimple() {
        val solution = listOf(
            listOf(1, 0),
            listOf(1, 0),
            listOf(0, 0)
        )
        val clues = ClueCalculator.colClues(solution)
        assertEquals(
            listOf(
                listOf(ColorClue(2, 1)),
                listOf(ColorClue(0, 0))
            ),
            clues
        )
    }

    @Test
    fun fullRowNoGap() {
        val solution = listOf(listOf(1, 1, 1, 1, 1))
        val clues = ClueCalculator.rowClues(solution)
        assertEquals(listOf(listOf(ColorClue(5, 1))), clues)
    }

    @Test
    fun colCluesWalkThreeColors() {
        val solution = listOf(
            listOf(1),
            listOf(1),
            listOf(2),
            listOf(3),
            listOf(3)
        )
        val clues = ClueCalculator.colClues(solution)
        assertEquals(
            listOf(
                listOf(
                    ColorClue(2, 1),
                    ColorClue(1, 2),
                    ColorClue(2, 3)
                )
            ),
            clues
        )
    }

    @Test
    fun threeDifferentColorsMixedWithGaps() {
        val solution = listOf(listOf(1, 0, 2, 2, 0, 3, 0, 1))
        val clues = ClueCalculator.rowClues(solution)
        assertEquals(
            listOf(
                listOf(
                    ColorClue(1, 1),
                    ColorClue(2, 2),
                    ColorClue(1, 3),
                    ColorClue(1, 1)
                )
            ),
            clues
        )
    }

    @Test
    fun threeDifferentColorsMixedNoGaps() {
        val solution = listOf(listOf(1, 2, 3, 1, 2))
        val clues = ClueCalculator.rowClues(solution)
        assertEquals(
            listOf(
                listOf(
                    ColorClue(1, 1),
                    ColorClue(1, 2),
                    ColorClue(1, 3),
                    ColorClue(1, 1),
                    ColorClue(1, 2)
                )
            ),
            clues
        )
    }
}
