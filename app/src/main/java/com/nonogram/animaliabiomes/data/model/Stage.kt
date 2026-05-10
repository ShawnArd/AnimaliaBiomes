package com.nonogram.animaliabiomes.data.model

data class Stage(
    val id: Int,
    val name: String,
    val targetPuzzleCount: Int,
    val puzzles: List<Puzzle>
)
