package com.asdev.libjam.md.layout

import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.util.FloatPoint
import com.asdev.libjam.md.view.OverlayView
import com.asdev.libjam.md.view.VISIBILITY_INVISIBLE
import com.asdev.libjam.md.view.View

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
    private var overlayViewParams = newRelativeLayoutParams()

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
        overlayView?.onAttach()
        overlayView = v
        overlayView?.onDetach()
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
        // check if the view is a child of ours
        val pos = findChildPosition(v)
        if(pos != null) {
            return pos add yourPos
        } else {
            val children = getChildren()
            for(c in children) {
                if(c is ViewGroup) {
                    val p = c.findViewPosition0(v, yourPos add findChildPosition(c)!!)
                    if(p != null)
                        return p
                } else if(c is PaddingLayout) {
                    // special case for paddinglayout, which actually isnt a layout
                    if(c.child == v) {
                        return yourPos add c.findChildPosition() add findChildPosition(c)!!
                    } else if(c.child is ViewGroup) {
                        val p = c.child.findViewPosition0(v, yourPos add findChildPosition(c)!! add c.findChildPosition())
                        if(p != null)
                            return p
                    }
                } else if(c is ElevatedLayout) {
                    // special case for elevation layout, which actually isnt a layout
                    if(c.child == v) {
                        return yourPos add c.findChildPosition() add findChildPosition(c)!!
                    } else if(c.child is ViewGroup) {
                        val p = c.child.findViewPosition0(v, yourPos add findChildPosition(c)!! add c.findChildPosition())
                        if(p != null)
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
        overlayViewParams = overlayView?.onMeasure(newRelativeLayoutParams()) as? RelativeLayoutParams?: throw IllegalArgumentException("OverlayView must return relative layout params!")
        overlayView?.visibility = VISIBILITY_INVISIBLE

        return super.onMeasure(result)
    }

}