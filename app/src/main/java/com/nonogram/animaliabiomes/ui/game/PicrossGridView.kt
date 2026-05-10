package com.nonogram.animaliabiomes.ui.game

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.nonogram.animaliabiomes.data.model.CellState
import com.nonogram.animaliabiomes.data.model.Puzzle

class PicrossGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var puzzle: Puzzle? = null
        set(value) { field = value; requestLayout(); invalidate() }

    var grid: List<List<CellState>>? = null
        set(value) { field = value; invalidate() }

    var flashCell: Pair<Int, Int>? = null
        set(value) { field = value; invalidate() }

    var onCellTap: ((row: Int, col: Int) -> Unit)? = null
    var onCellLongPress: ((row: Int, col: Int) -> Unit)? = null

    // ── Paints ────────────────────────────────────────────────────────────────

    private val filledPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A237E")
        style = Paint.Style.FILL
    }
    private val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ECF0F1")
        style = Paint.Style.FILL
    }
    private val markedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CFD8DC")
        style = Paint.Style.FILL
    }
    private val flashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E53935")
        style = Paint.Style.FILL
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
        color = Color.parseColor("#1A237E")
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

        // Clue area = 25% of each dimension, minimum 72px
        clueColWidth = (w * 0.25f).coerceAtLeast(72f)
        clueRowHeight = (h * 0.25f).coerceAtLeast(72f)

        val availW = w - clueColWidth
        val availH = h - clueRowHeight
        cellSize = minOf(availW / size, availH / size)

        // Centre the grid in remaining space
        gridLeft = clueColWidth + (availW - cellSize * size) / 2f
        gridTop  = clueRowHeight + (availH - cellSize * size) / 2f

        cluePaint.textSize = (cellSize * 0.38f).coerceAtLeast(18f)
        xPaint.strokeWidth = (cellSize * 0.1f).coerceAtLeast(4f)
    }

    // ── Drawing ───────────────────────────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val p = puzzle ?: return
        val g = grid ?: return
        drawColumnClues(canvas, p)
        drawRowClues(canvas, p)
        drawCells(canvas, g, p.gridSize)
        drawGridLines(canvas, p.gridSize)
    }

    private fun drawColumnClues(canvas: Canvas, p: Puzzle) {
        val size = p.gridSize
        for (col in 0 until size) {
            val clues = p.colClues[col]
            val cellCenterX = gridLeft + col * cellSize + cellSize / 2f
            val totalLines = clues.size
            val lineH = clueRowHeight / (totalLines + 1)
            clues.forEachIndexed { i, num ->
                val y = (i + 1) * lineH + cluePaint.textSize / 3f
                canvas.drawText(num.toString(), cellCenterX, y, cluePaint)
            }
        }
    }

    private fun drawRowClues(canvas: Canvas, p: Puzzle) {
        val size = p.gridSize
        for (row in 0 until size) {
            val clues = p.rowClues[row]
            val cellCenterY = gridTop + row * cellSize + cellSize / 2f + cluePaint.textSize / 3f
            val totalCols = clues.size
            val colW = clueColWidth / (totalCols + 1)
            clues.forEachIndexed { i, num ->
                val x = (i + 1) * colW
                canvas.drawText(num.toString(), x, cellCenterY, cluePaint)
            }
        }
    }

    private fun drawCells(canvas: Canvas, g: List<List<CellState>>, size: Int) {
        val pad = cellSize * 0.04f
        for (row in 0 until size) {
            for (col in 0 until size) {
                val l = gridLeft + col * cellSize + pad
                val t = gridTop + row * cellSize + pad
                val r = l + cellSize - pad * 2
                val b = t + cellSize - pad * 2

                val isFlash = flashCell?.first == row && flashCell?.second == col
                val paint = when {
                    isFlash -> flashPaint
                    g[row][col] == CellState.FILLED -> filledPaint
                    g[row][col] == CellState.MARKED -> markedPaint
                    else -> emptyPaint
                }
                canvas.drawRect(l, t, r, b, paint)

                if (g[row][col] == CellState.MARKED) {
                    val margin = cellSize * 0.2f
                    canvas.drawLine(l + margin, t + margin, r - margin, b - margin, xPaint)
                    canvas.drawLine(r - margin, t + margin, l + margin, b - margin, xPaint)
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
            // Thicker lines every 5 cells for larger grids
            val useSectionLine = size > 5 && i % 5 == 0
            val paint = if (useSectionLine) sectionLinePaint else gridLinePaint
            canvas.drawLine(x, gridTop, x, gridTop + gridH, paint)
            canvas.drawLine(gridLeft, y, gridLeft + gridW, y, paint)
        }
    }
}
