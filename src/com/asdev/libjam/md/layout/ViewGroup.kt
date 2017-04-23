package com.asdev.libjam.md.layout

import com.asdev.libjam.md.menu.ContextMenuItem
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.*
import com.asdev.libjam.md.view.OverlayView
import com.asdev.libjam.md.view.VISIBILITY_VISIBLE
import com.asdev.libjam.md.view.View
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

/**
 * A [View] that holds multiple other [View] children.
 */
abstract class ViewGroup: View() {

    /**
     * The overlay of this ViewGroup.
     */
    private var overlayView: OverlayView? = null

    /**
     * The internal layout params for the overlay.
     */
    private var overlayViewParams = newOverlayLayoutParams()

    /**
     * Adds the specified child to this [ViewGroup]
     */
    abstract fun addChild(child: View)

    /**
     * Removes the specified child from this [ViewGroup]
     */
    abstract fun removeChild(child: View)

    /**
     * Returns the children managed by this [ViewGroup]
     */
    abstract fun getChildren(): Array<View>

    /**
     * Returns the number of children managed by this [ViewGroup]
     */
    open fun getChildCount() = getChildren().size

    /**
     * Sets the local OverlayView to the given one.
     */
    fun setOverlayView(v: OverlayView) {
        overlayView?.onDetach()
        overlayView = v
        overlayView?.onAttach()
        requestLayout()
    }

    /**
     * Returns the local OverlayView.
     */
    fun getOverlayView() = overlayView

    /**
     * Implementation of [onThemeChange]. Calls the [View] implementation and then notifies its children of the theme change.
     */
    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)

        // theme change the children as well
        for(c in getChildren())
            c.onThemeChange(prevTheme, newTheme)

        overlayView?.onThemeChange(prevTheme, newTheme)
    }

    /**
     * Implementation of [loop]. Calls the [View] implementation and then loops its children.
     */
    override fun loop() {
        super.loop()

        for(i in 0 until getChildCount()) {
            getChildAtIndex(i).loop()
        }

        overlayView?.loop()
    }

    override fun onPostLoop() {
        super.onPostLoop()

        for(i in 0 until getChildCount()) {
            getChildAtIndex(i).onPostLoop()
        }

        overlayView?.onPostLoop()
    }

    /**
     * Returns the child [View] at the specified index ($i).
     */
    open fun getChildAtIndex(i: Int): View {
        if(i < 0 || i >= getChildCount())
            throw ArrayIndexOutOfBoundsException("There is no child with the specified index ($i)")

        return getChildren()[i]
    }

    /////// MOUSE METHODS IMPL BEGINS //////

    override fun onMouseEnter(e: MouseEvent, mPos: Point) {
        super.onMouseEnter(e, mPos)
    }

    override fun onMouseExit(e: MouseEvent, mPos: Point) {
        super.onMouseExit(e, mPos)

        // mouse exited this view, call exit on any view it was hovering on inside this layout
        if(previousHoveringView != -1) {
            getChildAtIndex(previousHoveringView).onMouseExit(e, mPos)
            previousHoveringView = -1
        }
    }

    override fun onMousePress(e: MouseEvent, mPos: Point) {
        super.onMousePress(e, mPos)

        // check if it is in the bounds of the overlay first
        val overlay = getOverlayView()
        if(overlay != null) {
            if(mPos.x >= overlay.position.x + overlay.translationX && mPos.x <= overlay.position.x + overlay.translationX + overlay.layoutSize.w
                    && mPos.y >= overlay.position.y + overlay.translationY && mPos.y <= overlay.position.y + overlay.translationY + overlay.layoutSize.h) {
                // in the overlay
                overlay.onMousePress(e, Point((mPos.x - overlay.position.x - overlay.translationX).toInt(), (mPos.y - overlay.position.y - overlay.translationY).toInt()))
                return
            }
        }

        // not on the overlay, most be on one of the children
        val children = getChildren()

        for(i in children.indices) {
            // check which view the point is in the bounds of
            val c = children[i]
            val p = findChildPosition(c)!!

            if(mPos.x >= p.x + c.translationX && mPos.x <= p.x + c.translationX + c.layoutSize.w &&
                    mPos.y >= p.y + c.translationY && mPos.y <= p.y + c.translationY + c.layoutSize.h) {
                // in this view, call the child's method
                c.onMousePress(e, Point( (mPos.x - p.x - c.translationX).toInt(), (mPos.y - p.y - c.translationY).toInt() ))
                return
            }
        }
    }

    private var focusMutex = -1

    override fun onMouseRelease(e: MouseEvent, mPos: Point) {
        super.onMouseRelease(e, mPos)

        // check if it is in the bounds of the overlay first
        val overlay = getOverlayView()
        if(overlay != null) {
            if(mPos.x >= overlay.position.x + overlay.translationX && mPos.x <= overlay.position.x + overlay.translationX + overlay.layoutSize.w
                    && mPos.y >= overlay.position.y + overlay.translationY && mPos.y <= overlay.position.y + overlay.translationY + overlay.layoutSize.h) {
                // in the overlay
                overlay.onMouseRelease(e, Point((mPos.x - overlay.position.x - overlay.translationX).toInt(), (mPos.y - overlay.position.y - overlay.translationY).toInt()))
                return
            }
        }

        // not on the overlay, most be on one of the children
        val children = getChildren()

        for(i in children.indices) {
            // check which view the point is in the bounds of
            val c = children[i]
            val p = findChildPosition(c)!!

            if(mPos.x >= p.x + c.translationX && mPos.x <= p.x + c.translationX + c.layoutSize.w &&
                    mPos.y >= p.y + c.translationY && mPos.y <= p.y + c.translationY + c.layoutSize.h) {

                if(focusMutex != i) {
                    if(focusMutex != -1) {
                        // the previous focused view has lost focus
                        children[focusMutex].onFocusLost()
                    }

                    // drop any lingering focuses
                    for(j in children) {
                        if(j.state == State.STATE_FOCUSED && j != c) {
                            j.onFocusLost()
                        }
                    }

                    // this view now has focus
                    c.onFocusGained()
                    focusMutex = i
                }

                // in this view, call the child's method
                c.onMouseRelease(e, Point( (mPos.x - p.x - c.translationX).toInt(), (mPos.y - p.y - c.translationY).toInt() ))
                return
            }
        }
    }

    override fun onFocusLost() {
        super.onFocusLost()

        // release the focus mutex of this group as well
        if(focusMutex != -1) {
            getChildAtIndex(focusMutex).onFocusLost()
            focusMutex = -1
        }
    }

    private var previousHoveringView = -1
    private var wasHoveringOverlay = false

    override fun onMouseMoved(e: MouseEvent, mPos: Point) {
        super.onMouseMoved(e, mPos)

        val children = getChildren()

        // if it is on the overlay, check that first
        val overlay = getOverlayView()

        if(overlay != null) {
            // check if the point is within the bounds of the overlay
            if(mPos.x >= overlay.position.x + overlay.translationX && mPos.x <= overlay.position.x + overlay.translationX + overlay.layoutSize.w
                    && mPos.y >= overlay.position.y + overlay.translationY && mPos.y <= overlay.position.y + overlay.translationY + overlay.layoutSize.h) {

                // was it on the overlay before?
                // if it wasn't, call mouse exit on the previous view
                // and mouse enter on the overlay it self
                if(!wasHoveringOverlay) {
                    // was it on a view previously?
                    if(previousHoveringView != -1) {
                        // notify the previous child that the mouse has left that view
                        children[previousHoveringView].onMouseExit(e, mPos)
                    }

                    // reset the hovering states, as now it is hovering the overlay
                    wasHoveringOverlay = true
                    previousHoveringView = -1
                    overlay.onMouseEnter(e, mPos)
                }

                // the standard mouse move on the overlay
                overlay.onMouseMoved(e, Point((mPos.x - overlay.position.x - overlay.translationX).toInt(), (mPos.y - overlay.position.y - overlay.translationY).toInt()))
                return
            } else {
                // not on the overlay, but if it was previously, notify the overlay of a mouse exit
                if(wasHoveringOverlay) {
                    overlay.onMouseExit(e, mPos)
                    wasHoveringOverlay = false
                }
            }
        }

        // find the child the mouse is currently hovering over
        for(i in children.indices) {
            val c = children[i]
            val p = findChildPosition(c)!!

            if(mPos.x >= p.x + c.translationX && mPos.x <= p.x + c.translationX + c.layoutSize.w &&
                    mPos.y >= p.y + c.translationY && mPos.y <= p.y + c.translationY + c.layoutSize.h) {
                // fits this view
                // check the mouse was on a different view previously
                if(previousHoveringView != i) {
                    // call mouse exit on the previous view if it was set
                    if(previousHoveringView != -1) {
                        children[previousHoveringView].onMouseExit(e, mPos)
                    }

                    // call mouse enter on this view
                    c.onMouseEnter(e, mPos)
                    // set this as the hovering view
                    previousHoveringView = i
                }

                // call standard mouse move
                c.onMouseMoved(e, Point( (mPos.x - p.x - c.translationX).toInt(), (mPos.y - p.y - c.translationY).toInt() ))
                return
            }
        }

        // not on any view, so if it was on one before call exit on it
        if(previousHoveringView != -1) {
            children[previousHoveringView].onMouseExit(e, mPos)
            previousHoveringView = -1
        }
    }

    override fun onMouseDragged(e: MouseEvent, mPos: Point) {
        super.onMouseDragged(e, mPos)

        val children = getChildren()

        // if it is on the overlay, check that first
        val overlay = getOverlayView()

        if(overlay != null) {
            // check if the point is within the bounds of the overlay
            if(mPos.x >= overlay.position.x + overlay.translationX && mPos.x <= overlay.position.x + overlay.translationX + overlay.layoutSize.w
                    && mPos.y >= overlay.position.y + overlay.translationY && mPos.y <= overlay.position.y + overlay.translationY + overlay.layoutSize.h) {

                // was it on the overlay before?
                // if it wasn't, call mouse exit on the previous view
                // and mouse enter on the overlay it self
                if(!wasHoveringOverlay) {
                    // was it on a view previously?
                    if(previousHoveringView != -1) {
                        // notify the previous child that the mouse has left that view
                        children[previousHoveringView].onMouseExit(e, mPos)
                    }

                    // reset the hovering states, as now it is hovering the overlay
                    wasHoveringOverlay = true
                    previousHoveringView = -1
                    overlay.onMouseEnter(e, mPos)
                }

                // the standard mouse move on the overlay
                overlay.onMouseDragged(e, Point((mPos.x - overlay.position.x - overlay.translationX).toInt(), (mPos.y - overlay.position.y - overlay.translationY).toInt()))
                return
            } else {
                // not on the overlay, but if it was previously, notify the overlay of a mouse exit
                if(wasHoveringOverlay) {
                    overlay.onMouseExit(e, mPos)
                    wasHoveringOverlay = false
                }
            }
        }

        // find the child the mouse is currently hovering over
        for(i in children.indices) {
            val c = children[i]
            val p = findChildPosition(c)!!

            if(mPos.x >= p.x + c.translationX && mPos.x <= p.x + c.translationX + c.layoutSize.w &&
                    mPos.y >= p.y + c.translationY && mPos.y <= p.y + c.translationY + c.layoutSize.h) {
                // fits this view
                // check the mouse was on a different view previously
                if(previousHoveringView != i) {
                    // call mouse exit on the previous view if it was set
                    if(previousHoveringView != -1) {
                        children[previousHoveringView].onMouseExit(e, mPos)
                    }

                    // call mouse enter on this view
                    c.onMouseEnter(e, mPos)
                    // set this as the hovering view
                    previousHoveringView = i
                }

                // call standard mouse move
                c.onMouseDragged(e, Point( (mPos.x - p.x - c.translationX).toInt(), (mPos.y - p.y - c.translationY).toInt() ))
                return
            }
        }

        // not on any view, so if it was on one before call exit on it
        if(previousHoveringView != -1) {
            children[previousHoveringView].onMouseExit(e, mPos)
            previousHoveringView = -1
        }
    }

    /**
     * Calls [onKeyTyped] on all the children that are focused.
     */
    override fun onKeyTyped(e: KeyEvent) {
        super.onKeyTyped(e)

        val children = getChildren()
        // check if it was on the overlay before
        val overlay = getOverlayView()

        if(wasHoveringOverlay && overlay != null) {
            overlay.onKeyTyped(e)
            return
        }

        for(c in children)
            if(c.state == State.STATE_FOCUSED || c.state == State.STATE_HOVER)
                c.onKeyTyped(e)
    }

    /**
     * Calls [onKeyPressed] on all the children that are focused.
     */
    override fun onKeyPressed(e: KeyEvent) {
        super.onKeyPressed(e)

        val children = getChildren()
        // check if it was on the overlay before
        val overlay = getOverlayView()

        if(wasHoveringOverlay && overlay != null) {
            overlay.onKeyPressed(e)
            return
        }

        for(c in children)
            if(c.state == State.STATE_FOCUSED || c.state == State.STATE_HOVER)
                c.onKeyPressed(e)
    }

    /**
     * Calls [onKeyReleased] on all the children that are focused.
     */
    override fun onKeyReleased(e: KeyEvent) {
        super.onKeyReleased(e)

        val children = getChildren()
        // check if it was on the overlay before
        val overlay = getOverlayView()

        if(wasHoveringOverlay && overlay != null) {
            overlay.onKeyReleased(e)
            return
        }

        for(c in children)
            if(c.state == State.STATE_FOCUSED || c.state == State.STATE_HOVER)
                c.onKeyReleased(e)
    }

    /**
     * Scrolls the proper child of this layout.
     */
    override fun onScroll(e: MouseWheelEvent) {
        super.onScroll(e)

        val children = getChildren()
        val overlay = getOverlayView()

        if(wasHoveringOverlay && overlay != null) {
            overlay.onScroll(e)
        } else {
            // find the focused view
            for (c in children) {
                if (c.state == State.STATE_HOVER || c.state == State.STATE_PRESSED || c.state == State.STATE_FOCUSED) {
                    // scroll it
                    c.onScroll(e)
                    return
                }
            }
        }
    }

    /**
     * Returns the view with the specified id, or null if none found.
     */
    open fun findViewById(id: String): View? {
        if(this.id == id) {
            return this
        }

        for(i in 0 until getChildCount()) {
            val child = getChildAtIndex(i)
            if(child is ViewGroup) {
                val v = child.findViewById(id)
                if(v != null)
                    return v
            } else if(child.id == id){
                return child
            }
        }

        return null
    }

    /**
     * Returns the local coordinates of the specified child excluding the local translations, or null if none found.
     */
    abstract fun findChildPosition(child: View): FloatPoint?

    /**
     * Returns the coordinates relative to this view group of the view if it is a child of this ViewGroup or one of its descendants.
     */
    open fun findViewPosition(v: View): FloatPoint? {
        return findViewPosition0(v, FloatPoint(0f, 0f))
    }

    /**
     * Internal method for finding the view position.
     */
    protected open fun findViewPosition0(v: View, yourPos: FloatPoint): FloatPoint? {
        if(v == this) {
            return yourPos
        }

        // check if the view is a child of ours
        val pos = findChildPosition(v)
        if(pos != null) {
            return pos + yourPos
        } else {
            val children = getChildren()
            for(c in children) {
                if(c is ViewGroup) {
                    val p = c.findViewPosition0(v, yourPos + findChildPosition(c)!!)
                    if(p != null)
                        return p
                } else if(c is PaddingLayout) {
                    // special case for paddinglayout, which actually isnt a layout
                    if(c.child == v) {
                        return yourPos + c.findChildPosition() + findChildPosition(c)!!
                    } else if(c.child is ViewGroup) {
                        val p = c.child.findViewPosition0(v, yourPos + findChildPosition(c)!! + c.findChildPosition())
                        if(p != null)
                            return p
                    }
                } else if(c is ElevatedLayout) {
                    // special case for elevation layout, which actually isnt a layout
                    if (c.child == v) {
                        return yourPos + c.findChildPosition() + findChildPosition(c)!!
                    } else if (c.child is ViewGroup) {
                        val p = c.child.findViewPosition0(v, yourPos + findChildPosition(c)!! + c.findChildPosition())
                        if (p != null)
                            return p
                    }
                }
            }
        }

        // not found, return null
        return null
    }

    /**
     * Measures this ViewGroup along with its OverlayView.
     */
    override fun onMeasure(result: LayoutParams): LayoutParams {
        if(overlayView != null)
            overlayViewParams = overlayView?.onMeasure(newOverlayLayoutParams()) as? OverlayLayoutParams?: throw IllegalArgumentException("OverlayView must return overlay layout params!")
        // overlayView?.visibility = VISIBILITY_INVISIBLE

        return super.onMeasure(result)
    }

    override fun onPostLayout() {
        super.onPostLayout()

        val children = getChildren()
        for(c in children) {
            c.onPostLayout()
        }

        val hardRef = overlayView
        var size = layoutSize.copy()

        // determine the size of the overlay
        if(hardRef != null) {
            val dims = overlayViewParams
            // do a size check
            if(!(dims.minSize to dims.maxSize fits size)) {
                // if size is smaller than use the min size otherwise use the max size
                var newW = size.w
                var newH = size.h
                if(size.w <= dims.minSize.w && dims.minSize != DIM_UNSET && dims.minSize != DIM_UNLIMITED) {
                    // use min w
                    newW = dims.minSize.w
                } else if(size.w >= dims.maxSize.w && dims.maxSize != DIM_UNSET && dims.maxSize != DIM_UNLIMITED) {
                    newW = dims.maxSize.w
                }

                if(size.h <= dims.minSize.h && dims.minSize != DIM_UNSET && dims.minSize != DIM_UNLIMITED) {
                    newH = dims.minSize.h
                } else if(size.h >= dims.maxSize.h && dims.maxSize != DIM_UNSET && dims.maxSize != DIM_UNLIMITED) {
                    newH = dims.maxSize.h
                }

                size = FloatDim(newW, newH)
            }
        }

        // find the bound view if one exists for the overlay view
        if(hardRef != null && hardRef.bindViewId != null) {
            val bindId = hardRef.bindViewId!!
            val view = findViewById(bindId)

            // find the bind view
            if(view != null) {
                // get the position of it
                val viewPos = findViewPosition(view)!!
                // anchor it to that position with the gravity
                val position = FloatPoint(calculateXComp(overlayViewParams.gravity, viewPos.x, view.layoutSize.w, size.w), calculateYComp(overlayViewParams.gravity, viewPos.y, view.layoutSize.h, size.h))
                // account for the anchor
                position.x = calculateAnchorX(overlayViewParams.anchor, position.x, size.w, viewPos.x, view.layoutSize.w)
                position.y = calculateAnchorY(overlayViewParams.anchor, position.y, size.h, viewPos.y, view.layoutSize.h)
                // perform the layout and post layout
                hardRef.onLayout(size)
                hardRef.onPostLayout(position, view)
            } else {
                //no bind view
                // size is established, so anchor by the set gravity
                val position = FloatPoint(calculateXComp(overlayViewParams.gravity, 0f, layoutSize.w, size.w), calculateYComp(overlayViewParams.gravity, 0f, layoutSize.h, size.h))
                // perform the on post
                hardRef.onLayout(size)
                hardRef.onPostLayout(position, null)
            }
        } else if(hardRef != null) {
            //no bind view
            // size is established, so anchor by the set gravity
            val position = FloatPoint(calculateXComp(overlayViewParams.gravity, 0f, layoutSize.w, size.w), calculateYComp(overlayViewParams.gravity, 0f, layoutSize.h, size.h))
            // perform the on post
            hardRef.onLayout(size)
            hardRef.onPostLayout(position, null)
        }

        overlayView?.onPostLayout()

        // make it visible again
        hardRef?.visibility = VISIBILITY_VISIBLE
    }

    protected fun calculateAnchorY(anchor: Int, originalY: Float, objectH: Float, boxY: Float, boxH: Float): Float {
        if(anchor == ANCHOR_ABOVE) {
            return boxY - objectH
        } else if(anchor == ANCHOR_BELOW) {
            return boxY + boxH
        }

        return originalY
    }

    protected fun calculateAnchorX(anchor: Int, originalX: Float, objectW: Float, boxX: Float, boxW: Float): Float {
        if(anchor == ANCHOR_TO_LEFT) {
            return boxX - objectW
        } else if(anchor == ANCHOR_TO_RIGHT) {
            return boxX + boxW
        }

        return originalX
    }

    /**
     * Calculates the x-component of the corresponding gravity.
     * @param bX bounding box X
     * @param bW bounding box W
     * @param oW object W
     */
    protected fun calculateXComp(gravity: Int, bX: Float, bW: Float, oW: Float): Float {
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
    protected fun calculateYComp(gravity: Int, bY: Float, bH: Float, oH: Float): Float {
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

    override fun onPostDraw(g: Graphics2D) {
        super.onPostDraw(g)

        val hardRef = overlayView
        if(hardRef != null) {
            g.translate(hardRef.position.x.toDouble() + hardRef.translationX, hardRef.position.y.toDouble() + hardRef.translationY)
            // no clip cuz its a overlay view
            hardRef.onDraw(g)
            hardRef.onPostDraw(g)
            g.translate(-hardRef.position.x.toDouble() - hardRef.translationX, -hardRef.position.y.toDouble() - hardRef.translationY)
        }
    }

    /**
     * Finds the context menu items for the view at the given position, or the most lower-level non-null
     * context menu items if none for the children are found. Avoids returning no items/null.
     */
    override fun findContextMenuItems(viewPos: FloatPoint): List<ContextMenuItem>? {
        // find the child at that point
        val children = getChildren()
        val overlay = getOverlayView()

        if(wasHoveringOverlay && overlay != null) {
            return overlay.findContextMenuItems(viewPos - overlay.position)
        }

        for(c in children) {
            val pos = findChildPosition(c)?: throw IllegalStateException("Unable to find the position of my own child!")

            // check if the point is in the view
            if(viewPos.x >= pos.x && viewPos.x <= pos.x + c.layoutSize.w
                    && viewPos.y >= pos.y && viewPos.y <= pos.y + c.layoutSize.h) {

                // return the items of that view if they aren't null
                val items = c.findContextMenuItems(viewPos - pos)
                if(items != null)
                    return items

                // we already found the view at the position, so break now
                break
            }
        }

        return super.findContextMenuItems(viewPos)
    }

    private var currentTraverse = 0

    /**
     * Traverses the children of this ViewGroup.
     */
    override fun onTabTraversal(): Boolean {
        val children = getChildren()

        // find any previously focused/hovered views and go from there
        for(i in children.indices) {
            if(children[i].state == State.STATE_FOCUSED && i != currentTraverse) {
                children[i].onFocusLost()
            }
        }

        if(currentTraverse < children.size) {
            while(children[currentTraverse].onTabTraversal()) {
                currentTraverse ++
                focusMutex = currentTraverse

                if(currentTraverse >= children.size) {
                    currentTraverse = 0
                    focusMutex = -1
                    return true
                }
            }

            return false
        } else {
            // reset the traversal now that it is all done
            currentTraverse = 0
            focusMutex = -1
            return true
        }
    }
}