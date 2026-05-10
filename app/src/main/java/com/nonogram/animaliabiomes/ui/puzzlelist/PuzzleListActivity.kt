package com.nonogram.animaliabiomes.ui.puzzlelist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nonogram.animaliabiomes.R
import com.nonogram.animaliabiomes.data.OceanPuzzles
import com.nonogram.animaliabiomes.ui.funfact.FunFactActivity

class PuzzleListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_list)

        val biomeId = intent.getIntExtra(EXTRA_BIOME_ID, 1)
        val biome = OceanPuzzles.all.find { it.id == biomeId } ?: return

        findViewById<TextView>(R.id.tvBiomeName).text = biome.name

        val puzzleButtons = listOf(
            R.id.btnPuzzle1, R.id.btnPuzzle2, R.id.btnPuzzle3
        )

        biome.puzzles.forEachIndexed { index, puzzle ->
            if (index < puzzleButtons.size) {
                findViewById<Button>(puzzleButtons[index]).apply {
                    text = "${puzzle.gridSize}×${puzzle.gridSize}  ${puzzle.name}"
                    setOnClickListener {
                        startActivity(Intent(this@PuzzleListActivity, FunFactActivity::class.java).apply {
                            putExtra(FunFactActivity.EXTRA_PUZZLE_ID, puzzle.id)
                            putExtra(FunFactActivity.EXTRA_BIOME_ID, biomeId)
                        })
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_BIOME_ID = "extra_biome_id"
    }
}
