package com.asdev.libjam.md.layout

import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.VISIBILITY_VISIBLE
import com.asdev.libjam.md.view.View
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

/**
 * Created by Asdev on 11/16/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

/**
 * A layout which allows for scrolling if the minimum size of the child does not fit the available size.
 */
class ScrollLayout(val child: View) : ViewGroup() {

    /**
     * The cached parameters of the child view.
     */
    private lateinit var childParams: LinearLayoutParams

    /**
     * The x location of the child.
     */
    private var childX = 0f

    /**
     * The y location of the child.
     */
    private var childY = 0f

    /**
     * The computed width of the child.
     */
    private var childW = 0f

    /**
     * The computed height of the child.
     */
    private var childH = 0f

    /**
     * The amount that is currently scrolled horizontally.
     */
    private var scrollX = 0f

    /**
     * The amount that is currently scrolled vertically.
     */
    private var scrollY = 0f

    /**
     * The amount that can be scrolled horizontally.
     */
    private var maxScrollX = 0f

    /**
     * The amount that can be scrolled vertically.
     */
    private var maxScrollY = 0f

    /**
     * Throws an exception because you cannot add a view to a ScrollLayout.
     */
    override fun addChild(child: View) {
        throw IllegalStateException("You cannot add a view to a ScrollLayout!")
    }

    /**
     * Throws an exception because you cannot remove the child of a ScrollLayout.
     */
    override fun removeChild(child: View) {
        throw IllegalStateException("You cannot remove the child of a ScrollLayout!")
    }

    /**
     * Returns the single child attached to this layout.
     */
    override fun getChildren() = arrayOf(child)

    override fun onMouseEnter(e: MouseEvent, mPos: Point) {
        super.onMouseEnter(e, mPos)
    }

    override fun onMouseExit(e: MouseEvent, mPos: Point) {
        super.onMouseExit(e, mPos)
    }

    override fun onMouseRelease(e: MouseEvent, mPos: Point) {
        super.onMouseRelease(e, mPos)
    }

    override fun onMousePress(e: MouseEvent, mPos: Point) {
        super.onMousePress(e, mPos)
    }

    override fun onTabTraversal(): Boolean {
        return super.onTabTraversal()
    }

    override fun onMouseDragged(e: MouseEvent, mPos: Point) {
        super.onMouseDragged(e, mPos)
    }

    override fun onMouseMoved(e: MouseEvent, mPos: Point) {
        super.onMouseMoved(e, mPos)
    }

    override fun onKeyPressed(e: KeyEvent) {
        super.onKeyPressed(e)
    }

    override fun onKeyTyped(e: KeyEvent) {
        super.onKeyTyped(e)
    }

    override fun onKeyReleased(e: KeyEvent) {
        super.onKeyReleased(e)
    }

    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)
    }

    override fun onMeasure(result: LayoutParams): LayoutParams {
        // use linear layout params because it contains gravity, the only thing we need
        childParams = child.onMeasure(newLinearLayoutParams()) as LinearLayoutParams

        // the max and min size of this layout may be whatever, they are completely independent of the child
        // meaning that we don't have to do anything else here

        return super.onMeasure(result)
    }

    override fun onLayout(newSize: FloatDim) {
        super.onLayout(newSize)

        // check to see if this size fits the min size
        // also check the max size. Apply the gravity if this size is over the max size of the child

        // reset vars
        childW = newSize.w
        childX = 0f
        scrollX = 0f
        maxScrollX = 0f

        // check if min width is larger
        if(childParams.minSize.w > 0f && childParams.minSize.w > newSize.w) {
            // child wants to be bigger than this, so apply it to the max scroll x
            maxScrollX = childParams.minSize.w - newSize.w
            // the min size of the child is the new width of it
            childW = childParams.minSize.w
        }

        // check if max width is smaller
        if(childParams.maxSize.w > 0f && childParams.maxSize.w < newSize.w) {
            // the new size of the child is it's max size
            childW = childParams.maxSize.w
            // shift over the child x according to the gravity
            childX = calculateXComp(childParams.gravity, 0f, newSize.w, childW)
        }

        // reset vars
        childH = newSize.h
        childY = 0f
        scrollY = 0f
        maxScrollY = 0f

        // check if min width is larger
        if(childParams.minSize.h > 0f && childParams.minSize.h > newSize.h) {
            // child wants to be bigger than this, so apply it to the max scroll y
            maxScrollY = childParams.minSize.h - newSize.h
            // the min size of the child is the new height of it
            childH = childParams.minSize.h
        }

        // check if max width is smaller
        if(childParams.maxSize.h > 0f && childParams.maxSize.h < newSize.h) {
            // the new size of the child is it's max size
            childH = childParams.maxSize.h
            // shift over the child y according to the gravity
            childY = calculateYComp(childParams.gravity, 0f, newSize.h, childH)
        }

        // layout child according to the child W and H
        child.onLayout(FloatDim(childW, childH))
    }

    /**
     * Calculates the x-component of the corresponding gravity.
     * @param bX bounding box X
     * @param bW bounding box W
     * @param oW object W
     */
    private fun calculateXComp(gravity: Int, bX: Float, bW: Float, oW: Float): Float {
        if(gravity == GRAVITY_TOP_LEFT ||
                gravity == GRAVITY_MIDDLE_LEFT ||
                gravity == GRAVITY_BOTTOM_LEFT) {
            // just return bounding box x
            return bX
        } else if(gravity == GRAVITY_TOP_RIGHT ||
                gravity == GRAVITY_MIDDLE_RIGHT ||
                gravity == GRAVITY_BOTTOM_RIGHT) {
            // align to the right
            return bX + bW - oW
        } else {
            // gravity has to be a center
            return bX + bW / 2f - oW / 2f
        }
    }

    /**
     * Calculates the y-component of the corresponding gravity.
     * @param bY bounding box Y
     * @param bH bounding box H
     * @param oH object H
     */
    private fun calculateYComp(gravity: Int, bY: Float, bH: Float, oH: Float): Float {
        if(gravity == GRAVITY_TOP_LEFT ||
                gravity == GRAVITY_TOP_MIDDLE ||
                gravity == GRAVITY_TOP_RIGHT) {
            // just return 0f
            return bY
        } else if(gravity == GRAVITY_MIDDLE_LEFT ||
                gravity == GRAVITY_MIDDLE_MIDDLE ||
                gravity == GRAVITY_MIDDLE_RIGHT) {
            // align to the middle
            return bY + bH / 2f - oH / 2f
        } else {
            // align to bottom
            return bY + bH - oH
        }
    }

    /**
     * Scrolls this layout by the given amount.
     */
    fun scroll(xAmt: Float, yAmt: Float) {
        scrollX += xAmt
        scrollY += yAmt

        // make sure scroll doesn't exceed the maximums
        if(scrollX > maxScrollX)
            scrollX = maxScrollX
        if(scrollY > maxScrollY)
            scrollY = maxScrollY

        // make sure there is no negative scrolling
        if(scrollX < 0f)
            scrollX = 0f
        if(scrollY < 0f)
            scrollY = 0f
    }

    /**
     * Draws the child of this ScrollLayout according to the user scroll.
     */
    override fun onDraw(g: Graphics2D) {
        if(visibility != VISIBILITY_VISIBLE)
            return

        super.onDraw(g)

        // draw the child based on it's x and y plus translation and scroll
        val prevClip = g.clip

        // translate the canvas to dest spot
        g.translate(childX.toInt() + child.translationX.toInt(), childY.toInt() + child.translationY.toInt())
        // clip canvas with overclip
        g.clipRect(0 - child.overClipLeft.toInt(), 0 - child.overClipTop.toInt(), childW.toInt() + child.overClipLeft.toInt() + child.overClipRight.toInt(), childH.toInt() + child.overClipTop.toInt() + child.overClipBottom.toInt())
        // translate again to apply scroll. Scroll is negative because we want to reverse the direction of the actual
        // scrolling gesture
        g.translate(-scrollX.toInt(), -scrollY.toInt())
        // finally draw child
        child.onDraw(g)
        // undo translations
        g.translate(scrollX.toInt(), scrollY.toInt())
        g.translate(-childX.toInt() - child.translationX.toInt(), -childY.toInt() - child.translationY.toInt())

        // undo clip op
        g.clip = prevClip
    }

}