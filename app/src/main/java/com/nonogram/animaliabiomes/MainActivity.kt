package com.nonogram.animaliabiomes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nonogram.animaliabiomes.ui.puzzlelist.PuzzleListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnOceanBiome).setOnClickListener {
            startActivity(Intent(this, PuzzleListActivity::class.java).apply {
                putExtra(PuzzleListActivity.EXTRA_BIOME_ID, 1)
            })
        }
    }
}
