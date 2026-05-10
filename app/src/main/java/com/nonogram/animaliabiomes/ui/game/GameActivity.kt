package com.nonogram.animaliabiomes.ui.game

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nonogram.animaliabiomes.R
import com.nonogram.animaliabiomes.data.repository.Repositories
import kotlinx.coroutines.launch

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

        gridView     = findViewById(R.id.picrossGridView)
        tvPuzzleName = findViewById(R.id.tvPuzzleName)
        strikeViews  = listOf(
            findViewById(R.id.ivStrike1),
            findViewById(R.id.ivStrike2),
            findViewById(R.id.ivStrike3)
        )

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        lifecycleScope.launch {
            val puzzle = Repositories.puzzles(this@GameActivity).getPuzzle(biomeId, puzzleId)
            if (puzzle == null) {
                Toast.makeText(this@GameActivity, getString(R.string.puzzle_unavailable), Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }

            tvPuzzleName.text = puzzle.name
            gridView.puzzle   = puzzle

            gridView.onCellTap       = viewModel::onCellTap
            gridView.onCellLongPress = viewModel::onCellLongPress

            viewModel.init(puzzle)

            viewModel.grid.observe(this@GameActivity) { gridView.grid = it }

            viewModel.strikes.observe(this@GameActivity) { count ->
                strikeViews.forEachIndexed { i, view ->
                    view.alpha = if (i < count) 1f else 0.2f
                }
            }

            viewModel.strikeFlash.observe(this@GameActivity) { flash ->
                if (flash == null) return@observe
                viewModel.clearStrikeFlash()
                if ((viewModel.strikes.value ?: 0) >= 3) {
                    gridView.postDelayed({
                        viewModel.resetAfterThreeStrikes()
                        Toast.makeText(this@GameActivity, getString(R.string.three_strikes_reset), Toast.LENGTH_SHORT).show()
                    }, 900)
                }
            }

            viewModel.isComplete.observe(this@GameActivity) { complete ->
                if (complete) {
                    Repositories.progress(this@GameActivity).markCompleted(biomeId, puzzleId)
                    Toast.makeText(this@GameActivity, getString(R.string.puzzle_complete), Toast.LENGTH_SHORT).show()
                    gridView.postDelayed({ finish() }, 1500)
                }
            }
        }
    }

    companion object {
        const val EXTRA_PUZZLE_ID = "extra_puzzle_id"
        const val EXTRA_BIOME_ID  = "extra_biome_id"
    }
}
