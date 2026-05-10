package com.nonogram.animaliabiomes.ui.puzzlelist

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.nonogram.animaliabiomes.R
import com.nonogram.animaliabiomes.data.model.Puzzle
import com.nonogram.animaliabiomes.data.repository.Repositories
import com.nonogram.animaliabiomes.ui.funfact.FunFactActivity
import kotlinx.coroutines.launch

class PuzzleListActivity : AppCompatActivity() {

    private var biomeId = 1
    private var stageId = 1
    private val puzzleButtons = mutableListOf<Pair<Puzzle, MaterialButton>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_list)

        biomeId = intent.getIntExtra(EXTRA_BIOME_ID, 1)
        stageId = intent.getIntExtra(EXTRA_STAGE_ID, 1)

        lifecycleScope.launch {
            val stage = Repositories.puzzles(this@PuzzleListActivity).getStage(biomeId, stageId)
            if (stage == null) {
                Toast.makeText(this@PuzzleListActivity, getString(R.string.stage_unavailable), Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }

            findViewById<TextView>(R.id.tvBiomeName).text = stage.name

            val container = findViewById<LinearLayout>(R.id.puzzleButtonsContainer)
            container.removeAllViews()
            puzzleButtons.clear()

            val tints = listOf(R.color.ocean_deep, R.color.ocean_mid, R.color.ocean_light)

            stage.puzzles.forEachIndexed { index, puzzle ->
                val button = MaterialButton(this@PuzzleListActivity).apply {
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(56)
                    ).apply { bottomMargin = dp(12) }
                    layoutParams = params
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                    gravity = Gravity.CENTER
                    backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@PuzzleListActivity, tints[index % tints.size])
                    )
                    setTextColor(ContextCompat.getColor(this@PuzzleListActivity, R.color.white))
                    setOnClickListener {
                        startActivity(Intent(this@PuzzleListActivity, FunFactActivity::class.java).apply {
                            putExtra(FunFactActivity.EXTRA_PUZZLE_ID, puzzle.id)
                            putExtra(FunFactActivity.EXTRA_BIOME_ID, biomeId)
                        })
                    }
                }
                container.addView(button)
                puzzleButtons.add(puzzle to button)
            }

            applyCompletionLabels()
        }
    }

    override fun onResume() {
        super.onResume()
        applyCompletionLabels()
    }

    private fun applyCompletionLabels() {
        if (puzzleButtons.isEmpty()) return
        val progress = Repositories.progress(this)
        puzzleButtons.forEach { (puzzle, button) ->
            val completed = progress.isCompleted(biomeId, puzzle.id)
            val label = "${puzzle.gridSize}×${puzzle.gridSize}  ${puzzle.name}"
            button.text = if (completed) "✓  $label" else label
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    companion object {
        const val EXTRA_BIOME_ID = "extra_biome_id"
        const val EXTRA_STAGE_ID = "extra_stage_id"
    }
}
