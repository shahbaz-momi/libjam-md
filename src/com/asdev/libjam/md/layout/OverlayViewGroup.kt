package com.asdev.libjam.md.layout

import com.asdev.libjam.md.menu.ContextMenuItem
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.FloatPoint
import com.asdev.libjam.md.util.generateRandomId
import com.asdev.libjam.md.view.OverlayView
import com.asdev.libjam.md.view.View
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

/**
 * Created by Asdev on 03/03/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

/**
 * A ViewGroup which is drawn as an overlay on a ViewGroup.
 */
abstract class OverlayViewGroup(id: String = "OverlayViewGroup:${generateRandomId()}", bindViewId: String? = null): OverlayView(id, bindViewId) {


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

    override fun onPostLayout() {
        super.onPostLayout()

        for(c in getChildren())
            c.onPostLayout()
    }

    /**
     * Returns the child [View] at the specified index (i).
     */
    open fun getChildAtIndex(i: Int): View {
        if(i < 0 || i >= getChildCount())
            throw ArrayIndexOutOfBoundsException("There is no child with the specified index ($i)")

        return getChildren()[i]
    }

    /**
     * Implementation of [onThemeChange]. Calls the [View] implementation and then notifies its children of the theme change.
     */
    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)

        // theme change the children as well
        for(c in getChildren())
            c.onThemeChange(prevTheme, newTheme)
    }

    /**
     * Implementation of [loop]. Calls the [View] implementation and then loops its children.
     */
    override fun loop() {
        super.loop()

        for(i in 0 until getChildCount()) {
            getChildAtIndex(i).loop()
        }

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

    override fun onMouseRelease(e: MouseEvent, mPos: Point) {
        super.onMouseRelease(e, mPos)

        // not on the overlay, most be on one of the children
        val children = getChildren()

        for(i in children.indices) {
            // check which view the point is in the bounds of
            val c = children[i]
            val p = findChildPosition(c)!!

            if(mPos.x >= p.x + c.translationX && mPos.x <= p.x + c.translationX + c.layoutSize.w &&
                    mPos.y >= p.y + c.translationY && mPos.y <= p.y + c.translationY + c.layoutSize.h) {
                // in this view, call the child's method
                c.onMouseRelease(e, Point( (mPos.x - p.x - c.translationX).toInt(), (mPos.y - p.y - c.translationY).toInt() ))
                return
            }
        }
    }

    private var previousHoveringView = -1

    override fun onMouseMoved(e: MouseEvent, mPos: Point) {
        super.onMouseMoved(e, mPos)

        val children = getChildren()

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
        // find the focused view
        for (c in children) {
            if (c.state == State.STATE_HOVER || c.state == State.STATE_PRESSED || c.state == State.STATE_FOCUSED) {
                // scroll it
                c.onScroll(e)
                return
            }
        }
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

    abstract fun findChildPosition(child: View): FloatPoint?

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

    private var currentTraverse = 0

    /**
     * Traverses the children of this ViewGroup.
     */
    override fun onTabTraversal(): Boolean {
        val children = getChildren()

        if(currentTraverse < children.size) {
            while(children[currentTraverse].onTabTraversal()) {
                currentTraverse ++

                if(currentTraverse >= children.size) {
                    currentTraverse = 0
                    return true
                }
            }

            return false
        } else {
            // reset the traversal now that it is all done
            currentTraverse = 0
            return true
        }
    }

}