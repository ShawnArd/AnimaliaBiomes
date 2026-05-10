package com.nonogram.animaliabiomes.ui.puzzlelist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nonogram.animaliabiomes.R
import com.nonogram.animaliabiomes.data.OceanPuzzles
import com.nonogram.animaliabiomes.data.ProgressManager
import com.nonogram.animaliabiomes.ui.funfact.FunFactActivity

class PuzzleListActivity : AppCompatActivity() {

    private var biomeId = 1
    private val puzzleButtonIds = listOf(R.id.btnPuzzle1, R.id.btnPuzzle2, R.id.btnPuzzle3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_list)

        biomeId = intent.getIntExtra(EXTRA_BIOME_ID, 1)
        val biome = OceanPuzzles.all.find { it.id == biomeId } ?: return

        findViewById<TextView>(R.id.tvBiomeName).text = biome.name

        biome.puzzles.forEachIndexed { index, puzzle ->
            if (index < puzzleButtonIds.size) {
                findViewById<Button>(puzzleButtonIds[index]).setOnClickListener {
                    startActivity(Intent(this, FunFactActivity::class.java).apply {
                        putExtra(FunFactActivity.EXTRA_PUZZLE_ID, puzzle.id)
                        putExtra(FunFactActivity.EXTRA_BIOME_ID, biomeId)
                    })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh checkmarks every time we return to this screen
        val biome = OceanPuzzles.all.find { it.id == biomeId } ?: return
        biome.puzzles.forEachIndexed { index, puzzle ->
            if (index < puzzleButtonIds.size) {
                val completed = ProgressManager.isCompleted(this, puzzle.id)
                val label = "${puzzle.gridSize}×${puzzle.gridSize}  ${puzzle.name}"
                findViewById<Button>(puzzleButtonIds[index]).text =
                    if (completed) "✓  $label" else label
            }
        }
    }

    companion object {
        const val EXTRA_BIOME_ID = "extra_biome_id"
    }
}
