package com.nonogram.animaliabiomes.data

import com.nonogram.animaliabiomes.data.model.ColorClue

object ClueCalculator {

    fun rowClues(solution: List<List<Int>>): List<List<ColorClue>> =
        solution.map { lineClues(it) }

    fun colClues(solution: List<List<Int>>): List<List<ColorClue>> {
        if (solution.isEmpty()) return emptyList()
        val width = solution[0].size
        return (0 until width).map { c ->
            lineClues(solution.map { row -> row[c] })
        }
    }

    private fun lineClues(line: List<Int>): List<ColorClue> {
        val clues = mutableListOf<ColorClue>()
        var run = 0
        var firstColor = 0
        var mixed = false
        for (cell in line) {
            if (cell == 0) {
                if (run > 0) {
                    clues.add(ColorClue(run, if (mixed) 0 else firstColor))
                    run = 0
                    firstColor = 0
                    mixed = false
                }
            } else {
                run++
                if (firstColor == 0) {
                    firstColor = cell
                } else if (cell != firstColor) {
                    mixed = true
                }
            }
        }
        if (run > 0) {
            clues.add(ColorClue(run, if (mixed) 0 else firstColor))
        }
        return if (clues.isEmpty()) listOf(ColorClue(0, 0)) else clues
    }
}
