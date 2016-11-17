package com.asdev.libjam.md.layout

import com.asdev.libjam.md.util.*
import com.asdev.libjam.md.view.VISIBILITY_VISIBLE
import com.asdev.libjam.md.view.View
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.util.*

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

/**
 * A [ViewGroup] that manages children in a grid-like fashion.
 */
class RelativeLayout: ViewGroup() {

    // array of children in nine spots
    /**
     * The temporary list of children managed by this [ViewGroup]
     */
    private val children = ArrayList<View>()
    /**
     * The children of this [ViewGroup] in order of their gravity.
     */
    private val orderedChildren = Array<View?>(9){null}
    // the layout parameters of each child. Stored to prevent recalling onMeasure()
    /**
     * The cache of [LayoutParams] of each child [View]. In order of the child's gravity.
     */
    private val layoutParams = Array<RelativeLayoutParams?>(9) {null}
    // the coordinates of each child
    /**
     * The coordinates of each child, in order of the child's gravity.
     */
    private val childCoords = Array<FloatPoint?>(9) {null}

    // the actual minimum size of all of the children combined
    /**
     * The absolute minimum size required to fit the children into this [ViewGroup].
     */
    private var minChildSize = DIM_UNSET

    override fun removeChild(child: View) { children.remove(child) }

    override fun getChildren() = children.toTypedArray()

    override fun addChild(child: View) {
        if(DEBUG) {
            println("[RelativeLayout] Adding child: $child")
        }
        // post a request layout to the root view
        requestLayout()
        children.add(child)
    }

    override fun loop() {
        super.loop()
    }

    /**
     * Measures it self and its children.
     */
    override fun onMeasure(result: LayoutParams): LayoutParams {
        if(DEBUG) {
            println("[RelativeLayout] onMeasure")
        }
        // if there are no children, don't even bother
        if(children.isEmpty())
            return super.onMeasure(result)

        // first make sure that there is no conflicting gravity views
        // erase the previous layout parameters
        layoutParams.fill(null)
        orderedChildren.fill(null)

        // measure each view and make sure they don't conflict
        children.forEach {
            // measure each and store, error if duplicate
            val lp = it.onMeasure(newRelativeLayoutParams())
            if(lp !is RelativeLayoutParams) {
                throw IllegalArgumentException("Layout parameters must be of type RelativeLayoutParams!")
            } else if(lp.gravity > GRAVITY_BOTTOM_RIGHT) {
                // invalid gravity. Bottom right is the highest gravity (9).
                throw IllegalArgumentException("Layout parameter gravity isn't valid: $lp")
            } else if(layoutParams[lp.gravity] != null) { // if there is already a view for that gravity, then error
                throw IllegalStateException("Two views cannot share the same gravity! $lp")
            } else {
                // assign it to the array based off of gravity as the index
                layoutParams[lp.gravity] = lp
                orderedChildren[lp.gravity] = it
            }
        }

        // compute the min width by compute the width of each row and taking the largest one
        var minW = DIM_UNSET.w
        for(i in GRAVITY_TOP_LEFT..GRAVITY_TOP_RIGHT) {
            val lp = layoutParams[i]
            // add the min width if it isn't UNSET or UNLIMITED
            if(lp != null) {
                if(lp.minSize.w > 0f) {
                    // add it
                    if(minW == DIM_UNSET.w)
                        minW = 0f
                    minW += lp.minSize.w
                }
            }
        }
        // do it for the middle row as well
        var temp = DIM_UNSET.w
        for(i in GRAVITY_MIDDLE_LEFT..GRAVITY_MIDDLE_RIGHT) {
            val lp = layoutParams[i]
            if(lp != null) {
                if(lp.minSize.w > 0f) {
                    if(temp == DIM_UNSET.w)
                        temp = 0f
                    temp += lp.minSize.w
                }
            }
        }

        minW = Math.max(minW, temp)

        // lastly the bottom row
        temp = DIM_UNSET.w
        for(i in GRAVITY_BOTTOM_LEFT..GRAVITY_BOTTOM_RIGHT) {
            val lp = layoutParams[i]
            if(lp != null) {
                if(lp.minSize.w > 0f) {
                    if(temp == DIM_UNSET.w)
                        temp = 0f
                    temp += lp.minSize.w
                }
            }
        }

        minW = Math.max(minW, temp)

        // compute the min H
        var minH = DIM_UNSET.h
        // left row
        for(i in 0..2) {
            val lp = layoutParams[GRAVITY_ARRAY_ROW_LEFT[i]]
            if(lp != null) {
                if(lp.minSize.h > 0f) {
                    if(minH == DIM_UNSET.h)
                        minH = 0f
                    minH += lp.minSize.h
                }
            }
        }

        temp = DIM_UNSET.h
        // middle row
        for(i in 0..2) {
            val lp = layoutParams[GRAVITY_ARRAY_ROW_MIDDLE[i]]
            if(lp != null) {
                if(lp.minSize.h > 0f) {
                    if(temp == DIM_UNSET.h)
                        temp = 0f
                    temp += lp.minSize.h
                }
            }
        }

        minH = Math.max(minH, temp)

        temp = DIM_UNSET.h
        // right row
        for(i in 0..2) {
            val lp = layoutParams[GRAVITY_ARRAY_ROW_RIGHT[i]]
            if(lp != null) {
                if(lp.minSize.h > 0f) {
                    if(temp == DIM_UNSET.h)
                        temp = 0f
                    temp += lp.minSize.h
                }
            }
        }

        minH = Math.max(minH, temp)

        minChildSize = FloatDim(Math.max(minW, 0f), Math.max(minH, 0f))

        // we know know our minimum dimensions. Maximum will be the same but it must be greater than the minimum
        // minimum can stay the same as well but only if it is greater than the computed minimum
        if(minSize.w > minW)
            minW = minSize.w
        if(minSize.h > minH)
            minH = minSize.h

        minSize = FloatDim(minW, minH)

        if(DEBUG)
            println("[RelativeLayout] Computed a minimum size of $minChildSize and but using $minSize")

        // if there is unlimited max size, then no need to update
        if(maxSize != DIM_UNSET && maxSize != DIM_UNLIMITED) {
            var maxW = minW
            var maxH = minH

            if (maxSize.w > maxW)
                maxW = maxSize.w
            if (maxSize.h > maxH)
                maxH = maxSize.h

            maxSize = FloatDim(maxW, maxH)
        }

        // min and max get applied by the existing View.onMeasure impl. So, just use that.
        // also applies the additional parameters
        return super.onMeasure(result)
    }

    /**
     * Lays out the children to fit in the given size ($newSize).
     */
    override fun onLayout(newSize: FloatDim) {
        if(DEBUG) {
            println("[RelativeLayout] Laying out to size $newSize")
        }
        // min size can safely pack all of the items in without clipping
        // the the delta between min size and newSize and use that as a grow value
        // divide the grow value by three and add it to the size of each view until max size is reached.
        // add the overflow grow value of the view (if it has reached max size) to the grow value then redistribute
        // it evenly. To calculate co-ordinates, simply add up the widths and height upto that view

        // calculate the difference between the widthes
        val newSizes = Array(9) { DIM_UNSET }

        layoutRow(newSize, newSizes, GRAVITY_TOP_LEFT, GRAVITY_TOP_RIGHT)
        layoutRow(newSize, newSizes, GRAVITY_MIDDLE_LEFT, GRAVITY_MIDDLE_RIGHT)
        layoutRow(newSize, newSizes, GRAVITY_BOTTOM_LEFT, GRAVITY_BOTTOM_RIGHT)

        layoutColumn(newSize, newSizes, GRAVITY_ARRAY_ROW_LEFT)
        layoutColumn(newSize, newSizes, GRAVITY_ARRAY_ROW_MIDDLE)
        layoutColumn(newSize, newSizes, GRAVITY_ARRAY_ROW_RIGHT)

        for(i in 0 until orderedChildren.size) {
            if(orderedChildren[i] != null) {
                // set the new size
                orderedChildren[i]!!.onLayout(newSizes[i])
            }
        }

        super.onLayout(newSize)
    }

    /**
     * Lays out the specified row. Internal method only.
     */
    private fun layoutRow(newSize: FloatDim, newSizes: Array<FloatDim>, rangeStart: Int, rangeEnd: Int) {
        // calculate the difference between the widthes
        val deltaW = newSize.w - minSize.w

        var numViews = 0
        val indices = Array(3) {-1}
        for(i in rangeStart..rangeEnd)
            if(orderedChildren[i] != null) {
                indices[numViews] = i
                numViews ++
            }

        val diffPerView = deltaW / numViews + (minSize.w - minChildSize.w) / numViews

        // the difference between the new size and the min size, relative to each view
        for(i in 0 until numViews) {
            // calculate the new width
            val newW = Math.max(layoutParams[indices[i]]!!.minSize.w, 0f) + diffPerView
            // check if the new width fits the bounding box
            if(layoutParams[indices[i]]!!.maxSize.w > 0f && newW > layoutParams[indices[i]]!!.maxSize.w) {
                // set size to max width
                newSizes[indices[i]] = FloatDim(layoutParams[indices[i]]!!.maxSize.w, DIM_UNSET.h)

                childCoords[indices[i]] = FloatPoint( calculateXComp( layoutParams[indices[i]]!!.gravity, newSize.w, layoutParams[indices[i]]!!.maxSize.w ) , POINT_UNSET.y)
            } else {
                // the new size is newW
                newSizes[indices[i]] = FloatDim(newW, DIM_UNSET.h)

                childCoords[indices[i]] = FloatPoint( calculateXComp(layoutParams[indices[i]]!!.gravity, newSize.w, newW), POINT_UNSET.y )
            }
        }
    }

    /**
     * Layouts out the specified column. Internal method only.
     */
    private fun layoutColumn(newSize: FloatDim, newSizes: Array<FloatDim>, viewRange: Array<Int>) {
        // calculate the difference between the heights
        val deltaH = newSize.h - minSize.h

        var numViews = 0
        val indices = Array(3) {-1}
        for(i in viewRange)
            if(orderedChildren[i] != null) {
                indices[numViews] = i
                numViews ++
            }

        var numVertViews = 0

        for(i in GRAVITY_TOP_LEFT..GRAVITY_TOP_RIGHT) {
            if(orderedChildren[i] != null) {
                numVertViews += 1
                break
            }
        }

        for(i in GRAVITY_MIDDLE_LEFT..GRAVITY_MIDDLE_RIGHT) {
            if(orderedChildren[i] != null) {
                numVertViews += 1
                break
            }
        }

        for(i in GRAVITY_BOTTOM_LEFT..GRAVITY_BOTTOM_RIGHT) {
            if(orderedChildren[i] != null) {
                numVertViews += 1
                break
            }
        }

        if(DEBUG) {
            println("[RelativeLayout] Using $numVertViews rows")
        }

        val diffPerView = deltaH / numVertViews + (minSize.h - minChildSize.h) / numVertViews

        // the difference between the new size and the min size, relative to each view

        for(i in 0 until numViews) {
            // calculate the new height
            val newH = Math.max(layoutParams[indices[i]]!!.minSize.h, 0f) + diffPerView

            // check if the new height fits the bounding box
            if(layoutParams[indices[i]]!!.maxSize.h > 0f && newH > layoutParams[indices[i]]!!.maxSize.h) {
                // set size to max height
                newSizes[indices[i]].h = layoutParams[indices[i]]!!.maxSize.h

                if(numVertViews == 2) {
                    val gravity = layoutParams[indices[i]]!!.gravity
                    if(gravity >= GRAVITY_TOP_LEFT && gravity <= GRAVITY_TOP_RIGHT) {
                        childCoords[indices[i]]!!.y = calculateYComp(layoutParams[indices[i]]!!.gravity, newSize.h, layoutParams[indices[i]]!!.maxSize.h)
                    } else {
                        // force it to the bottom
                        childCoords[indices[i]]!!.y = calculateYComp(GRAVITY_BOTTOM_MIDDLE, newSize.h, layoutParams[indices[i]]!!.maxSize.h)
                    }
                } else {
                    childCoords[indices[i]]!!.y = calculateYComp(layoutParams[indices[i]]!!.gravity, newSize.h, layoutParams[indices[i]]!!.maxSize.h)
                }

                if(DEBUG) {
                    println("[RelativeLayout] Using ${newSizes[indices[i]]} (max size of view) view ${indices[i]} at position ${childCoords[indices[i]]}")
                }
            } else {
                // the new size is newH
                newSizes[indices[i]].h = newH

                if(numVertViews == 2) {
                    // check if the top view(s). otherwise it is the bottom view
                    val gravity = layoutParams[indices[i]]!!.gravity
                    if(gravity >= GRAVITY_TOP_LEFT && gravity <= GRAVITY_TOP_RIGHT) {
                        childCoords[indices[i]]!!.y = calculateYComp(layoutParams[indices[i]]!!.gravity, newSize.h, newH)
                    } else {
                        // force it to the bottom
                        childCoords[indices[i]]!!.y = calculateYComp(GRAVITY_BOTTOM_MIDDLE, newSize.h, newH)
                    }
                } else {
                    childCoords[indices[i]]!!.y = calculateYComp(layoutParams[indices[i]]!!.gravity, newSize.h, newH)
                }

                if(DEBUG) {
                    println("[RelativeLayout] Using ${newSizes[indices[i]]} view ${indices[i]} at position ${childCoords[indices[i]]}")
                }
            }
        }
    }

    /**
     * Calculates the x-component of the corresponding gravity.
     * @param bW bounding box W
     * @param oW object W
     */
    private fun calculateXComp(gravity: Int, bW: Float, oW: Float): Float {
        if(gravity == GRAVITY_TOP_LEFT ||
            gravity == GRAVITY_MIDDLE_LEFT ||
            gravity == GRAVITY_BOTTOM_LEFT) {
            // just return bounding box x
            return 0f
        } else if(gravity == GRAVITY_TOP_RIGHT ||
                    gravity == GRAVITY_MIDDLE_RIGHT ||
                    gravity == GRAVITY_BOTTOM_RIGHT) {
            // align to the right
            return bW - oW
        } else {
            // gravity has to be a center
            return bW / 2f - oW / 2f
        }
    }

    /**
     * Calculates the y-component of the corresponding gravity.
     * @param bH bounding box H
     * @param oH object H
     */
    private fun calculateYComp(gravity: Int, bH: Float, oH: Float): Float {
        if(gravity == GRAVITY_TOP_LEFT ||
                gravity == GRAVITY_TOP_MIDDLE ||
                gravity == GRAVITY_TOP_RIGHT) {
            // just return 0f
            return 0f
        } else if(gravity == GRAVITY_MIDDLE_LEFT ||
                gravity == GRAVITY_MIDDLE_MIDDLE ||
                gravity == GRAVITY_MIDDLE_RIGHT) {
            // align to the middle
            return bH / 2f - oH / 2f
        } else {
            // align to bottom
            return bH - oH
        }
    }

    /**
     * Calls [View]'s implementation of [onMousePress] then checks to find which child is being pressed on. Finally,
     * it calls [onMousePress] of that child.
     */
    override fun onMousePress(e: MouseEvent, mPos: Point) {
        super.onMousePress(e, mPos)
        // check the bounds of each child view and see if it fits
        for(i in 0 until orderedChildren.size) {
            val c = orderedChildren[i] ?: continue
            val p = childCoords[i]!!
            // check if it fits betweens p and c.layoutSize
            if(mPos.x >= p.x && mPos.x <= p.x + c.layoutSize.w &&
                mPos.y >= p.y && mPos.y <= p.y + c.layoutSize.h) {
                c.onMousePress(e, Point(mPos.x - p.x.toInt(), mPos.y - p.y.toInt()))
                break
            }
        }
    }

    /**
     * Calls [View]'s implementation of [onMouseRelease] then checks to find which child is being released on. Finally,
     * it calls [onMouseRelease] of that child.
     */
    override fun onMouseRelease(e: MouseEvent, mPos: Point) {
        super.onMouseRelease(e, mPos)
        // check the bounds of each child view and see if it fits
        for(i in 0 until orderedChildren.size) {
            val c = orderedChildren[i] ?: continue
            val p = childCoords[i]!!
            // check if it fits betweens p and c.layoutSize
            if(mPos.x >= p.x && mPos.x <= p.x + c.layoutSize.w &&
                    mPos.y >= p.y && mPos.y <= p.y + c.layoutSize.h) {
                c.onMouseRelease(e, Point(mPos.x - p.x.toInt(), mPos.y - p.y.toInt()))
                break
            }
        }
    }

    /**
     * The last [View] that the moused was moved/dragged on.
     */
    private var previousViewMousedOn = -1

    /**
     * Calls [View]'s implementation of [onMouseMoved] then checks to find which child is being moved on. Finally,
     * it calls [onMouseMoved] of that child.
     */
    override fun onMouseMoved(e: MouseEvent, mPos: Point) {
        super.onMouseMoved(e, mPos)
        var viewMouseOn = -1
        // check the bounds of each child view and see if it fits
        for(i in 0 until orderedChildren.size) {
            val c = orderedChildren[i] ?: continue
            val p = childCoords[i]!!
            // check if it fits betweens p and c.layoutSize
            if(mPos.x >= p.x && mPos.x <= p.x + c.layoutSize.w &&
                    mPos.y >= p.y && mPos.y <= p.y + c.layoutSize.h) {
                if(previousViewMousedOn != i) {
                    if(previousViewMousedOn != -1) {
                        orderedChildren[i]!!.onMouseExit(e, mPos)
                    }

                    c.onMouseEnter(e, mPos)
                    previousViewMousedOn = i
                }

                viewMouseOn = i
                c.onMouseMoved(e, Point(mPos.x - p.x.toInt(), mPos.y - p.y.toInt()))
                break
            }
        }

        if(viewMouseOn == -1 && previousViewMousedOn != -1) {
            // call on exit on that
            orderedChildren[previousViewMousedOn]?.onMouseExit(e, mPos)
            previousViewMousedOn = -1
        }
    }

    /**
     * Calls [View]'s implementation of [onMouseDragged] then checks to find which child is being dragged on. Finally,
     * it calls [onMouseDragged] of that child.
     */
    override fun onMouseDragged(e: MouseEvent, mPos: Point) {
        super.onMouseDragged(e, mPos)
        var viewMouseOn = -1
        // check the bounds of each child view and see if it fits
        for(i in 0 until orderedChildren.size) {
            val c = orderedChildren[i] ?: continue
            val p = childCoords[i]!!
            // check if it fits betweens p and c.layoutSize
            if(mPos.x >= p.x && mPos.x <= p.x + c.layoutSize.w &&
                    mPos.y >= p.y && mPos.y <= p.y + c.layoutSize.h) {
                if(previousViewMousedOn != i) {
                    if(previousViewMousedOn != -1) {
                        orderedChildren[i]!!.onMouseExit(e, mPos)
                    }

                    c.onMouseEnter(e, mPos)
                    previousViewMousedOn = i
                }

                viewMouseOn = i
                c.onMouseDragged(e, Point(mPos.x - p.x.toInt(), mPos.y - p.y.toInt()))
                break
            }
        }

        if(viewMouseOn == -1 && previousViewMousedOn != -1) {
            // call on exit on that
            orderedChildren[previousViewMousedOn]?.onMouseExit(e, mPos)
            previousViewMousedOn = -1
        }
    }

    /**
     * Calls [View]'s implementation of [onMouseExit] then checks to find which child is being exited on. Finally,
     * it calls [onMouseExit] of that child.
     */
    override fun onMouseExit(e: MouseEvent, mPos: Point) {
        super.onMouseExit(e, mPos)
        if(previousViewMousedOn != -1)
            orderedChildren[previousViewMousedOn]!!.onMouseExit(e, mPos)
        previousViewMousedOn = -1
    }

    /**
     * Calls [onKeyTyped] on all the children that are focused.
     */
    override fun onKeyTyped(e: KeyEvent) {
        super.onKeyTyped(e)

        for(c in children)
            if(c.state == State.STATE_FOCUSED)
                c.onKeyTyped(e)
    }

    /**
     * Calls [onKeyPressed] on all the children that are focused.
     */
    override fun onKeyPressed(e: KeyEvent) {
        super.onKeyPressed(e)

        for(c in children)
            if(c.state == State.STATE_FOCUSED)
                c.onKeyPressed(e)
    }

    /**
     * Calls [onKeyReleased] on all the children that are focused.
     */
    override fun onKeyReleased(e: KeyEvent) {
        super.onKeyReleased(e)

        for(c in children)
            if(c.state == State.STATE_FOCUSED)
                c.onKeyReleased(e)
    }

    private var currTraverse = -1

    /**
     * Traverses the Views within this layout.
     */
    override fun onTabTraversal(): Boolean {
        val occn = orderedChildren.filterNotNull()
        // check the current traversal
        if(currTraverse < occn.size) {
            if(currTraverse == -1) {
                onStateChanged(state, State.STATE_HOVER)
                currTraverse = 0
            }

            if(occn[currTraverse].onTabTraversal()) {
                debug("[RelativeLayout] Child finished traversal, incrementing!")
                // increment the traversal
                currTraverse++

                return onTabTraversal()
            } else {
                debug("[RelativeLayout] Child is traversing!")
            }

            return false
        } else {
            debug("[RelativeLayout] Finished the traversal!")
            currTraverse = -1
            onStateChanged(state, State.STATE_NORMAL)
            return true
        }
    }

    /**
     * Draws this [ViewGroup] and its children [View].
     */
    override fun onDraw(g: Graphics2D) {
        if(visibility != VISIBILITY_VISIBLE)
            return

        val clipBounds = g.clip
        super.onDraw(g)

        for(c in children.sorted()) {
            val p = childCoords[orderedChildren.indexOf(c)]!!
            // translate it
            g.translate( p.x.toDouble() + c.translationX.toDouble(), p.y.toDouble() + c.translationY.toDouble())
            // intersect the clip
            g.clipRect(0 - c.overClipLeft.toInt(), 0 - c.overClipTop.toInt(), c.layoutSize.w.toInt() + c.overClipRight.toInt() + c.overClipLeft.toInt(), c.layoutSize.h.toInt() + c.overClipBottom.toInt() + c.overClipTop.toInt())
            c.onDraw(g)
            g.translate( -p.x.toDouble() - c.translationX.toDouble(), -p.y.toDouble() - c.translationY.toDouble())
            // reset the clip
            g.clip = clipBounds
        }

        if(DEBUG_LAYOUT_BOXES) {
            g.color = Color.RED
            g.drawRect(0, 0, layoutSize.w.toInt(), layoutSize.h.toInt())
        }

        g.clip = clipBounds
    }
}