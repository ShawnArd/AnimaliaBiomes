package com.nonogram.animaliabiomes.data.model

data class Puzzle(
    val id: Int,
    val name: String,
    val gridSize: Int,
    val palette: List<Int>,
    val solution: List<List<Int>>,
    val funFact: String
)
