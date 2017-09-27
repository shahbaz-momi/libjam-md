package com.asdev.libjam.md.layout

import com.asdev.libjam.md.animation.DecelerateInterpolator
import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.AlphaCompositeMutable
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.util.FloatPoint
import com.asdev.libjam.md.view.VISIBILITY_VISIBLE
import com.asdev.libjam.md.view.View
import java.awt.Cursor
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

/**
 * Created by Asdev on 11/16/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

/**
 * The amount that will be scrolled upon an arrow key press.
 */
const val KEY_SCROLL_AMT = 7f

const val SCROLLBAR_NORMAL_OPACITY = 0.7f
const val SCROLLBAR_HOVER_OPACITY = 0.825f

/**
 * A layout which allows for scrolling if the minimum size of the child does not fit the available size.
 */
class ScrollLayout() : ViewGroup() {

    private lateinit var child: View

    /**
     * A listener for scroll events produced by the ScrollLayout itself.
     * Params: ScrollLayout, scrollX (Float), scrollY (Float)
     */
    var onScrollChangeListener: ((ScrollLayout, Float, Float) -> Unit)? = null

    constructor(child: View): this() {
        this.child = child
    }

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
     * The width of the horizontal scrollbar.
     */
    private var scrollBarWidth = 0f

    /**
     * The x-coordinate of the horizontal scrollbar.
     */
    private var scrollBarX = 0f

    /**
     * Whether or not the mouse is hovering the horizontal scrollbar.
     */
    private var scrollBarHoveredH = false

    /**
     * The height of the vertical scrollbar.
     */
    private var scrollBarHeight = 0f

    /**
     * The y-coordinate of the vertical scrollbar.
     */
    private var scrollBarY = 0f

    /**
     * Whether or not the mouse is hovering the vertical scrollbar.
     */
    private var scrollBarHovered = false

    /**
     * The animator for the scroll bar opacity
     */
    private val scrollBarOpacityAnim = FloatValueAnimator(200f, DecelerateInterpolator, 0f, 0f, 0f)

    /**
     * Throws an exception because you cannot add a view to a ScrollLayout.
     */
    override fun addChild(child: View) {
        this.child = child
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
        // animate the bar in
        if(!scrollBarHovered && !scrollBarHoveredH)
            scrollBarOpacityAnim.setFromValue(0f).setToValue(SCROLLBAR_NORMAL_OPACITY).start()

        // shift the coordinates of the mouse event by the scroll
        child.onMouseEnter(e, Point(mPos.x + scrollX.toInt(), mPos.y + scrollY.toInt()))
    }

    override fun onMouseExit(e: MouseEvent, mPos: Point) {
        super.onMouseExit(e, mPos)
        if(!scrollBarHovered && !scrollBarHoveredH)
            scrollBarOpacityAnim.setFromValue(SCROLLBAR_NORMAL_OPACITY).setToValue(0f).start()

        // shift the coordinates of the mouse event by the scroll
        child.onMouseExit(e, Point(mPos.x + scrollX.toInt(), mPos.y + scrollY.toInt()))
    }

    override fun onMouseRelease(e: MouseEvent, mPos: Point) {
        if(scrollBarHovered) {
            // consume the event
            // trigger the reset of the mouse
            onMouseMoved(e, mPos)
        } else if(scrollBarHoveredH) {
            onMouseMoved(e, mPos)
        } else {
            // shift the coordinates of the mouse event by the scroll
            child.onMouseRelease(e, Point(mPos.x + scrollX.toInt(), mPos.y + scrollY.toInt()))
        }
    }

    private var pressSource = Point()
    private var scrollUnits = 0f
    private var scrollUnitsH = 0f

    override fun onMousePress(e: MouseEvent, mPos: Point) {
        if(scrollBarHovered) {
            pressSource = mPos
        } else if(scrollBarHoveredH) {
            pressSource = mPos
        } else {
            // shift the coordinates of the mouse event by the scroll
            child.onMousePress(e, Point(mPos.x + scrollX.toInt(), mPos.y + scrollY.toInt()))
        }
    }

    override fun onTabTraversal(): Boolean {
        super.onTabTraversal()
        return child.onTabTraversal()
    }

    override fun onMouseDragged(e: MouseEvent, mPos: Point) {
        super.onMouseDragged(e, mPos)
        if(scrollBarHovered) {
            // get y delta from source point
            val yDelta = mPos.y - pressSource.y
            // calculate the percentage of each pixel
            scroll(0f, yDelta.toFloat() * scrollUnits)
            pressSource = mPos
            requestRepaint()
        } else if(scrollBarHoveredH) {
            val xDelta = mPos.x - pressSource.x
            scroll(xDelta.toFloat() * scrollUnitsH, 0f)
            pressSource = mPos
            requestRepaint()
        } else {
            // shift the coordinates of the mouse event by the scroll
            child.onMouseDragged(e, Point(mPos.x + scrollX.toInt(), mPos.y + scrollY.toInt()))
        }
    }

    override fun onMouseMoved(e: MouseEvent, mPos: Point) {
        super.onMouseMoved(e, mPos)
        // shift the coordinates of the mouse event by the scroll
        child.onMouseMoved(e, Point(mPos.x + scrollX.toInt(), mPos.y + scrollY.toInt()))
        // check if moved over to right side
        if(mPos.x >= layoutSize.w - THEME.getScrollBarWidth() * 2.5f) {
            if(!scrollBarHovered) {
                setCursor(Cursor.HAND_CURSOR)
                scrollBarOpacityAnim.setFromValue(SCROLLBAR_NORMAL_OPACITY).setToValue(SCROLLBAR_HOVER_OPACITY).start()
            }
            scrollBarHovered = true
            requestRepaint()
        } else if(mPos.y >= layoutSize.h - THEME.getScrollBarWidth() * 3f) {
            if(!scrollBarHoveredH) {
                setCursor(Cursor.HAND_CURSOR)
                scrollBarOpacityAnim.setFromValue(SCROLLBAR_NORMAL_OPACITY).setToValue(SCROLLBAR_HOVER_OPACITY).start()
            }

            scrollBarHoveredH = true
            requestRepaint()
        } else {
            if(scrollBarHovered) {
                setCursor(Cursor.DEFAULT_CURSOR)
                scrollBarOpacityAnim.setFromValue(SCROLLBAR_HOVER_OPACITY).setToValue(SCROLLBAR_NORMAL_OPACITY).start()
            }

            scrollBarHovered = false

            if(scrollBarHoveredH) {
                setCursor(Cursor.DEFAULT_CURSOR)
                scrollBarOpacityAnim.setFromValue(SCROLLBAR_HOVER_OPACITY).setToValue(SCROLLBAR_NORMAL_OPACITY).start()
            }

            scrollBarHoveredH = false
        }
    }

    override fun onKeyPressed(e: KeyEvent) {
        super.onKeyPressed(e)
        if(e.keyCode == KeyEvent.VK_DOWN) {
            scroll(0f, KEY_SCROLL_AMT)
        } else if(e.keyCode == KeyEvent.VK_UP) {
            scroll(0f, -KEY_SCROLL_AMT)
        } else if(e.keyCode == KeyEvent.VK_RIGHT) {
            scroll(KEY_SCROLL_AMT, 0f)
        } else if(e.keyCode == KeyEvent.VK_LEFT) {
            scroll(-KEY_SCROLL_AMT, 0f)
        } else {
            child.onKeyPressed(e)
        }
    }

    override fun onKeyTyped(e: KeyEvent) {
        super.onKeyTyped(e)
        child.onKeyTyped(e)
    }

    override fun onKeyReleased(e: KeyEvent) {
        super.onKeyReleased(e)
        child.onKeyReleased(e)
    }

    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)
        child.onThemeChange(prevTheme, newTheme)
    }

    /**
     * Loops itself and its child.
     */
    override fun loop() {
        super.loop()
        child.loop()

        scrollBarOpacityAnim.loop()
        if(scrollBarOpacityAnim.isRunning()) {
            requestRepaint()
        }
    }

    /**
     * Scrolls the child within this layout.
     */
    override fun onScroll(e: MouseWheelEvent) {
        // scroll this view, when max scroll reached scroll the child
        if((scrollY == maxScrollY && e.unitsToScroll > 0) ||
                (scrollY <= 0f && e.unitsToScroll < 0)) {
            // scroll the child
            child.onScroll(e)
        } else {
            // haven't hit max scroll, so do a local scroll
            scroll(0f, (e.scrollAmount * e.unitsToScroll).toFloat())
            // also call a mouse on moved
        }
    }

    /**
     * Measures the child using [LinearLayoutParams] to determine the gravity and minimum size.
     */
    override fun onMeasure(result: LayoutParams): LayoutParams {
        // use linear layout params because it contains gravity, the only thing we need
        childParams = child.onMeasure(newLinearLayoutParams()) as LinearLayoutParams

        // the max and min size of this layout may be whatever, they are completely independent of the child
        // meaning that we don't have to do anything else here

        return super.onMeasure(result)
    }

    /**
     * Lays out the child within this scroll view.
     */
    override fun onLayout(newSize: FloatDim) {
        super.onLayout(newSize)

        // check to see if this size fits the min size
        // also check the max size. Apply the gravity if this size is over the max size of the child

        scrollBarHeight = 0f
        scrollBarY = 0f

        scrollBarWidth = 0f
        scrollBarX = 0f

        // reset vars
        childW = newSize.w
        childX = 0f
        // scrollX = 0f
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
        // scrollY = 0f
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

        scrollUnits = childH / newSize.h

        // scrollbar height should be difference between child h and actual h
        scrollBarHeight = newSize.h / childH

        if(scrollBarHeight >= 1f)
            scrollBarHeight = 0f
        else if(scrollBarHeight < 0.15f)
            scrollBarHeight = 0.15f
        // apply to actual units
        scrollBarHeight *= newSize.h

        scrollUnitsH = childW / newSize.w

        scrollBarWidth = newSize.w / childW
        if(scrollBarWidth >= 1f)
            scrollBarWidth = 0f
        else if(scrollBarWidth < 0.15f)
            scrollBarWidth = 0.15f
        scrollBarWidth *= newSize.w

        // layout child according to the child W and H
        child.onLayout(FloatDim(childW, childH))

        scrollTo(scrollX, scrollY)
    }

    /**
     * Returns the amount horizontally scrolled.
     */
    fun getScrollX() = scrollX
    
    /**
     * Returns the amount vertically scrolled.
     */
    fun getScrollY() = scrollY

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

        // find the new y coord of bar
        val diff = layoutSize.h - scrollBarHeight
        val perc = scrollY / maxScrollY
        scrollBarY = diff * perc

        val diffH = layoutSize.w - scrollBarWidth
        val percH = scrollX / maxScrollX
        scrollBarX = diffH * percH

        onScrollChangeListener?.invoke(this, scrollX, scrollY)

        requestRepaint()
    }

    /**
     * Scrolls to the specified point.
     */
    fun scrollTo(xScroll: Float, yScroll: Float) {
        scroll(xScroll - scrollX, yScroll - scrollY)
    }

    override fun findChildPosition(child: View): FloatPoint? {
        if(child == this.child) {
            return FloatPoint(childX, childY)
        }

        return null
    }

    private val alphaComp = AlphaCompositeMutable().apply { setAlpha(0.1f) }

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

        alphaComp.setAlpha(scrollBarOpacityAnim.getValue())

        val prevComp = g.composite
        g.composite = alphaComp.composite

        // draw scroll bar if this is focused or hovered or pressed
        if((state == State.STATE_POST_PRESS || state == State.STATE_HOVER || state == State.STATE_PRESSED || scrollBarOpacityAnim.isRunning() || scrollBarHoveredH || scrollBarHovered) && scrollBarHeight > 0f) {
            g.color = THEME.getScrollbarColor()

            g.fillRoundRect(
                    layoutSize.w.toInt() - THEME.getScrollBarWidth() + ((1f - (scrollBarOpacityAnim.getValue() - SCROLLBAR_NORMAL_OPACITY) * 5f) * THEME.getScrollBarCornerRadius()).toInt(),
                    scrollBarY.toInt(),
                    THEME.getScrollBarWidth() + THEME.getScrollBarCornerRadius(),
                    scrollBarHeight.toInt(),
                    THEME.getScrollBarCornerRadius(),
                    THEME.getScrollBarCornerRadius()
            )

        }

        if((state == State.STATE_POST_PRESS || state == State.STATE_HOVER || state == State.STATE_PRESSED || scrollBarOpacityAnim.isRunning() || scrollBarHovered || scrollBarHoveredH) && scrollBarWidth > 0f) {
            g.color = THEME.getScrollbarColor()

            g.fillRoundRect(
                    scrollBarX.toInt(),
                    layoutSize.h.toInt() - THEME.getScrollBarWidth() + ((1f - (scrollBarOpacityAnim.getValue() - SCROLLBAR_NORMAL_OPACITY) * 5f) * THEME.getScrollBarCornerRadius()).toInt(),
                    scrollBarWidth.toInt(),
                    THEME.getScrollBarWidth() + THEME.getScrollBarCornerRadius(),
                    THEME.getScrollBarCornerRadius(),
                    THEME.getScrollBarCornerRadius()
            )

        }

        g.composite = prevComp

        // undo clip op
        g.clip = prevClip
    }

    override fun onPostDraw(g: Graphics2D) {
        // translate the canvas to dest spot
        g.translate(childX.toInt() + child.translationX.toInt(), childY.toInt() + child.translationY.toInt())
        // translate again to apply scroll. Scroll is negative because we want to reverse the direction of the actual
        // scrolling gesture
        g.translate(-scrollX.toInt(), -scrollY.toInt())
        // finally draw child
        child.onPostDraw(g)
        // undo translations
        g.translate(scrollX.toInt(), scrollY.toInt())
        g.translate(-childX.toInt() - child.translationX.toInt(), -childY.toInt() - child.translationY.toInt())

        super.onPostDraw(g)
    }

    override fun findContextMenuItems(viewPos: FloatPoint) = child.findContextMenuItems(FloatPoint(viewPos.x + scrollX, viewPos.y + scrollY))

}