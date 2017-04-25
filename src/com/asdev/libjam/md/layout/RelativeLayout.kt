package com.asdev.libjam.md.layout

import com.asdev.libjam.md.util.*
import com.asdev.libjam.md.view.VISIBILITY_VISIBLE
import com.asdev.libjam.md.view.View
import java.awt.Color
import java.awt.Graphics2D
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
 * A [ViewGroup] that manages childCache in a grid-like fashion.
 */
class RelativeLayout: ViewGroup() {

    /**
     * The temporary list of childCache managed by this [ViewGroup]
     */
    private val childCache = ArrayList<View>()

    /**
     * The childCache of this [ViewGroup] in order of their gravity.
     */
    private val children = Array<View?>(9){null}

    /**
     * The cache of [LayoutParams] of each child [View]. In order of the child's gravity.
     */
    private val params = Array<RelativeLayoutParams?>(9) {null}

    /**
     * The coordinates of each child, in order of the child's gravity.
     */
    private val positions = Array(9) { FloatPoint(0f, 0f) }

    /**
     * The sizes of each individual child.
     */
    private val sizes = Array(9) { DIM_UNSET.copy() }

    override fun removeChild(child: View) {
        childCache.remove(child)
        requestLayout()
    }

    override fun getChildren() = childCache.toTypedArray()

    override fun addChild(child: View) {
        if(DEBUG) {
            println("[RelativeLayout] Adding child: $child")
        }
        // post a request layout to the root view
        requestLayout()
        childCache.add(child)
    }

    override fun loop() {
        super.loop()
    }

    /**
     * Measures it self and its children.
     */
    override fun onMeasure(result: LayoutParams): LayoutParams {
        children.fill(null)
        params.fill(null)

        // measure each individual child
        for(i in childCache.indices) {
            val child = childCache[i]

            val params = child.onMeasure(newRelativeLayoutParams()) as? RelativeLayoutParams?: throw IllegalArgumentException("Children of RelativeLayout must return RelativeLayoutParams in onMeasure()")

            // put in the actual array according to the gravity
            if(children[params.gravity] != null)
                throw IllegalArgumentException("Duplicate child for the same gravity at ${params.gravity}")

            children[params.gravity] = child
            this.params[params.gravity] = params
        }

        // determine the min and max sizes
        var minW = 0f

        var temp = 0f

        for(i in GRAVITY_TOP_LEFT .. GRAVITY_TOP_RIGHT) {
            val c = children[i] ?: continue
            if(c.minSize.w > 0f)
                temp += c.minSize.w
        }

        minW = maxOf(minW, temp)

        temp = 0f

        for(i in GRAVITY_MIDDLE_LEFT .. GRAVITY_MIDDLE_RIGHT) {
            val c = children[i] ?: continue
            if(c.minSize.w > 0f)
                temp += c.minSize.w
        }

        minW = maxOf(minW, temp)

        temp = 0f

        for(i in GRAVITY_BOTTOM_LEFT .. GRAVITY_BOTTOM_RIGHT) {
            val c = children[i] ?: continue
            if(c.minSize.w > 0f)
                temp += c.minSize.w
        }

        minW = maxOf(minW, temp)

        var minH = 0f
        temp = 0f

        for(i in GRAVITY_ARRAY_ROW_LEFT) {
            val c = children[i] ?: continue
            if(c.minSize.h > 0f)
                temp += c.minSize.h
        }

        minH = maxOf(minH, temp)

        temp = 0f

        for(i in GRAVITY_ARRAY_ROW_MIDDLE) {
            val c = children[i] ?: continue
            if(c.minSize.h > 0f)
                temp += c.minSize.h
        }

        minH = maxOf(minH, temp)

        temp = 0f

        for(i in GRAVITY_ARRAY_ROW_RIGHT) {
            val c = children[i] ?: continue
            if(c.minSize.h > 0f)
                temp += c.minSize.h
        }

        minH = maxOf(minH, temp)

        // check if the current min size is bigger than the calculated one
        if(minSize.w > minW)
            minW = minSize.w
        if(minSize.h > minH)
            minH = minSize.h

        minSize.w = minW
        minSize.h = minH

        // no max size constraint by us, but make sure it is at least the min size if it is set
        if(maxSize.w > 0f)
            maxSize.w = maxOf(maxSize.w, minSize.w)
        if(maxSize.h > 0f)
            maxSize.h = maxOf(maxSize.h, minSize.h)

        return super.onMeasure(result)
    }

    override fun onLayout(newSize: FloatDim) {
        layoutRow(newSize, getRowRange(0))
        layoutRow(newSize, getRowRange(1))
        layoutRow(newSize, getRowRange(2))

        // calculate the number of rows that actually contain children
        val numRows = (0..2).count{ getRowRange(it).count { children[it] != null } > 0 }


        val rowHeights = Array(3) {0f}

        // calculate the maximum size of each row (the smallest max of the children)
        val maxRowHeights = Array(3) {
            val range = getRowRange(it)
            var max = 0f
            for(i in range) {
                val c = children[i]?: continue
                if((c.maxSize.h < max || max == 0f) && c.maxSize.h > 0f)
                    max = c.maxSize.h
            }

            max
        }

        val minRowHeights = Array(3) {
            val range = getRowRange(it)
            var min = 0f
            for(i in range) {
                val c = children[i]?: continue
                if(c.minSize.h > min)
                    min = c.minSize.h
            }

            min
        }

        // check the size remaining after accounting for the minimum size
        val remainingHeight = newSize.h - minRowHeights.sum()
        val p = remainingHeight / numRows
        for(i in 0..2) {
            rowHeights[i] = minRowHeights[i] + p
        }

        var overflow = 0f
        var count = numRows
        // clamp each row to the maximum size
        for(attempts in 0 until 10) {
            val perRow = overflow / count

            for(i in 0..2) {
                if(rowHeights[i] == maxRowHeights[i] && maxRowHeights[i] > 0f) {
                    continue
                }

                if(getRowRange(i).count { children[it] != null } == 0)
                    continue

                rowHeights[i] += perRow
                overflow -= perRow

                if(rowHeights[i] > maxRowHeights[i] && maxRowHeights[i] > 0f) {
                    count --
                    // add the difference back into the overflow
                    overflow += rowHeights[i] - maxRowHeights[i]
                    rowHeights[i] = maxRowHeights[i]
                    break
                }
            }

            if(overflow == 0f)
                break
        }

        // set the actual sizes and positions of the views while preventing overlap
        var height = 0f
        for(i in 0..2) {
            // make sure there are children in this row
            if(getRowRange(i).count { children[it] != null } == 0)
                continue

            for(j in getRowRange(i)) {
                children[j]?: continue
                sizes[j].h = rowHeights[i]
                positions[j].y = calculateYComp(params[j]!!.gravity, 0f, newSize.h, sizes[j].h)
            }

            // stop the overlap
            if(i > 0) {
                if(getRowRange(i - 1).count { children[it] != null } == 0)
                    continue

                for(j in getRowRange(i)) {
                    if(positions[j].y < height) {
                        positions[j].y = height
                    }
                }
            }

            height += rowHeights[i]

            // make it doesn't overlap into the next row
            if(i < 2) {
                if(getRowRange(i + 1).count { children[it] != null } == 0)
                    continue

                for(j in getRowRange(i)) {
                    if(positions[j].y + sizes[j].h > height) {
                        positions[j].y = height - sizes[j].h
                    }
                }
            }
        }

        // call on layout off all of the children
        for(i in children.indices) {
            val c = children[i]?: continue

            c.onLayout(sizes[i])
        }

        super.onLayout(newSize)
    }

    ///////// ONLY DEALS WITH WIDTHS AND X POSS ///////////
    private fun layoutRow(size: FloatDim, range: IntRange) {
        // find out the additional real estate we have
        val deltaW = size.w - maxOf(minSize.w, 0f)

        // count the number of children in this row
        val count = range.count { children[it] != null }

        // if no children, nothing to layout
        if(count == 0)
            return

        // if there is only one child, try and set it to that size
        // account for max sizes tho
        if(count == 1) {
            var w = size.w
            val index = range.first { children[it] != null }

            // find the non-null child
            val child = children[index]!!

            if(w > child.maxSize.w && child.maxSize.w > 0f)
                w = child.maxSize.w

            sizes[index].w = w
            // find the new x coord now that we have the width
            positions[index].x = calculateXComp(params[index]!!.gravity, 0f, size.w, w)
        } else {
            var overflow = deltaW

            // do the initial widths as the min width for
            for(i in range) {
                val c = children[i]?: continue
                sizes[i].w = maxOf(c.minSize.w, 0f)
            }

            var attempts = 0
            var nonMaxedViews = count

            // only try the fit a certain number of times (10) and while extra room is available
            while(overflow > 1f && attempts < 10) {
                // the individual growth per view
                val growth = overflow / nonMaxedViews

                for (i in range) {
                    val child = children[i] ?: continue

                    // skip views that already have reached their maximum size
                    if(sizes[i].w == child.maxSize.w) {
                        continue
                    }

                    // start with the current size and grow it
                    var w = sizes[i].w + growth

                    // if out of bounds, add to overflow
                    if (w > child.maxSize.w && child.maxSize.w > 0f) {
                        overflow += w - maxOf(child.maxSize.w, 0f)
                        nonMaxedViews --
                        w = child.maxSize.w
                    }

                    overflow -= growth

                    // temp store the size
                    sizes[i].w = w
                }

                attempts ++
            }

            // calculate the positions of the view
            for(i in range) {
                children[i]?: continue // make sure a view at the current space exists
                positions[i].x = calculateXComp(params[i]!!.gravity, 0f, size.w, sizes[i].w)

                // check for overlap
                if(i > range.start) {
                    if(positions[i - 1].x + sizes[i - 1].w > positions[i].x) {
                        positions[i].x = positions[i - 1].x + sizes[i - 1].w
                    }
                }
            }
        }
    }

    @Suppress("ConvertTwoComparisonsToRangeCheck")
    private fun calculateRowNumber(gravity: Int): Int {
        if(gravity >= GRAVITY_TOP_LEFT && gravity <= GRAVITY_TOP_RIGHT) {
            return 0
        } else if(gravity >= GRAVITY_MIDDLE_LEFT && gravity <= GRAVITY_MIDDLE_RIGHT) {
            return 1
        } else {
            return 2
        }
    }

    private fun getRowRange(row: Int): IntRange {
        if(row == 0)
            return GRAVITY_TOP_LEFT..GRAVITY_TOP_RIGHT
        else if(row == 1)
            return GRAVITY_MIDDLE_LEFT..GRAVITY_MIDDLE_RIGHT
        else
            return GRAVITY_BOTTOM_LEFT..GRAVITY_BOTTOM_RIGHT
    }

    override fun findChildPosition(child: View): FloatPoint? {
        val index = children.indexOf(child)

        // make sure that the child is part of this layout
        if(index == -1) {
            return null
        }

        return positions[index]
    }

    /**
     * Draws this [ViewGroup] and its childCache [View].
     */
    override fun onDraw(g: Graphics2D) {
        if(visibility != VISIBILITY_VISIBLE)
            return

        val clipBounds = g.clip
        super.onDraw(g)

        // ignore z-indexes cuz these should not draw over each other
        for(i in children.indices) {
            val c = children[i] ?: continue

            val p = positions[i]
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

    override fun onPostDraw(g: Graphics2D) {
        if(visibility != VISIBILITY_VISIBLE)
            return

        for(i in children.indices) {
            val c = children[i] ?: continue

            val p = positions[i]

            // translate it
            g.translate( p.x.toDouble() + c.translationX.toDouble(), p.y.toDouble() + c.translationY.toDouble())
            c.onPostDraw(g)
            g.translate( -p.x.toDouble() - c.translationX.toDouble(), -p.y.toDouble() - c.translationY.toDouble())
        }

        super.onPostDraw(g)
    }
}