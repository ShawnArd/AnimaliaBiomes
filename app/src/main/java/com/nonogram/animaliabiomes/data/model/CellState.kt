package com.nonogram.animaliabiomes.data.model

enum class CellState {
    EMPTY,
    FILLED,
    MARKED,    // player's strategy X marker (long press)
    INCORRECT  // wrong cell tapped — locked with red X until reset
}
