package com.nonogram.animaliabiomes.ui.funfact

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nonogram.animaliabiomes.R
import com.nonogram.animaliabiomes.data.repository.Repositories
import com.nonogram.animaliabiomes.ui.game.GameActivity
import kotlinx.coroutines.launch

class FunFactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fun_fact)

        val puzzleId = intent.getIntExtra(EXTRA_PUZZLE_ID, 1)
        val biomeId  = intent.getIntExtra(EXTRA_BIOME_ID, 1)

        lifecycleScope.launch {
            val puzzle = Repositories.puzzles(this@FunFactActivity).getPuzzle(biomeId, puzzleId)
            if (puzzle == null) {
                Toast.makeText(this@FunFactActivity, getString(R.string.puzzle_unavailable), Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }

            findViewById<TextView>(R.id.tvAnimalName).text = puzzle.name
            findViewById<TextView>(R.id.tvFunFact).text    = puzzle.funFact
            findViewById<TextView>(R.id.tvGridSize).text   = "${puzzle.gridSize}×${puzzle.gridSize} Nonogram"

            findViewById<Button>(R.id.btnStartPuzzle).setOnClickListener {
                startActivity(Intent(this@FunFactActivity, GameActivity::class.java).apply {
                    putExtra(GameActivity.EXTRA_PUZZLE_ID, puzzleId)
                    putExtra(GameActivity.EXTRA_BIOME_ID, biomeId)
                })
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_PUZZLE_ID = "extra_puzzle_id"
        const val EXTRA_BIOME_ID  = "extra_biome_id"
    }
}
