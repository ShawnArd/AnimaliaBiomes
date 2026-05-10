package com.nonogram.animaliabiomes.ui.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

class ColorPaletteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var palette: List<Int> = emptyList()
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var selectedColorIndex: Int = 1
        set(value) {
            field = value
            invalidate()
        }

    var onColorSelected: ((Int) -> Unit)? = null

    private val density = resources.displayMetrics.density
    private val swatchDiameterPx = SWATCH_DIAMETER_DP * density
    private val viewHeightPx = VIEW_HEIGHT_DP * density

    private val swatchFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val selectedRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = SELECTED_RING_WIDTH_DP * density
    }
    private val unselectedRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#90A4AE")
        style = Paint.Style.STROKE
        strokeWidth = UNSELECTED_RING_WIDTH_DP * density
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val desiredHeight = viewHeightPx.toInt()
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val count = palette.size
        if (count == 0) return

        val centerY = height / 2f
        val slotWidth = width.toFloat() / count
        val maxRadius = (slotWidth - 8 * density) / 2f
        val radius = minOf(swatchDiameterPx / 2f, maxRadius).coerceAtLeast(0f)

        for (i in 0 until count) {
            val centerX = slotWidth * i + slotWidth / 2f
            swatchFillPaint.color = palette[i]
            canvas.drawCircle(centerX, centerY, radius, swatchFillPaint)

            val ringPaint = if (i + 1 == selectedColorIndex) selectedRingPaint else unselectedRingPaint
            val ringRadius = (radius - ringPaint.strokeWidth / 2f).coerceAtLeast(0f)
            canvas.drawCircle(centerX, centerY, ringRadius, ringPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) return true
        val count = palette.size
        if (count == 0) return true

        val slotWidth = width.toFloat() / count
        val index = min((event.x / slotWidth).toInt(), count - 1).coerceAtLeast(0)
        val newSelection = index + 1
        if (newSelection != selectedColorIndex) {
            selectedColorIndex = newSelection
        }
        onColorSelected?.invoke(newSelection)
        performClick()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    companion object {
        private const val SWATCH_DIAMETER_DP = 48f
        private const val VIEW_HEIGHT_DP = 64f
        private const val SELECTED_RING_WIDTH_DP = 4f
        private const val UNSELECTED_RING_WIDTH_DP = 1.5f
    }
}
