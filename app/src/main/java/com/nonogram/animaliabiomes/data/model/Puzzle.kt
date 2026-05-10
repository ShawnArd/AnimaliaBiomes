package com.nonogram.animaliabiomes.data.model

data class Puzzle(
    val id: Int,
    val name: String,
    val gridSize: Int,
    val solution: List<List<Boolean>>,
    val rowClues: List<List<Int>>,
    val colClues: List<List<Int>>,
    val funFact: String,
    val cellColor: Int  // filled cell color — unique per animal
)
