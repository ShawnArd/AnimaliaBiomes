package com.nonogram.animaliabiomes.ui.stageselect

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.nonogram.animaliabiomes.R
import com.nonogram.animaliabiomes.data.model.Biome
import com.nonogram.animaliabiomes.data.model.Stage
import com.nonogram.animaliabiomes.data.repository.Repositories
import com.nonogram.animaliabiomes.data.repository.requiredCompletedForUnlock
import com.nonogram.animaliabiomes.ui.puzzlelist.PuzzleListActivity
import kotlinx.coroutines.launch

class StageSelectActivity : AppCompatActivity() {

    private var biomeId = 1

    private data class RowViews(
        val container: LinearLayout,
        val name: TextView,
        val subtitle: TextView,
        val lock: ImageView
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stage_select)

        biomeId = intent.getIntExtra(EXTRA_BIOME_ID, 1)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        lifecycleScope.launch {
            val biome = Repositories.puzzles(this@StageSelectActivity).getBiome(biomeId)
            if (biome == null) {
                Toast.makeText(this@StageSelectActivity, getString(R.string.biome_unavailable), Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }

            findViewById<TextView>(R.id.tvBiomeHeader).text = biome.name
            bindRows(biome)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val biome = Repositories.puzzles(this@StageSelectActivity).getBiome(biomeId) ?: return@launch
            bindRows(biome)
        }
    }

    private fun bindRows(biome: Biome) {
        val rowDefs = listOf(
            RowViews(
                container = findViewById(R.id.row_stage1),
                name = findViewById(R.id.tv_stage_name1),
                subtitle = findViewById(R.id.tv_stage_subtitle1),
                lock = findViewById(R.id.iv_lock1)
            ),
            RowViews(
                container = findViewById(R.id.row_stage2),
                name = findViewById(R.id.tv_stage_name2),
                subtitle = findViewById(R.id.tv_stage_subtitle2),
                lock = findViewById(R.id.iv_lock2)
            ),
            RowViews(
                container = findViewById(R.id.row_stage3),
                name = findViewById(R.id.tv_stage_name3),
                subtitle = findViewById(R.id.tv_stage_subtitle3),
                lock = findViewById(R.id.iv_lock3)
            ),
            RowViews(
                container = findViewById(R.id.row_stage4),
                name = findViewById(R.id.tv_stage_name4),
                subtitle = findViewById(R.id.tv_stage_subtitle4),
                lock = findViewById(R.id.iv_lock4)
            )
        )

        val progress = Repositories.progress(this@StageSelectActivity)

        rowDefs.forEachIndexed { index, row ->
            if (index >= biome.stages.size) {
                row.container.visibility = View.GONE
                return@forEachIndexed
            }
            val stage = biome.stages[index]
            row.container.visibility = View.VISIBLE
            row.name.text = stage.name

            val (completed, target) = progress.stageCompletion(biome.id, stage)
            row.subtitle.text = getString(R.string.stage_subtitle, completed, target)

            val unlocked = progress.isStageUnlocked(biome, stage)
            applyLockState(row, biome, stage, unlocked)
        }
    }

    private fun applyLockState(row: RowViews, biome: Biome, stage: Stage, unlocked: Boolean) {
        if (unlocked) {
            row.container.background = ContextCompat.getDrawable(this, R.drawable.bg_stage_row_unlocked)
            row.lock.visibility = View.GONE
            row.name.alpha = 1.0f
            row.subtitle.alpha = 1.0f
            TooltipCompat.setTooltipText(row.container, null)
            row.container.setOnLongClickListener(null)
            row.container.setOnClickListener {
                startActivity(Intent(this, PuzzleListActivity::class.java).apply {
                    putExtra(PuzzleListActivity.EXTRA_BIOME_ID, biome.id)
                    putExtra(PuzzleListActivity.EXTRA_STAGE_ID, stage.id)
                })
            }
        } else {
            row.container.background = ContextCompat.getDrawable(this, R.drawable.bg_stage_row_locked)
            row.lock.visibility = View.VISIBLE
            row.name.alpha = 0.5f
            row.subtitle.alpha = 0.5f

            val previousIndex = biome.stages.indexOfFirst { it.id == stage.id } - 1
            val previous = biome.stages.getOrNull(previousIndex)
            val tooltipText = if (previous != null) {
                val progress = Repositories.progress(this)
                val (completedInPrev, _) = progress.stageCompletion(biome.id, previous)
                val needed = (requiredCompletedForUnlock(previous.targetPuzzleCount) - completedInPrev)
                    .coerceAtLeast(1)
                getString(R.string.stage_locked_tooltip, needed, previous.name)
            } else {
                getString(R.string.stage_locked_toast)
            }
            TooltipCompat.setTooltipText(row.container, tooltipText)

            row.container.setOnLongClickListener {
                Toast.makeText(this, tooltipText, Toast.LENGTH_SHORT).show()
                true
            }
            row.container.setOnClickListener {
                Toast.makeText(this, tooltipText, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val EXTRA_BIOME_ID = "extra_biome_id"
    }
}
