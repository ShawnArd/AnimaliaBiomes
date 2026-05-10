package com.nonogram.animaliabiomes.data

import android.graphics.Color
import com.nonogram.animaliabiomes.data.model.Biome
import com.nonogram.animaliabiomes.data.model.Puzzle

object OceanPuzzles {

    // ── 5×5 ── Fish ────────────────────────────────────────────────────────────
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
        funFact = "Most fish don't have eyelids — they sleep with their eyes wide open!",
        cellColor = Color.parseColor("#F57C00")  // warm orange
    )

    // ── 5×5 ── Crab ────────────────────────────────────────────────────────────
    //
    //  X . X . X   [1,1,1]   ← claws
    //  X X X X X   [5]       ← body top
    //  . X X X .   [3]       ← body
    //  X X X X X   [5]       ← body bottom
    //  . X . X .   [1,1]     ← legs
    //
    //  Col clues: [2,1] [4] [4] [4] [2,1]

    private val crab = Puzzle(
        id = 2,
        name = "Crab",
        gridSize = 5,
        solution = listOf(
            listOf(true,  false, true,  false, true),
            listOf(true,  true,  true,  true,  true),
            listOf(false, true,  true,  true,  false),
            listOf(true,  true,  true,  true,  true),
            listOf(false, true,  false, true,  false)
        ),
        rowClues = listOf(
            listOf(1, 1, 1),
            listOf(5),
            listOf(3),
            listOf(5),
            listOf(1, 1)
        ),
        colClues = listOf(
            listOf(2, 1),
            listOf(4),
            listOf(4),
            listOf(4),
            listOf(2, 1)
        ),
        funFact = "Crabs can walk in all four directions, but they prefer to walk sideways.",
        cellColor = Color.parseColor("#C62828")  // deep red
    )

    // ── 5×5 ── Starfish ─────────────────────────────────────────────────────────
    // Placeholder — solution to be designed

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
        funFact = "Starfish have no brain or blood — they pump seawater through their bodies instead!",
        cellColor = Color.parseColor("#E65100")  // burnt orange
    )

    // ── 10×10 placeholders ──────────────────────────────────────────────────────

    private val octopus = Puzzle(
        id = 4, name = "Octopus", gridSize = 10,
        solution = List(10) { List(10) { false } },
        rowClues = List(10) { listOf(0) },
        colClues = List(10) { listOf(0) },
        funFact = "Octopuses have three hearts and blue blood!",
        cellColor = Color.parseColor("#6A1B9A")  // deep purple
    )

    private val seaTurtle = Puzzle(
        id = 5, name = "Sea Turtle", gridSize = 10,
        solution = List(10) { List(10) { false } },
        rowClues = List(10) { listOf(0) },
        colClues = List(10) { listOf(0) },
        funFact = "Sea turtles can hold their breath for up to seven hours while sleeping underwater.",
        cellColor = Color.parseColor("#2E7D32")  // forest green
    )

    private val dolphin = Puzzle(
        id = 6, name = "Dolphin", gridSize = 10,
        solution = List(10) { List(10) { false } },
        rowClues = List(10) { listOf(0) },
        colClues = List(10) { listOf(0) },
        funFact = "Dolphins sleep with one eye open, resting only half their brain at a time.",
        cellColor = Color.parseColor("#546E7A")  // slate blue-grey
    )

    // ── 15×15 placeholders ──────────────────────────────────────────────────────

    private val shark = Puzzle(
        id = 7, name = "Shark", gridSize = 15,
        solution = List(15) { List(15) { false } },
        rowClues = List(15) { listOf(0) },
        colClues = List(15) { listOf(0) },
        funFact = "Sharks are older than trees — they've existed for over 450 million years.",
        cellColor = Color.parseColor("#37474F")  // dark blue-grey
    )

    private val jellyfish = Puzzle(
        id = 8, name = "Jellyfish", gridSize = 15,
        solution = List(15) { List(15) { false } },
        rowClues = List(15) { listOf(0) },
        colClues = List(15) { listOf(0) },
        funFact = "Jellyfish are 95% water and have no brain, heart, or bones.",
        cellColor = Color.parseColor("#AD1457")  // deep pink
    )

    private val seahorse = Puzzle(
        id = 9, name = "Seahorse", gridSize = 15,
        solution = List(15) { List(15) { false } },
        rowClues = List(15) { listOf(0) },
        colClues = List(15) { listOf(0) },
        funFact = "Seahorses are the only species where the male carries and gives birth to the young.",
        cellColor = Color.parseColor("#F9A825")  // golden yellow
    )

    // ── 20×20 placeholders ──────────────────────────────────────────────────────

    private val blueWhale = Puzzle(
        id = 10, name = "Blue Whale", gridSize = 20,
        solution = List(20) { List(20) { false } },
        rowClues = List(20) { listOf(0) },
        colClues = List(20) { listOf(0) },
        funFact = "The blue whale is the largest animal ever known to have existed on Earth.",
        cellColor = Color.parseColor("#1565C0")  // ocean blue
    )

    private val mantaRay = Puzzle(
        id = 11, name = "Manta Ray", gridSize = 20,
        solution = List(20) { List(20) { false } },
        rowClues = List(20) { listOf(0) },
        colClues = List(20) { listOf(0) },
        funFact = "Manta rays have the largest brain-to-body ratio of any cold-blooded fish.",
        cellColor = Color.parseColor("#263238")  // near black-blue
    )

    private val lobster = Puzzle(
        id = 12, name = "Lobster", gridSize = 20,
        solution = List(20) { List(20) { false } },
        rowClues = List(20) { listOf(0) },
        colClues = List(20) { listOf(0) },
        funFact = "Lobsters don't age the way most animals do — older ones are often more fertile than younger ones.",
        cellColor = Color.parseColor("#BF360C")  // deep burnt red
    )

    private val anglerfish = Puzzle(
        id = 13, name = "Anglerfish", gridSize = 20,
        solution = List(20) { List(20) { false } },
        rowClues = List(20) { listOf(0) },
        colClues = List(20) { listOf(0) },
        funFact = "The anglerfish's glowing lure is produced by bioluminescent bacteria living inside it.",
        cellColor = Color.parseColor("#1B5E20")  // deep dark green
    )

    private val clownfish = Puzzle(
        id = 14, name = "Clownfish", gridSize = 20,
        solution = List(20) { List(20) { false } },
        rowClues = List(20) { listOf(0) },
        colClues = List(20) { listOf(0) },
        funFact = "All clownfish are born male. The dominant fish in a group can change to female.",
        cellColor = Color.parseColor("#E64A19")  // vivid orange
    )

    private val narwhal = Puzzle(
        id = 15, name = "Narwhal", gridSize = 20,
        solution = List(20) { List(20) { false } },
        rowClues = List(20) { listOf(0) },
        colClues = List(20) { listOf(0) },
        funFact = "The narwhal's spiral tusk is actually a giant tooth that can grow up to 10 feet long.",
        cellColor = Color.parseColor("#4A148C")  // deep purple
    )

    val oceanBiome = Biome(
        id = 1,
        name = "Ocean",
        puzzles = listOf(
            fish, crab, starfish,
            octopus, seaTurtle, dolphin,
            shark, jellyfish, seahorse,
            blueWhale, mantaRay, lobster, anglerfish, clownfish, narwhal
        )
    )

    val all = listOf(oceanBiome)
}
