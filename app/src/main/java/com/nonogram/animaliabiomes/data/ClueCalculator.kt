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
        var currentColor = 0
        for (cell in line) {
            if (cell == 0) {
                if (run > 0) {
                    clues.add(ColorClue(run, currentColor))
                    run = 0
                    currentColor = 0
                }
            } else {
                if (cell == currentColor) {
                    run++
                } else {
                    if (run > 0) {
                        clues.add(ColorClue(run, currentColor))
                    }
                    currentColor = cell
                    run = 1
                }
            }
        }
        if (run > 0) {
            clues.add(ColorClue(run, currentColor))
        }
        return if (clues.isEmpty()) listOf(ColorClue(0, 0)) else clues
    }
}
