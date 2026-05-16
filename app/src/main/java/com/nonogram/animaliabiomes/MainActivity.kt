package com.nonogram.animaliabiomes

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.nonogram.animaliabiomes.data.repository.Repositories
import com.nonogram.animaliabiomes.ui.stageselect.StageSelectActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            val biomes = Repositories.puzzles(this@MainActivity).getBiomes()
                .sortedBy { it.id }

            val container = findViewById<LinearLayout>(R.id.biomeButtonsContainer)
            container.removeAllViews()

            val tints = listOf(
                R.color.ocean_mid,
                R.color.ocean_light,
                R.color.ocean_deep,
                R.color.ocean_mid,
                R.color.ocean_light
            )

            biomes.forEachIndexed { index, biome ->
                val button = MaterialButton(this@MainActivity).apply {
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(64)
                    ).apply { bottomMargin = dp(12) }
                    layoutParams = params
                    text = biome.name
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    gravity = Gravity.CENTER
                    backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@MainActivity, tints[index % tints.size])
                    )
                    setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    setOnClickListener {
                        startActivity(Intent(this@MainActivity, StageSelectActivity::class.java).apply {
                            putExtra(StageSelectActivity.EXTRA_BIOME_ID, biome.id)
                        })
                    }
                }
                container.addView(button)
            }
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
