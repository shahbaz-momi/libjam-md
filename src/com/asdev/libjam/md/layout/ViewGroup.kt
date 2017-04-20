package com.asdev.libjam.md.layout

import com.asdev.libjam.md.menu.ContextMenuItem
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.*
import com.asdev.libjam.md.view.OverlayView
import com.asdev.libjam.md.view.VISIBILITY_VISIBLE
import com.asdev.libjam.md.view.View
import java.awt.Graphics2D

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

    /**
     * Returns the child [View] at the specified index ($i).
     */
    open fun getChildAtIndex(i: Int): View {
        if(i < 0 || i >= getChildCount())
            throw ArrayIndexOutOfBoundsException("There is no child with the specified index ($i)")

        return getChildren()[i]
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
            if(c is ViewGroup)
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
}