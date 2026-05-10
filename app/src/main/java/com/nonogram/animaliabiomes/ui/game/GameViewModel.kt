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

    // Emits the (row, col) of a wrong tap so the view can flash it red
    private val _strikeFlash = MutableLiveData<Pair<Int, Int>?>(null)
    val strikeFlash: LiveData<Pair<Int, Int>?> = _strikeFlash

    fun init(p: Puzzle) {
        puzzle = p
        reset()
    }

    private fun emptyGrid(): Grid =
        List(puzzle.gridSize) { List(puzzle.gridSize) { CellState.EMPTY } }

    private fun reset() {
        _grid.value = emptyGrid()
        _strikes.value = 0
        _isComplete.value = false
    }

    fun onCellTap(row: Int, col: Int) {
        val current = _grid.value ?: return
        if (current[row][col] != CellState.EMPTY) return

        if (puzzle.solution[row][col]) {
            _grid.value = current.mapIndexed { r, rowList ->
                rowList.mapIndexed { c, cell ->
                    if (r == row && c == col) CellState.FILLED else cell
                }
            }
            checkWin()
        } else {
            // Mark cell as incorrect permanently so the X stays visible
            _grid.value = current.mapIndexed { r, rowList ->
                rowList.mapIndexed { c, cell ->
                    if (r == row && c == col) CellState.INCORRECT else cell
                }
            }
            val newStrikes = (_strikes.value ?: 0) + 1
            _strikes.value = newStrikes
            _strikeFlash.value = Pair(row, col)
        }
    }

    fun onCellLongPress(row: Int, col: Int) {
        val current = _grid.value ?: return
        if (current[row][col] == CellState.FILLED || current[row][col] == CellState.INCORRECT) return
        _grid.value = current.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                if (r == row && c == col) {
                    if (cell == CellState.MARKED) CellState.EMPTY else CellState.MARKED
                } else cell
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
                if (puzzle.solution[r][c] && g[r][c] != CellState.FILLED) return
            }
        }
        _isComplete.value = true
    }
}
