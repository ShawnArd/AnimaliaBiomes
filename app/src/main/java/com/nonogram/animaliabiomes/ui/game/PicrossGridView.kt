package com.nonogram.animaliabiomes.ui.game

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.nonogram.animaliabiomes.data.ClueCalculator
import com.nonogram.animaliabiomes.data.model.CellState
import com.nonogram.animaliabiomes.data.model.ColorClue
import com.nonogram.animaliabiomes.data.model.Puzzle

class PicrossGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var puzzle: Puzzle? = null
        set(value) {
            field = value
            if (value != null) {
                rowClues = ClueCalculator.rowClues(value.solution)
                colClues = ClueCalculator.colClues(value.solution)
            } else {
                rowClues = emptyList()
                colClues = emptyList()
            }
            requestLayout()
            invalidate()
        }

    var grid: List<List<CellState>>? = null
        set(value) { field = value; invalidate() }

    var onCellTap: ((row: Int, col: Int) -> Unit)? = null
    var onCellLongPress: ((row: Int, col: Int) -> Unit)? = null

    private var rowClues: List<List<ColorClue>> = emptyList()
    private var colClues: List<List<ColorClue>> = emptyList()

    // ── Paints ────────────────────────────────────────────────────────────────

    private val filledPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val markedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CFD8DC")
        style = Paint.Style.FILL
    }
    private val incorrectXPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E53935")
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val gridLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#90A4AE")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val sectionLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#37474F")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val cluePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#212121")
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    private val xPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E53935")
        strokeWidth = 5f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    // ── Layout metrics ────────────────────────────────────────────────────────

    private var cellSize = 0f
    private var clueColWidth = 0f
    private var clueRowHeight = 0f
    private var gridLeft = 0f
    private var gridTop = 0f

    // ── Gesture detection ─────────────────────────────────────────────────────

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent) = true
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            cellAt(e.x, e.y)?.let { (r, c) -> onCellTap?.invoke(r, c) }
            return true
        }
        override fun onLongPress(e: MotionEvent) {
            cellAt(e.x, e.y)?.let { (r, c) -> onCellLongPress?.invoke(r, c) }
        }
    })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    private fun cellAt(x: Float, y: Float): Pair<Int, Int>? {
        val p = puzzle ?: return null
        val col = ((x - gridLeft) / cellSize).toInt()
        val row = ((y - gridTop) / cellSize).toInt()
        if (row < 0 || row >= p.gridSize || col < 0 || col >= p.gridSize) return null
        return Pair(row, col)
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        recalcLayout(w, h)
    }

    private fun recalcLayout(w: Int, h: Int) {
        val p = puzzle ?: return
        val size = p.gridSize

        val maxColClues = colClues.maxOfOrNull { it.size } ?: 1
        val maxRowClues = rowClues.maxOfOrNull { it.size } ?: 1

        val density     = resources.displayMetrics.density
        val rightMargin = 20 * density
        val gridBottomY = h * 0.72f

        val availW = w - rightMargin
        cellSize = minOf(
            availW / (maxRowClues + size),
            gridBottomY / (maxColClues + size)
        )

        clueColWidth  = maxRowClues * cellSize
        clueRowHeight = maxColClues * cellSize

        val gridW = cellSize * size
        val gridH = cellSize * size

        gridTop = gridBottomY - gridH

        val totalW = clueColWidth + gridW
        val hOffset = (availW - totalW) / 2f
        gridLeft = hOffset.coerceAtLeast(0f) + clueColWidth

        cluePaint.textSize          = (cellSize * 0.45f).coerceAtLeast(14f)
        xPaint.strokeWidth          = (cellSize * 0.1f).coerceAtLeast(4f)
        incorrectXPaint.strokeWidth = (cellSize * 0.12f).coerceAtLeast(5f)
    }

    // ── Drawing ───────────────────────────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val p = puzzle ?: return
        val g = grid ?: return
        drawColumnClues(canvas, p)
        drawRowClues(canvas, p)
        drawCells(canvas, g, p)
        drawGridLines(canvas, p.gridSize)
    }

    private fun drawColumnClues(canvas: Canvas, p: Puzzle) {
        val size = p.gridSize
        val lineSpacing = cluePaint.textSize * 1.4f
        for (col in 0 until size) {
            val clues = colClues[col]
            val cellCenterX = gridLeft + col * cellSize + cellSize / 2f
            clues.reversed().forEachIndexed { i, clue ->
                val y = gridTop - i * lineSpacing - lineSpacing / 2f + cluePaint.textSize / 3f
                canvas.drawText(clue.count.toString(), cellCenterX, y, cluePaint)
            }
        }
    }

    private fun drawRowClues(canvas: Canvas, p: Puzzle) {
        val size = p.gridSize
        val colSpacing = cluePaint.textSize * 1.4f
        for (row in 0 until size) {
            val clues = rowClues[row]
            val cellCenterY = gridTop + row * cellSize + cellSize / 2f + cluePaint.textSize / 3f
            clues.reversed().forEachIndexed { i, clue ->
                val x = gridLeft - i * colSpacing - colSpacing / 2f
                canvas.drawText(clue.count.toString(), x, cellCenterY, cluePaint)
            }
        }
    }

    private fun drawCells(canvas: Canvas, g: List<List<CellState>>, p: Puzzle) {
        val size = p.gridSize
        val pad = cellSize * 0.04f
        for (row in 0 until size) {
            for (col in 0 until size) {
                val l = gridLeft + col * cellSize + pad
                val t = gridTop + row * cellSize + pad
                val r = l + cellSize - pad * 2
                val b = t + cellSize - pad * 2
                val margin = cellSize * 0.2f

                when (val cell = g[row][col]) {
                    is CellState.Filled -> {
                        filledPaint.color = p.palette[cell.colorIndex - 1]
                        canvas.drawRect(l, t, r, b, filledPaint)
                    }
                    CellState.Marked -> {
                        canvas.drawRect(l, t, r, b, markedPaint)
                        canvas.drawLine(l + margin, t + margin, r - margin, b - margin, xPaint)
                        canvas.drawLine(r - margin, t + margin, l + margin, b - margin, xPaint)
                    }
                    CellState.Incorrect -> {
                        canvas.drawLine(l + margin, t + margin, r - margin, b - margin, incorrectXPaint)
                        canvas.drawLine(r - margin, t + margin, l + margin, b - margin, incorrectXPaint)
                    }
                    CellState.Empty -> {
                        // Transparent — let the ocean background show through.
                    }
                }
            }
        }
    }

    private fun drawGridLines(canvas: Canvas, size: Int) {
        val gridW = cellSize * size
        val gridH = cellSize * size

        for (i in 0..size) {
            val x = gridLeft + i * cellSize
            val y = gridTop + i * cellSize
            val useSectionLine = size > 5 && i % 5 == 0
            val paint = if (useSectionLine) sectionLinePaint else gridLinePaint
            canvas.drawLine(x, gridTop, x, gridTop + gridH, paint)
            canvas.drawLine(gridLeft, y, gridLeft + gridW, y, paint)
        }
    }

}
