package com.asdev.libjam.md.view

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.Drawable
import com.asdev.libjam.md.layout.GenericLayoutParamList
import com.asdev.libjam.md.layout.LayoutParams
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.thread.MESSAGE_REQUEST_LAYOUT
import com.asdev.libjam.md.thread.MESSAGE_REQUEST_REPAINT
import com.asdev.libjam.md.thread.Message
import com.asdev.libjam.md.thread.myLooper
import com.asdev.libjam.md.util.DEBUG
import com.asdev.libjam.md.util.DIM_UNLIMITED
import com.asdev.libjam.md.util.DIM_UNSET
import com.asdev.libjam.md.util.FloatDim
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.MouseEvent

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.view
 */

/**
 * Flag for [View] parameter. This is to signify that the view is visible and it should be drawn.
 */
val VISIBILITY_VISIBLE = 0

/**
 * Flag for [View] parameter. This is to signify that the view is invisible and it should not be drawn.
 */
val VISIBILITY_INVISIBLE = 1

/**
 * An open class that defines a View. A View is simply a lightweight widget which all other widgets originate from. It
 * implements the bare minimum functionality, allowing for easy extensibility.
 */
open class View: Comparable<View> {

    /**
     * Compares this view against the other view based on the z-index.
     */
    override fun compareTo(other: View) = zIndex.compareTo(other.zIndex)

    /**
     * The max and minimum sizes of this view. Consider the maximum size as the preferred size as the layout will always
     * try to grow the view to that size.
     */
    var maxSize = DIM_UNLIMITED
    var minSize = DIM_UNLIMITED

    /**
     * Defines the visibility of this view. May either be VISIBLE or INVISIBLE
     */
    var visibility = VISIBILITY_VISIBLE

    /**
     * The size of this view set by the layout. Will be between min and max size once a layout pass has been conducted.
     */
    var layoutSize = DIM_UNSET

    /**
     * The background drawable of this view. Defaults to the [THEME] background color.
     */
    var background: Drawable? = null // should it be ColorDrawable(THEME.getBackgroundColor())?

    /**
     * The ordering index of this view. The higher the index, the further on-top it is drawn.
     */
    var zIndex = 0

    /**
     * The mouse listener associated with this view
     */
    var mouseListener: ViewMouseListener? = null

    /**
     * Called by the layout before layout to signify that the view should determine its max and min sizes at this point.
     * @return the min and max sizes, respectively.
     */
    open fun onMeasure(result: LayoutParams): LayoutParams = result.apply { minSize = this@View.minSize; maxSize = this@View.maxSize; applyAdditional(paramList); }

    /**
     * Called when the layout has determined the size of this layout
     */
    open fun onLayout(newSize: FloatDim) {
        if(DEBUG) {
            println("[View] onLayout: $newSize")
        }

        layoutSize = newSize
    }

    /**
     * Called by the [RootView] when an [Theme] change occurs.
     */
    open fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        if(background is ColorDrawable && (background as ColorDrawable).color == prevTheme.getBackgroundColor()) {
            // apply the new background color
            (background as ColorDrawable).color = newTheme.getBackgroundColor()
        }
    }

    /**
     * The addition parameter list ([GenericLayoutParamList]) attached to this view. Will be applied in [onMeasure].
     */
    private var paramList: GenericLayoutParamList? = null

    /**
     * Applies the given layout parameters. Can be taken by onMeasure
     */
    open fun applyLayoutParameters(params: GenericLayoutParamList) {
        if(DEBUG) {
            println("[View] Got new layout parameters: $params")
        }

        paramList = params
    }

    /**
     * Async flag for requesting a layout. Should be used internally only.
     */
    private var flagRequestingLayout = false
    /**
     * Async flag for requesting a repaint. Should be used internally only.
     */
    private var flagRequestingRepaint = false

    /**
     * Sends a message to the root view to request a re-layout.
     */
    fun requestLayout() {
        if(DEBUG) {
            println("[View] Layout requested...")
        }
        // flag for execution on loop thread
        flagRequestingLayout  = true
    }

    /**
     * Requests a repaint (to the [RootView]) of the entire frame. It broadcasts a message via the UI [Looper] to the
     * [RootView].
     */
    fun requestRepaint() {
        if(DEBUG)
            println("[View] Repaint requested...")
        // flag repaint on ui thread
        flagRequestingRepaint = true
    }

    /**
     * Called when the mouse is pressed within this [View]'s bounds.
     */
    open fun onMousePress(e: MouseEvent, mPos: Point) = mouseListener?.onMousePress(e, mPos)

    /**
     * Called when the mouse is released within this [View]'s bounds.
     */
    open fun onMouseRelease(e: MouseEvent, mPos: Point) = mouseListener?.onMouseRelease(e,  mPos)

    /**
     * Called when the mouse is moved within this [View]'s bounds.
     */
    open fun onMouseMoved(e: MouseEvent, mPos: Point) = mouseListener?.onMouseMoved(e, mPos)

    /**
     * Called when the mouse is dragged within this [View]'s bounds.
     */
    open fun onMouseDragged(e: MouseEvent, mPos: Point) = mouseListener?.onMouseDragged(e, mPos)

    /**
     * Called when the mouse enters this [View]'s bounds.
     */
    open fun onMouseEnter(e: MouseEvent, mPos: Point) = mouseListener?.onMouseEnter(e, mPos)

    /**
     * Called when the mouse exits this [View]'s bounds.
     */
    open fun onMouseExit(e: MouseEvent, mPos: Point) = mouseListener?.onMouseExit(e, mPos)

    /**
     * A function called on the UI thread 60 times a second to update this view.
     */
    open fun loop() {
        if(flagRequestingLayout) {
            // send message to parent on my looper
            sendMessageToRoot(MESSAGE_REQUEST_LAYOUT)
            flagRequestingLayout = false
        }

        if(flagRequestingRepaint) {
            sendMessageToRoot(MESSAGE_REQUEST_REPAINT)
            flagRequestingRepaint = false
        }
    }

    /**
     * Sends a message to the root view. MUST be run on the ui looper/thread.
     */
    fun sendMessageToRoot(msg: Message) {
        // post it if the current looper isn't null
        myLooper()?.postMessage(msg)
    }

    /**
     * Called when to draw the view. Graphics should be clipped and translated to this view. E.g. the origin of this
     * view should be 0, 0 when drawing something
     */
    open fun onDraw(g: Graphics2D) {
        // check the visibility of this view
        if(visibility != VISIBILITY_VISIBLE)
            return

        // draw the background
        background?.draw(g, 0f, 0f, layoutSize.w, layoutSize.h)
    }

    interface ViewMouseListener {

        fun onMousePress(e: MouseEvent, p: Point)
        fun onMouseRelease(e: MouseEvent, p: Point)
        fun onMouseDragged(e: MouseEvent, p: Point)
        fun onMouseMoved(e: MouseEvent, p: Point)
        fun onMouseEnter(e: MouseEvent, p: Point)
        fun onMouseExit(e: MouseEvent, p: Point)
    }
}