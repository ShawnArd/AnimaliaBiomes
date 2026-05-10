package com.nonogram.animaliabiomes.data.model

data class Biome(
    val id: Int,
    val name: String,
    val stages: List<Stage>
) {
    val puzzles: List<Puzzle> get() = stages.flatMap { it.puzzles }
}
