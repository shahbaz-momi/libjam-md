package com.asdev.libjam.md.layout

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.DIM_UNLIMITED
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.util.FloatPoint
import com.asdev.libjam.md.view.View
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

/**
 * Created by Asdev on 11/21/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

// very similiar impl to recycler view
// use a interface as an adapter
// get number of items
// bind item to view
// construct view
// destroy view - if caching is implemented

/**
 * A class that accepts a ListLayoutAdapter and lays out the children in a list fashion. Normally, you should wrap this
 * in a ScrollLayout to add scrolling functionality.
 */
class ListLayout (val listLayoutAdapter: ListLayoutAdapter, val itemDividers: Boolean = false): View() {

    /**
     * The underlying layout which manages the dimensions of the children.
     */
    private val layout = LinearLayout()

    init {
        notifyDatasetChanged()
    }

    /**
     * Invalidates the data within this list and causes a reset to occur
     *
     */
    fun notifyDatasetChanged() {
        // clear the layout of its children
        layout.removeAllChildren()
        // construct the children and add it
        for(i in 0 until listLayoutAdapter.getItemCount()) {
            val v = listLayoutAdapter.constructView(i)
            listLayoutAdapter.bindView(this, v, i)
            // add it to the layout
            layout.addChild(v)
            if(i != listLayoutAdapter.getItemCount() - 1 && itemDividers) {
                // add divider
                val div = View()
                div.background = ColorDrawable(THEME.getDividerColor())
                div.minSize = FloatDim(DIM_UNLIMITED.w, 1f)
                div.maxSize = div.minSize
                layout.addChild(div)
            }
        }
    }

    override fun onKeyTyped(e: KeyEvent) {
        super.onKeyTyped(e)
        layout.onKeyTyped(e)
    }

    override fun onKeyPressed(e: KeyEvent) {
        super.onKeyPressed(e)
        layout.onKeyPressed(e)
    }

    override fun onKeyReleased(e: KeyEvent) {
        super.onKeyReleased(e)
        layout.onKeyReleased(e)
    }

    override fun onScroll(e: MouseWheelEvent) {
        super.onScroll(e)
        layout.onScroll(e)
    }

    override fun onMeasure(result: LayoutParams): LayoutParams {
        val lpUs = super.onMeasure(result)

        val lp = layout.onMeasure(result)

        // check if the child min size is bigger or the max size is smaller
        if(lpUs.minSize.w > 0f && lpUs.minSize.w > lp.minSize.w) {
            result.minSize.w = lpUs.minSize.w
        }
        if(lpUs.minSize.h > 0f && lpUs.minSize.h > lp.minSize.h) {
            result.minSize.h = lpUs.minSize.h
        }
        if(lpUs.maxSize.w > 0f && lpUs.maxSize.w < lp.maxSize.w) {
            result.maxSize.w = lpUs.maxSize.w
        }
        if(lpUs.maxSize.h > 0f && lpUs.maxSize.h < lp.maxSize.h) {
            result.maxSize.h = lpUs.maxSize.h
        }

        return result
    }

    override fun onLayout(newSize: FloatDim) {
        super.onLayout(newSize)
        layout.onLayout(newSize)
    }

    override fun onMouseDragged(e: MouseEvent, mPos: Point) {
        super.onMouseDragged(e, mPos)
        layout.onMouseDragged(e, mPos)
    }

    override fun onMouseEnter(e: MouseEvent, mPos: Point) {
        super.onMouseEnter(e, mPos)
        layout.onMouseEnter(e, mPos)
    }

    override fun onMouseRelease(e: MouseEvent, mPos: Point) {
        super.onMouseRelease(e, mPos)
        layout.onMouseRelease(e, mPos)
    }

    override fun onMouseMoved(e: MouseEvent, mPos: Point) {
        super.onMouseMoved(e, mPos)
        layout.onMouseMoved(e, mPos)
    }

    override fun onMouseExit(e: MouseEvent, mPos: Point) {
        super.onMouseExit(e, mPos)
        layout.onMouseExit(e, mPos)
    }

    override fun onMousePress(e: MouseEvent, mPos: Point) {
        super.onMousePress(e, mPos)
        layout.onMousePress(e, mPos)
    }

    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)
        layout.onThemeChange(prevTheme, newTheme)
    }

    override fun onTabTraversal(): Boolean {
        super.onTabTraversal()
        return layout.onTabTraversal()
    }

    override fun loop() {
        super.loop()
        layout.loop()
    }

    /**
     * Finds the position of the specified child relative to this View.
     */
    fun findChildPosition(child: View) = layout.findChildPosition(child)

    override fun onDraw(g: Graphics2D) {
        super.onDraw(g)
        layout.onDraw(g)
    }

    override fun onPostDraw(g: Graphics2D) {
        layout.onPostDraw(g)
        super.onPostDraw(g)
    }
}

/**
 * The base adapter for a list layout. Constructs and binds data to the Views in the layout.
 */
interface ListLayoutAdapter {

    /**
     * Returns the number of items within this adapter.
     */
    fun getItemCount(): Int

    /**
     * Called when the binding of the data at the index to the provided view is requested.
     */
    fun bindView(
            /**
             * The ListLayout itself.
             */
            parent: ListLayout,
            /**
             * The view to bind the data with.
             */
            view: View,
            /**
             * The index of the data item.
             */
            index: Int)

    /**
     * Called when the construction of a View is required. This view will be displayed for the specified index.
     */
    fun constructView(index: Int): View

    // fun destroyView // later for view recycling
}