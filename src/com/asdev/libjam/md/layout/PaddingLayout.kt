package com.asdev.libjam.md.layout

import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.DEBUG_LAYOUT_BOXES
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.View
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.MouseEvent

/**
 * Created by Asdev on 10/21/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

/**
 * A layout that has one child and that provides a padding around he child.
 */
class PaddingLayout (val child: View, val padding: Float = 15f): View() {


    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)
        child.onThemeChange(prevTheme, newTheme)
    }

    override fun onMeasure(result: LayoutParams): LayoutParams {
        val lp = child.onMeasure(result)

        if (lp.minSize.w > 0f && lp.minSize.w > minSize.w) {
            minSize = FloatDim(lp.minSize.w + padding * 2f, minSize.h)
        }

        if (lp.minSize.h > 0f && lp.minSize.h > minSize.h) {
            minSize = FloatDim(minSize.w, lp.minSize.h + padding * 2f)
        }

        if (lp.maxSize.w > 0f && lp.maxSize.w > maxSize.w) {
            maxSize = FloatDim(lp.maxSize.w + padding * 2f, maxSize.h)
        }

        if (lp.maxSize.h > 0f && lp.maxSize.h > maxSize.h) {
            maxSize = FloatDim(maxSize.w, lp.maxSize.h + padding * 2f)
        }

        if (maxSize.w < minSize.w && maxSize.w > 0f)
            maxSize = FloatDim(minSize.w, maxSize.h)

        if (maxSize.h < minSize.h && maxSize.h > 0f)
            maxSize = FloatDim(maxSize.w, minSize.h)

        return super.onMeasure(result)
    }

    override fun loop() {
        super.loop()
        child.loop()
    }

    override fun onLayout(newSize: FloatDim) {
        super.onLayout(newSize)
        child.onLayout(FloatDim(newSize.w - padding * 2f, newSize.h - padding * 2f))
    }

    override fun onDraw(g: Graphics2D) {
        val clipBounds = g.clipBounds

        if (layoutSize.w < 0f || layoutSize.h < 0f)
            return

        // draw the background
        background?.draw(g, 0f, 0f, layoutSize.w, layoutSize.h)
        // draw a blue debug border
        if(DEBUG_LAYOUT_BOXES) {
            g.color = Color.BLUE
            g.drawRect(0, 0, layoutSize.w.toInt(), layoutSize.h.toInt())
        }
        // translate the canvas
        g.translate(padding.toDouble() + child.translationX.toDouble(), padding.toDouble() + child.translationY.toDouble())
        // intersect the child bounds clip
        // g.clipRect(0, 0, child.layoutSize.w.toInt(), child.layoutSize.h.toInt())
        // draw the child
        child.onDraw(g)
        g.translate(-padding.toDouble() - child.translationX.toDouble(), -padding.toDouble() - child.translationY.toDouble())

        // reset the clip
        g.clip = clipBounds
    }

    private var wasOnCompBefore = false

    override fun onMouseDragged(e: MouseEvent, mPos: Point) {
        super.onMouseDragged(e, mPos)
        if (mPos.x >= padding && mPos.x <= layoutSize.w - padding &&
                mPos.y >= padding && mPos.y <= layoutSize.h - padding) {
            if (!wasOnCompBefore) {
                child.onMouseEnter(e, Point(mPos.x - padding.toInt(), mPos.y - padding.toInt()))
                wasOnCompBefore = true
            }
            child.onMouseDragged(e, Point(mPos.x - padding.toInt(), mPos.y - padding.toInt()))
        } else {
            if (wasOnCompBefore) {
                child.onMouseExit(e, Point(mPos.x - padding.toInt(), mPos.y - padding.toInt()))
                wasOnCompBefore = false
            }
        }
    }

    override fun onMouseMoved(e: MouseEvent, mPos: Point) {
        super.onMouseMoved(e, mPos)
        if (mPos.x >= padding && mPos.x <= layoutSize.w - padding &&
                mPos.y >= padding && mPos.y <= layoutSize.h - padding) {
            if (!wasOnCompBefore) {
                child.onMouseEnter(e, Point(mPos.x - padding.toInt(), mPos.y - padding.toInt()))
                wasOnCompBefore = true
            }
            child.onMouseMoved(e, Point(mPos.x - padding.toInt(), mPos.y - padding.toInt()))
        } else {
            if (wasOnCompBefore) {
                child.onMouseExit(e, Point(mPos.x - padding.toInt(), mPos.y - padding.toInt()))
                wasOnCompBefore = false
            }
        }
    }

    override fun onMousePress(e: MouseEvent, mPos: Point) {
        super.onMousePress(e, mPos)
        if (mPos.x >= padding && mPos.x <= layoutSize.w - padding &&
                mPos.y >= padding && mPos.y <= layoutSize.h - padding)
            child.onMousePress(e, Point(mPos.x - padding.toInt(), mPos.y - padding.toInt()))
    }

    override fun onMouseRelease(e: MouseEvent, mPos: Point) {
        super.onMouseRelease(e, mPos)
        if (mPos.x >= padding && mPos.x <= layoutSize.w - padding &&
                mPos.y >= padding && mPos.y <= layoutSize.h - padding)
            child.onMouseRelease(e, Point(mPos.x - padding.toInt(), mPos.y - padding.toInt()))
    }
}