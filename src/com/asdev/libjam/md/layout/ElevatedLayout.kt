package com.asdev.libjam.md.layout

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.ShadowDrawable
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.util.FloatPoint
import com.asdev.libjam.md.view.View
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.awt.geom.RoundRectangle2D

/**
 * Created by Asdev on 10/14/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */
open class ElevatedLayout(val child: View, val radius: Float = 15f, opacity: Float = 0.25f, shadowYOffset: Float = 1f, val roundRadius: Float = 0f): View() {

    private val shadow: ShadowDrawable = ShadowDrawable(radius, opacity, shadowYOffset)

    init {
        background = ColorDrawable(THEME.getBackgroundColor())
    }

    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)
        child.onThemeChange(prevTheme, newTheme)
    }

    override fun onMeasure(result: LayoutParams): LayoutParams {
        val lp = child.onMeasure(result)

        if(lp.minSize.w > 0f && lp.minSize.w > minSize.w) {
            minSize = FloatDim(lp.minSize.w + radius * 2f, minSize.h)
        }

        if(lp.minSize.h > 0f && lp.minSize.h > minSize.h) {
            minSize = FloatDim(minSize.w, lp.minSize.h + radius * 2f)
        }

        if(lp.maxSize.w > 0f && lp.maxSize.w > maxSize.w) {
            maxSize = FloatDim(lp.maxSize.w + radius * 2f, maxSize.h)
        }

        if(lp.maxSize.h > 0f && lp.maxSize.h > maxSize.h) {
            maxSize = FloatDim(maxSize.w, lp.maxSize.h + radius * 2f)
        }

        if(maxSize.w < minSize.w && maxSize.w > 0f)
            maxSize = FloatDim(minSize.w, maxSize.h)

        if(maxSize.h < minSize.h && maxSize.h > 0f)
            maxSize = FloatDim(maxSize.w, minSize.h)

        return super.onMeasure(result)
    }

    override fun loop() {
        super.loop()
        child.loop()
    }

    override fun onLayout(newSize: FloatDim) {
        super.onLayout(newSize)
        child.onLayout(FloatDim(newSize.w - radius * 2f, newSize.h - radius * 2f))
    }

    override fun onDraw(g: Graphics2D) {
        val clipBounds = g.clip

        if(layoutSize.w < 0f || layoutSize.h < 0f)
            return

        shadow.draw(g, radius, radius, layoutSize.w - radius * 2f, layoutSize.h - radius * 2f)

        // translate the canvas
        g.translate(radius.toDouble() + child.translationX.toDouble(), radius.toDouble() + child.translationY.toDouble())
        // intersect the child bounds clip
        g.clip(RoundRectangle2D.Float(0f - child.overClipLeft, 0f - child.overClipTop, child.layoutSize.w + child.overClipRight + child.overClipLeft, child.layoutSize.h + child.overClipBottom + child.overClipTop, roundRadius, roundRadius)) // for a rounded frame
        background?.draw(g, 0f, 0f, child.layoutSize.w, child.layoutSize.h)
        // draw the child
        child.onDraw(g)
        g.translate(-radius.toDouble() - child.translationX.toDouble(), -radius.toDouble() - child.translationY.toDouble())

        // reset the clip
        g.clip = clipBounds
    }

    override fun onPostDraw(g: Graphics2D) {
        if(layoutSize.w < 0f || layoutSize.h < 0f)
            return

        // translate the canvas
        g.translate(radius.toDouble() + child.translationX.toDouble(), radius.toDouble() + child.translationY.toDouble())
        // draw the child
        child.onPostDraw(g)
        g.translate(-radius.toDouble() - child.translationX.toDouble(), -radius.toDouble() - child.translationY.toDouble())

        // reset the clip
        super.onPostDraw(g)
    }

    private var wasOnCompBefore = false

    override fun onMouseDragged(e: MouseEvent, mPos: Point) {
        super.onMouseDragged(e, mPos)
        if(mPos.x >= radius && mPos.x <= layoutSize.w - radius &&
                mPos.y >= radius && mPos.y <= layoutSize.h - radius) {
            if(!wasOnCompBefore) {
                child.onMouseEnter(e, Point(mPos.x - radius.toInt(), mPos.y - radius.toInt()))
                wasOnCompBefore = true
            }
            child.onMouseDragged(e, Point(mPos.x - radius.toInt(), mPos.y - radius.toInt()))
        } else {
            if(wasOnCompBefore) {
                child.onMouseExit(e, Point(mPos.x - radius.toInt(), mPos.y - radius.toInt()))
                wasOnCompBefore = false
            }
        }
    }

    override fun onMouseMoved(e: MouseEvent, mPos: Point) {
        super.onMouseMoved(e, mPos)
        if(mPos.x >= radius && mPos.x <= layoutSize.w - radius &&
                mPos.y >= radius && mPos.y <= layoutSize.h - radius) {
            if(!wasOnCompBefore) {
                child.onMouseEnter(e, Point(mPos.x - radius.toInt(), mPos.y - radius.toInt()))
                wasOnCompBefore = true
            }
            child.onMouseMoved(e, Point(mPos.x - radius.toInt(), mPos.y - radius.toInt()))
        } else {
            if(wasOnCompBefore) {
                child.onMouseExit(e, Point(mPos.x - radius.toInt(), mPos.y - radius.toInt()))
                wasOnCompBefore = false
            }
        }
    }

    override fun onMousePress(e: MouseEvent, mPos: Point) {
        super.onMousePress(e, mPos)
        if(mPos.x >= radius && mPos.x <= layoutSize.w - radius &&
                mPos.y >= radius && mPos.y <= layoutSize.h - radius)
            child.onMousePress(e, Point(mPos.x - radius.toInt(), mPos.y - radius.toInt()))
    }

    override fun onMouseRelease(e: MouseEvent, mPos: Point) {
        super.onMouseRelease(e, mPos)
        if(mPos.x >= radius && mPos.x <= layoutSize.w - radius &&
                mPos.y >= radius && mPos.y <= layoutSize.h - radius)
            child.onMouseRelease(e, Point(mPos.x - radius.toInt(), mPos.y - radius.toInt()))
    }

    override fun onKeyTyped(e: KeyEvent) {
        super.onKeyTyped(e)
        child.onKeyTyped(e)
    }

    override fun onKeyPressed(e: KeyEvent) {
        super.onKeyPressed(e)
        child.onKeyPressed(e)
    }

    override fun onKeyReleased(e: KeyEvent) {
        super.onKeyReleased(e)
        child.onKeyReleased(e)
    }

    override fun onTabTraversal(): Boolean {
        super.onTabTraversal()
        // just traverse to the child
        return child.onTabTraversal()
    }

    /**
     * Scrolls the child of this layout.
     */
    override fun onScroll(e: MouseWheelEvent) {
        super.onScroll(e)
        child.onScroll(e)
    }

    fun findChildPosition() = FloatPoint(radius, radius)

    override fun findContextMenuItems(viewPos: FloatPoint) = child.findContextMenuItems(viewPos - FloatPoint(radius, radius))?: super.findContextMenuItems(viewPos)
}