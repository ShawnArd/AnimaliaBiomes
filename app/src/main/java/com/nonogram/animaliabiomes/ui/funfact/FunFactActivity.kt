package com.nonogram.animaliabiomes.ui.funfact

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nonogram.animaliabiomes.R
import com.nonogram.animaliabiomes.data.OceanPuzzles
import com.nonogram.animaliabiomes.ui.game.GameActivity

class FunFactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fun_fact)

        val puzzleId = intent.getIntExtra(EXTRA_PUZZLE_ID, 1)
        val biomeId  = intent.getIntExtra(EXTRA_BIOME_ID, 1)
        val biome    = OceanPuzzles.all.find { it.id == biomeId } ?: return
        val puzzle   = biome.puzzles.find { it.id == puzzleId }   ?: return

        findViewById<TextView>(R.id.tvAnimalName).text = puzzle.name
        findViewById<TextView>(R.id.tvFunFact).text    = puzzle.funFact
        findViewById<TextView>(R.id.tvGridSize).text   = "${puzzle.gridSize}×${puzzle.gridSize} Nonogram"

        findViewById<Button>(R.id.btnStartPuzzle).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java).apply {
                putExtra(GameActivity.EXTRA_PUZZLE_ID, puzzleId)
                putExtra(GameActivity.EXTRA_BIOME_ID, biomeId)
            })
            finish()
        }
    }

    companion object {
        const val EXTRA_PUZZLE_ID = "extra_puzzle_id"
        const val EXTRA_BIOME_ID  = "extra_biome_id"
    }
}
