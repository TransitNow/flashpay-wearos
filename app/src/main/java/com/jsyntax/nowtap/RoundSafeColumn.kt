package com.jsyntax.nowtap

import android.content.Context
import android.view.View
import android.view.ViewGroup
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Stacks children in a vertically centred column, clamping each to the widest span that still
 * fits inside a round display.
 *
 * Wear review rejects any screen where text or a control is cut by the screen edge, and the safe
 * width is not one number: it narrows the further a row sits from the middle. Each row is
 * therefore measured against the chord at its own most extreme edge, so the column tapers towards
 * the top and bottom of the circle instead of being clipped by it. On a square watch the clamp is
 * skipped and rows get the full width.
 *
 * Rows are expected to be single-line, so a row's height does not change between the two measure
 * passes.
 */
class RoundSafeColumn(context: Context) : ViewGroup(context) {

    /** Vertical space between rows, in pixels. */
    var gapPx: Int = 0

    private val isRound = context.resources.configuration.isScreenRound

    /** Keeps glyphs off the glass even where the chord arithmetic says they would just fit. */
    private val bezelMarginPx = (BEZEL_MARGIN_DP * resources.displayMetrics.density).roundToInt()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        // Natural heights first: a row's safe width depends on where it lands, and that depends on
        // how tall its neighbours turned out to be.
        forEachVisibleChild { child ->
            child.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED),
            )
        }

        var top = (height - stackedHeight()) / 2
        forEachVisibleChild { child ->
            val safeWidth = safeWidthFor(top, top + child.measuredHeight, width, height)
            child.measure(
                MeasureSpec.makeMeasureSpec(safeWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(child.measuredHeight, MeasureSpec.EXACTLY),
            )
            top += child.measuredHeight + gapPx
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        var top = (b - t - stackedHeight()) / 2
        forEachVisibleChild { child ->
            val left = (width - child.measuredWidth) / 2
            child.layout(left, top, left + child.measuredWidth, top + child.measuredHeight)
            top += child.measuredHeight + gapPx
        }
    }

    /**
     * Half-chord of the display circle, doubled, at whichever of the row's edges sits furthest
     * from the centre line — that edge is the one that would clip first.
     */
    private fun safeWidthFor(top: Int, bottom: Int, width: Int, height: Int): Int {
        if (!isRound) return width
        val radius = min(width, height) / 2f - bezelMarginPx
        val centreY = height / 2f
        val dy = maxOf(abs(top - centreY), abs(bottom - centreY))
        if (dy >= radius) return 0
        return (2f * sqrt(radius * radius - dy * dy)).roundToInt().coerceAtMost(width)
    }

    private fun stackedHeight(): Int {
        var total = 0
        var count = 0
        forEachVisibleChild { total += it.measuredHeight; count++ }
        return if (count == 0) 0 else total + gapPx * (count - 1)
    }

    private inline fun forEachVisibleChild(action: (View) -> Unit) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != GONE) action(child)
        }
    }

    private companion object {
        const val BEZEL_MARGIN_DP = 6f
    }
}
