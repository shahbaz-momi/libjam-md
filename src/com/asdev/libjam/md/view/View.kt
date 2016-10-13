package com.asdev.libjam.md.view

import com.asdev.libjam.md.drawable.Drawable
import com.asdev.libjam.md.layout.GenericLayoutParamList
import com.asdev.libjam.md.layout.LayoutParams
import com.asdev.libjam.md.thread.MESSAGE_REQUEST_LAYOUT
import com.asdev.libjam.md.thread.MESSAGE_REQUEST_REPAINT
import com.asdev.libjam.md.thread.Message
import com.asdev.libjam.md.thread.myLooper
import com.asdev.libjam.md.util.DEBUG
import com.asdev.libjam.md.util.DIM_UNLIMITED
import com.asdev.libjam.md.util.DIM_UNSET
import com.asdev.libjam.md.util.FloatDim
import java.awt.Graphics2D

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.view
 */

val VISIBILITY_VISIBLE = 0
val VISIBILITY_INVISIBLE = 1

open class View {

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
     * The background drawable of this view.
     */
    var background: Drawable? = null

    /**
     * Called by the layout before layout to signify that the view should determine its max and min sizes at this point.
     * @return the min and max sizes, respectively.
     */
    open fun onMeasure(result: LayoutParams): LayoutParams = result.apply { minSize = this@View.minSize; maxSize = this@View.maxSize; applyAdditional(paramList) }

    /**
     * Called when the layout has determined the size of this layout
     */
    open fun onLayout(newSize: FloatDim) {
        if(DEBUG) {
            println("[View] onLayout: $newSize")
        }

        layoutSize = newSize
    }

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

    private var flagRequestingLayout = false
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

    fun requestRepaint() {
        if(DEBUG)
            println("[View] Repaint requested...")
        // flag repaint on ui thread
        flagRequestingRepaint = true
    }

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
}