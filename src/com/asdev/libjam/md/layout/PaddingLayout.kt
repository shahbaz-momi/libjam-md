package com.asdev.libjam.md.layout

import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.DEBUG_LAYOUT_BOXES
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.util.FloatPoint
import com.asdev.libjam.md.view.View
import com.asdev.libjam.md.xml.XMLParamList
import res.R
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

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
class PaddingLayout (val child: View, padding: Float = 15f): View() {

    var paddingLeft: Float = padding
    var paddingRight: Float = padding
    var paddingTop: Float = padding
    var paddingBottom: Float = padding

    override fun applyParameters(params: GenericParamList) {
        super.applyParameters(params)

        if(params is XMLParamList) {
            if(params.hasParam(R.attrs.PaddingLayout.padding_bottom)) {
                paddingBottom = params.getInt(R.attrs.PaddingLayout.padding_bottom)!!.toFloat()
            }

            if(params.hasParam(R.attrs.PaddingLayout.padding_top)) {
                paddingTop = params.getInt(R.attrs.PaddingLayout.padding_top)!!.toFloat()
            }

            if(params.hasParam(R.attrs.PaddingLayout.padding_left)) {
                paddingLeft = params.getInt(R.attrs.PaddingLayout.padding_left)!!.toFloat()
            }

            if(params.hasParam(R.attrs.PaddingLayout.padding_right)) {
                paddingRight = params.getInt(R.attrs.PaddingLayout.padding_right)!!.toFloat()
            }
        }
    }

    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)
        child.onThemeChange(prevTheme, newTheme)
    }

    override fun onMeasure(result: LayoutParams): LayoutParams {
        val lp = child.onMeasure(result)

        if (lp.minSize.w > 0f && lp.minSize.w > minSize.w) {
            minSize = FloatDim(lp.minSize.w + paddingLeft + paddingRight, minSize.h)
        }

        if (lp.minSize.h > 0f && lp.minSize.h > minSize.h) {
            minSize = FloatDim(minSize.w, lp.minSize.h + paddingTop + paddingBottom)
        }

        if (lp.maxSize.w > 0f && lp.maxSize.w > maxSize.w) {
            maxSize = FloatDim(lp.maxSize.w + paddingLeft + paddingRight * 2f, maxSize.h)
        }

        if (lp.maxSize.h > 0f && lp.maxSize.h > maxSize.h) {
            maxSize = FloatDim(maxSize.w, lp.maxSize.h + paddingTop + paddingBottom)
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
        child.onLayout(FloatDim(newSize.w - paddingLeft - paddingRight, newSize.h - paddingTop - paddingBottom))
    }

    override fun onDraw(g: Graphics2D) {
        val clipBounds = g.clip

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
        g.translate(paddingLeft + child.translationX.toDouble(), paddingTop + child.translationY.toDouble())
        // intersect the child bounds clip
        // g.clipRect(0, 0, child.layoutSize.w.toInt(), child.layoutSize.h.toInt())
        // draw the child
        child.onDraw(g)
        g.translate(-paddingLeft.toDouble() - child.translationX.toDouble(), -paddingTop.toDouble() - child.translationY.toDouble())

        // reset the clip
        g.clip = clipBounds
    }

    override fun onPostDraw(g: Graphics2D) {
        if (layoutSize.w < 0f || layoutSize.h < 0f)
            return

        // translate the canvas
        g.translate(paddingLeft + child.translationX.toDouble(), paddingTop + child.translationY.toDouble())
        // intersect the child bounds clip
        // g.clipRect(0, 0, child.layoutSize.w.toInt(), child.layoutSize.h.toInt())
        // draw the child
        child.onPostDraw(g)
        g.translate(-paddingLeft.toDouble() - child.translationX.toDouble(), -paddingTop.toDouble() - child.translationY.toDouble())

        super.onPostDraw(g)
    }

    private var wasOnCompBefore = false

    override fun onMouseDragged(e: MouseEvent, mPos: Point) {
        super.onMouseDragged(e, mPos)
        if (mPos.x >= paddingLeft && mPos.x <= layoutSize.w - paddingRight &&
                mPos.y >= paddingTop && mPos.y <= layoutSize.h - paddingBottom) {
            if (!wasOnCompBefore) {
                child.onMouseEnter(e, Point(mPos.x - paddingLeft.toInt(), mPos.y - paddingTop.toInt()))
                wasOnCompBefore = true
            }
            child.onMouseDragged(e, Point(mPos.x - paddingLeft.toInt(), mPos.y - paddingTop.toInt()))
        } else {
            if (wasOnCompBefore) {
                child.onMouseExit(e, Point(mPos.x - paddingLeft.toInt(), mPos.y - paddingTop.toInt()))
                wasOnCompBefore = false
            }
        }
    }

    override fun onMouseMoved(e: MouseEvent, mPos: Point) {
        super.onMouseMoved(e, mPos)
        if (mPos.x >= paddingLeft && mPos.x <= layoutSize.w - paddingRight &&
                mPos.y >= paddingTop && mPos.y <= layoutSize.h - paddingBottom) {
            if (!wasOnCompBefore) {
                child.onMouseEnter(e, Point(mPos.x - paddingLeft.toInt(), mPos.y - paddingTop.toInt()))
                wasOnCompBefore = true
            }
            child.onMouseMoved(e, Point(mPos.x - paddingLeft.toInt(), mPos.y - paddingTop.toInt()))
        } else {
            if (wasOnCompBefore) {
                child.onMouseExit(e, Point(mPos.x - paddingLeft.toInt(), mPos.y - paddingTop.toInt()))
                wasOnCompBefore = false
            }
        }
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

    override fun onMousePress(e: MouseEvent, mPos: Point) {
        super.onMousePress(e, mPos)
        if (mPos.x >= paddingLeft && mPos.x <= layoutSize.w - paddingRight &&
                mPos.y >= paddingTop && mPos.y <= layoutSize.h - paddingBottom)
            child.onMousePress(e, Point(mPos.x - paddingLeft.toInt(), mPos.y - paddingTop.toInt()))
    }

    override fun onMouseRelease(e: MouseEvent, mPos: Point) {
        super.onMouseRelease(e, mPos)
        if (mPos.x >= paddingLeft && mPos.x <= layoutSize.w - paddingRight &&
                mPos.y >= paddingTop && mPos.y <= layoutSize.h - paddingBottom)
            child.onMouseRelease(e, Point(mPos.x - paddingLeft.toInt(), mPos.y - paddingTop.toInt()))
    }

    override fun onTabTraversal(): Boolean {
        super.onTabTraversal()
        return child.onTabTraversal()
    }

    /**
     * Scrolls the child of this layout.
     */
    override fun onScroll(e: MouseWheelEvent) {
        super.onScroll(e)
        child.onScroll(e)
    }

    /**
     * Returns the position of the child within this PaddingLayout.
     */
    fun findChildPosition() = FloatPoint(paddingLeft, paddingTop)

    override fun findContextMenuItems(viewPos: FloatPoint) = child.findContextMenuItems(viewPos - findChildPosition())?: super.findContextMenuItems(viewPos)
}