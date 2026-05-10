package com.nonogram.animaliabiomes.data.model

sealed class CellState {
    object Empty : CellState()
    object Marked : CellState()
    object Incorrect : CellState()
    data class Filled(val colorIndex: Int) : CellState()
}
