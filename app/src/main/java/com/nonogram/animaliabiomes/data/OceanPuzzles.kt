package com.nonogram.animaliabiomes.data

import com.nonogram.animaliabiomes.data.model.Biome
import com.nonogram.animaliabiomes.data.model.Puzzle

object OceanPuzzles {

    // ── 5×5 ────────────────────────────────────────────────────────────────────
    //
    //  . X X X .   [3]
    //  X . . . X   [1,1]
    //  X X X X X   [5]
    //  X . . . X   [1,1]
    //  . X X X .   [3]
    //
    //  Col clues: [3] [1,1,1] [1,1,1] [1,1,1] [3]

    private val fish = Puzzle(
        id = 1,
        name = "Fish",
        gridSize = 5,
        solution = listOf(
            listOf(false, true,  true,  true,  false),
            listOf(true,  false, false, false, true),
            listOf(true,  true,  true,  true,  true),
            listOf(true,  false, false, false, true),
            listOf(false, true,  true,  true,  false)
        ),
        rowClues = listOf(
            listOf(3),
            listOf(1, 1),
            listOf(5),
            listOf(1, 1),
            listOf(3)
        ),
        colClues = listOf(
            listOf(3),
            listOf(1, 1, 1),
            listOf(1, 1, 1),
            listOf(1, 1, 1),
            listOf(3)
        ),
        funFact = "Most fish don't have eyelids — they sleep with their eyes wide open!"
    )

    // Placeholder puzzles — will be replaced as we design each animal
    private val crab = Puzzle(
        id = 2,
        name = "Crab",
        gridSize = 5,
        solution = listOf(
            listOf(false, false, false, false, false),
            listOf(false, false, false, false, false),
            listOf(false, false, false, false, false),
            listOf(false, false, false, false, false),
            listOf(false, false, false, false, false)
        ),
        rowClues = listOf(listOf(0), listOf(0), listOf(0), listOf(0), listOf(0)),
        colClues = listOf(listOf(0), listOf(0), listOf(0), listOf(0), listOf(0)),
        funFact = "Crabs can walk in all four directions, but they prefer to walk sideways."
    )

    private val starfish = Puzzle(
        id = 3,
        name = "Starfish",
        gridSize = 5,
        solution = listOf(
            listOf(false, false, false, false, false),
            listOf(false, false, false, false, false),
            listOf(false, false, false, false, false),
            listOf(false, false, false, false, false),
            listOf(false, false, false, false, false)
        ),
        rowClues = listOf(listOf(0), listOf(0), listOf(0), listOf(0), listOf(0)),
        colClues = listOf(listOf(0), listOf(0), listOf(0), listOf(0), listOf(0)),
        funFact = "Starfish have no brain or blood — they pump seawater through their bodies instead!"
    )

    val oceanBiome = Biome(
        id = 1,
        name = "Ocean",
        puzzles = listOf(fish, crab, starfish)
    )

    val all = listOf(oceanBiome)
}
