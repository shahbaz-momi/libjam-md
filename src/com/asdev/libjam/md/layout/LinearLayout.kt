package com.asdev.libjam.md.layout
import com.asdev.libjam.md.util.*
import com.asdev.libjam.md.view.VISIBILITY_VISIBLE
import com.asdev.libjam.md.view.View
import com.asdev.libjam.md.xml.XMLParamList
import res.R
import java.awt.Color
import java.awt.Graphics2D
import java.util.*

/**
 * Created by Asdev on 10/07/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

/**
 * The list items are vertical.
 */
val ORIENTATION_VERTICAL = 0

/**
 * The list items are horizontal (default).
 */
val ORIENTATION_HORIZONTAL = 1

/**
 * A layout that lays view one after another (a linear fashion). The items may be vertical or horizontal, and this is
 * specified by the orientation. Directly inherits [ViewGroup].
 */
open class LinearLayout: ViewGroup() {

    private val children = ArrayList<View>()
    private lateinit var childrenSorted: List<View>
    private lateinit var childrenCoords: Array<FloatPoint>
    private lateinit var childrenLP: Array<LinearLayoutParams>

    /**
     * The orientation in which to layout out the items. May be either [ORIENTATION_VERTICAL] or [ORIENTATION_HORIZONTAL]
     */
    private var orientation = ORIENTATION_HORIZONTAL

    override fun applyParameters(params: GenericParamList) {
        super.applyParameters(params)

        if(params is XMLParamList) {
            if(params.hasParam(R.attrs.LinearLayout.orientation)) {
                val orientation = if(params.getString(R.attrs.LinearLayout.orientation) == "horizontal") ORIENTATION_HORIZONTAL else ORIENTATION_VERTICAL
                setOrientation(orientation)
            }
        }
    }

    /**
     * Set the orientation of this view.
     * $orientation the new orientation of this layout. May be either [ORIENTATION_VERTICAL] or [ORIENTATION_HORIZONTAL]
     */
    fun setOrientation(orientation: Int) {
        this.orientation = orientation
        super.requestLayout()
    }

    /**
     * Removes the specified child.
     * $child the child [View] to remove.
     */
    override fun removeChild(child: View) {
        children.remove(child)
        super.requestLayout()
    }

    /**
     * Removes all of the children from this layout.
     */
    fun removeAllChildren() {
        children.clear()
        super.requestLayout()
    }

    /**
     * Returns the children managed by this layout.
     */
    override fun getChildren() = children.toTypedArray()

    override fun getChildCount() = children.size

    /**
     * Adds the specified child to this layout.
     * $child the child [View] to add.
     */
    override fun addChild(child: View) {
        children.add(child)
        // request a layout to accomodate for the new view
        super.requestLayout()
    }

    /**
     * Adds the specified child to this layout at the given index.
     */
    fun addChildAtIndex(view: View, index: Int) {
        children.add(index, view)
        super.requestLayout()
    }

    /**
     * Measures this layout and determines the minimum and maximum sizes along with additional parameters.
     * This layout passes [LinearLayoutParams] as its [LayoutParams] type.
     */
    override fun onMeasure(result: LayoutParams): LayoutParams {
        // measure each view to determine the min size
        // the order of the children determines they are shown

        childrenLP = Array(children.size){ newLinearLayoutParams() }

        var minW = 0f
        var minH = 0f

        for((index, c) in children.withIndex()) {
            val lp = c.onMeasure(childrenLP[index]) as? LinearLayoutParams ?: throw IllegalStateException("Children of LinearLayout must return LinearLayoutParams in onMeasure(). This was not the case.")

            if (c.minSize.w > 0f) {
                if(orientation == ORIENTATION_HORIZONTAL) {
                    if(c.minSize.w > minW)
                        minW = c.minSize.w
                } else if(orientation == ORIENTATION_VERTICAL) {
                    minW += c.minSize.w
                }
            }

            if(c.minSize.h > 0f) {
                if(orientation == ORIENTATION_HORIZONTAL) {
                    minH += c.minSize.h
                } else if(orientation == ORIENTATION_VERTICAL) {
                    if(c.minSize.h > minH)
                        minH = c.minSize.h
                }
            }

            childrenLP[index] = lp
        }

        // we know know our minimum dimensions. Maximum will be the same but it must be greater than the minimum
        // minimum can stay the same as well but only if it is greater than the computed minimum
        if(minSize.w > minW)
            minW = minSize.w
        if(minSize.h > minH)
            minH = minSize.h

        minSize = FloatDim(minW, minH)

        if(DEBUG)
            println("[LinearLayout] Computed a minimum size of $minSize")

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

        return super.onMeasure(result)
    }

    /**
     * Lays out this [ViewGroup]. Will determine the new sizes and positions of the attached [View] children.
     */
    override fun onLayout(newSize: FloatDim) {
        // clear the previous positions
        childrenCoords = Array(children.size){ POINT_UNSET }

        // get the difference from the minSize
        val diffW = (newSize.w - minSize.w) / children.size
        val diffH = (newSize.h - minSize.h) / children.size

        var diffHOverflow = 0f
        var diffWOverflow = 0f

        val skipIndicesW = ArrayList<Int>()
        val skipIndicesH = ArrayList<Int>()

        val sizes = Array(children.size) { DIM_UNSET.copy() }

        // iterate over each view
        // the order of the children is the order they are drawn
        for(i in 0 until children.size) {
            val c = children[i]
            // x and y are a given to be 0
            var w = DIM_UNSET.w
            var h = DIM_UNSET.h

            var x = POINT_UNSET.x
            var y = POINT_UNSET.y

            if(orientation == ORIENTATION_VERTICAL) {
                y = 0f

                if(i > 0) {
                    // take the x of the previous and add the size
                    x = childrenCoords[i - 1].x + sizes[i - 1].w
                } else {
                    x = 0f
                }

                h = newSize.h
                w = Math.max(c.minSize.w, 0f) + diffW
            } else if(orientation == ORIENTATION_HORIZONTAL) {
                x = 0f

                if(i > 0) {
                    y = childrenCoords[i - 1].y + sizes[i - 1].h
                } else {
                    y = 0f
                }

                w = newSize.w
                h = Math.max(c.minSize.h, 0f) + diffH
            }

            // check the sizes to see if they fit. If they don't, then apply the gravity
            if(w > c.maxSize.w && c.maxSize.w > 0f) {

                if(orientation == ORIENTATION_HORIZONTAL)
                    x = calculateXComp(childrenLP[i].gravity, x, w, c.maxSize.w)

                // diffW += (w - c.maxSize.w) / (children.size - i - 1)
                if(orientation == ORIENTATION_VERTICAL)
                    diffWOverflow += (w - c.maxSize.w)

                skipIndicesW.add(i)

                w = c.maxSize.w
            }

            if(h > c.maxSize.h && c.maxSize.h > 0f) {

                if(orientation == ORIENTATION_VERTICAL)
                    y = calculateYComp(childrenLP[i].gravity, y, h, c.maxSize.h)

                // diffH += (h - c.maxSize.h) / (children.size - i - 1)
                if(orientation == ORIENTATION_HORIZONTAL)
                    diffHOverflow += (h - c.maxSize.h)

                skipIndicesH.add(i)

                h = c.maxSize.h
            }

            sizes[i] = FloatDim(w, h)
            childrenCoords[i] = (FloatPoint(x, y))
        }

        if(DEBUG) {
            println("[LinearLayout] Overflowed H $diffHOverflow and W $diffWOverflow")
        }

        var attempts = 0

        // add the height overflow back in
        while(diffHOverflow >= 0.1f && attempts < 10) {
            val gain = diffHOverflow / (children.size - skipIndicesH.size)

            for((i, c) in children.withIndex()) {
                // skip the ones that are already max size
                if(skipIndicesH.contains(i))
                    continue

                var shift = gain

                diffHOverflow -= gain

                // apply the gain
                val newH = sizes[i].h + gain
                // check to see if it is over the max size
                if(newH > c.maxSize.h && c.maxSize.h > 0f) {
                    shift = gain - (newH - c.maxSize.h)
                    // add the difference back in for next round
                    diffHOverflow += shift
                    // set the size to max size
                    sizes[i].h = c.maxSize.h
                } else {
                    sizes[i].h = newH
                }

                // shift the y coords of all the following views by the gain
                for(j in i + 1 until children.size) {
                    childrenCoords[j].y += shift
                }

            }

            attempts ++
        }

        attempts = 0

        // add the width overflow back in
        while(diffWOverflow >= 0.1f && attempts < 10) {
            val gain = diffWOverflow / (children.size - skipIndicesW.size)

            for((i, c) in children.withIndex()) {
                // skip the ones that are already max size
                if(skipIndicesW.contains(i))
                    continue

                var shift = gain

                diffWOverflow -= gain

                // apply the gain
                val newW = sizes[i].w + gain
                // check to see if it is over the max size
                if(newW > c.maxSize.w && c.maxSize.w > 0f) {
                    shift = gain - (newW - c.maxSize.w)
                    // add the difference back in for next round
                    diffWOverflow += shift
                    // set the size to max size
                    sizes[i].w = c.maxSize.w
                } else {
                    sizes[i].w = newW
                }

                // shift the y coords of all the following views by the gain
                for(j in i + 1 until children.size) {
                    childrenCoords[j].x += shift
                }
            }
        }

        for((i, c) in children.withIndex()) {
            c.onLayout(sizes[i])
        }

        childrenSorted = children.sortedWith(Comparator { o1, o2 -> o1.zIndex.compareTo(o2.zIndex) })

        super.onLayout(newSize)
    }

    /**
     * Draws this layout along with it's children. Will clip and translate the canvas to the child view's bounds.
     */
    override fun onDraw(g: Graphics2D) {
        if(visibility != VISIBILITY_VISIBLE)
            return

        val clipBounds = g.clip
        super.onDraw(g)

        // draw the children by z order
        for(index in childrenSorted.indices) {
            val c = childrenSorted[index]
            val i = children.indexOf(c)
            // add the translation of the views
            g.translate(childrenCoords[i].x.toDouble() + c.translationX.toDouble(), childrenCoords[i].y.toDouble() + c.translationY.toDouble())
            // intersect the child clip
            g.clipRect(0 - c.overClipLeft.toInt(), 0 - c.overClipTop.toInt(), c.layoutSize.w.toInt() + c.overClipRight.toInt() + c.overClipLeft.toInt(), c.layoutSize.h.toInt() + c.overClipBottom.toInt() + c.overClipTop.toInt())
            c.onDraw(g)
            g.translate(-childrenCoords[i].x.toDouble() - c.translationX.toDouble(), -childrenCoords[i].y.toDouble() - c.translationY.toDouble())

            // reset the clip bounds again
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

        // draw the children by z order
        for(index in childrenSorted.indices) {
            val c = childrenSorted[index]
            val i = children.indexOf(c)
            // add the translation of the views
            g.translate(childrenCoords[i].x.toDouble() + c.translationX.toDouble(), childrenCoords[i].y.toDouble() + c.translationY.toDouble())
            // intersect the child clip
            c.onPostDraw(g)
            g.translate(-childrenCoords[i].x.toDouble() - c.translationX.toDouble(), -childrenCoords[i].y.toDouble() - c.translationY.toDouble())
        }

        if(DEBUG_LAYOUT_BOXES) {
            g.color = Color.RED
            g.drawRect(0, 0, layoutSize.w.toInt(), layoutSize.h.toInt())
        }

        super.onPostDraw(g)
    }

    override fun findChildPosition(child: View): FloatPoint? {
        val index = children.indexOf(child)

        // make sure that the child is part of this layout
        if(index == -1) {
            return null
        }

        return childrenCoords[index]
    }
}