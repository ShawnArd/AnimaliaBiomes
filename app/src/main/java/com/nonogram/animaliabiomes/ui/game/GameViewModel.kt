package com.nonogram.animaliabiomes.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nonogram.animaliabiomes.data.model.CellState
import com.nonogram.animaliabiomes.data.model.Puzzle

typealias Grid = List<List<CellState>>

class GameViewModel : ViewModel() {

    private lateinit var puzzle: Puzzle

    private val _grid = MutableLiveData<Grid>()
    val grid: LiveData<Grid> = _grid

    private val _strikes = MutableLiveData(0)
    val strikes: LiveData<Int> = _strikes

    private val _isComplete = MutableLiveData(false)
    val isComplete: LiveData<Boolean> = _isComplete

    private val _strikeFlash = MutableLiveData<Pair<Int, Int>?>(null)
    val strikeFlash: LiveData<Pair<Int, Int>?> = _strikeFlash

    fun init(p: Puzzle) {
        puzzle = p
        reset()
    }

    private fun emptyGrid(): Grid =
        List(puzzle.gridSize) { List(puzzle.gridSize) { CellState.Empty } }

    private fun reset() {
        _grid.value = emptyGrid()
        _strikes.value = 0
        _isComplete.value = false
    }

    fun onCellTap(row: Int, col: Int) {
        val current = _grid.value ?: return
        if (current[row][col] != CellState.Empty) return

        val solutionValue = puzzle.solution[row][col]
        if (solutionValue != 0) {
            _grid.value = current.mapIndexed { r, rowList ->
                rowList.mapIndexed { c, cell ->
                    if (r == row && c == col) CellState.Filled(solutionValue) else cell
                }
            }
            checkWin()
        } else {
            _grid.value = current.mapIndexed { r, rowList ->
                rowList.mapIndexed { c, cell ->
                    if (r == row && c == col) CellState.Incorrect else cell
                }
            }
            val newStrikes = (_strikes.value ?: 0) + 1
            _strikes.value = newStrikes
            _strikeFlash.value = Pair(row, col)
        }
    }

    fun onCellLongPress(row: Int, col: Int) {
        val current = _grid.value ?: return
        val cell = current[row][col]
        if (cell is CellState.Filled || cell == CellState.Incorrect) return
        _grid.value = current.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, existing ->
                if (r == row && c == col) {
                    if (existing == CellState.Marked) CellState.Empty else CellState.Marked
                } else existing
            }
        }
    }

    fun clearStrikeFlash() {
        _strikeFlash.value = null
    }

    fun resetAfterThreeStrikes() {
        reset()
    }

    private fun checkWin() {
        val g = _grid.value ?: return
        val size = puzzle.gridSize
        for (r in 0 until size) {
            for (c in 0 until size) {
                val expected = puzzle.solution[r][c]
                val cell = g[r][c]
                if (expected == 0) {
                    if (cell is CellState.Filled) return
                } else {
                    if (cell !is CellState.Filled || cell.colorIndex != expected) return
                }
            }
        }
        _isComplete.value = true
    }
}
