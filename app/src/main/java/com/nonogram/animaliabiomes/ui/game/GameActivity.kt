package com.nonogram.animaliabiomes.ui.game

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nonogram.animaliabiomes.R
import com.nonogram.animaliabiomes.data.OceanPuzzles
import com.nonogram.animaliabiomes.data.ProgressManager

class GameActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()

    private lateinit var gridView: PicrossGridView
    private lateinit var strikeViews: List<ImageView>
    private lateinit var tvPuzzleName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val puzzleId = intent.getIntExtra(EXTRA_PUZZLE_ID, 1)
        val biomeId  = intent.getIntExtra(EXTRA_BIOME_ID, 1)
        val biome    = OceanPuzzles.all.find { it.id == biomeId } ?: return
        val puzzle   = biome.puzzles.find { it.id == puzzleId }   ?: return

        gridView     = findViewById(R.id.picrossGridView)
        tvPuzzleName = findViewById(R.id.tvPuzzleName)
        strikeViews  = listOf(
            findViewById(R.id.ivStrike1),
            findViewById(R.id.ivStrike2),
            findViewById(R.id.ivStrike3)
        )

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        tvPuzzleName.text = puzzle.name
        gridView.puzzle   = puzzle

        gridView.onCellTap       = viewModel::onCellTap
        gridView.onCellLongPress = viewModel::onCellLongPress

        viewModel.init(puzzle)

        viewModel.grid.observe(this) { gridView.grid = it }

        viewModel.strikes.observe(this) { count ->
            strikeViews.forEachIndexed { i, view ->
                view.alpha = if (i < count) 1f else 0.2f
            }
        }

        viewModel.strikeFlash.observe(this) { flash ->
            if (flash == null) return@observe
            viewModel.clearStrikeFlash()
            if ((viewModel.strikes.value ?: 0) >= 3) {
                // Brief pause so the player sees the 3rd X before the reset
                gridView.postDelayed({
                    viewModel.resetAfterThreeStrikes()
                    Toast.makeText(this, "3 strikes — puzzle reset!", Toast.LENGTH_SHORT).show()
                }, 900)
            }
        }

        viewModel.isComplete.observe(this) { complete ->
            if (complete) {
                ProgressManager.markCompleted(this, puzzleId)
                Toast.makeText(this, "Puzzle solved!", Toast.LENGTH_SHORT).show()
                gridView.postDelayed({ finish() }, 1500)
            }
        }
    }

    companion object {
        const val EXTRA_PUZZLE_ID = "extra_puzzle_id"
        const val EXTRA_BIOME_ID  = "extra_biome_id"
    }
}
