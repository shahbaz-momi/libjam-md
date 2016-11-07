package com.asdev.libjam.md.view

import com.asdev.libjam.md.animation.Animator
import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.animation.LinearInterpolator
import com.asdev.libjam.md.drawable.AnimatedDrawable
import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.Drawable
import com.asdev.libjam.md.drawable.StatefulDrawable
import com.asdev.libjam.md.layout.GenericLayoutParamList
import com.asdev.libjam.md.layout.LayoutParams
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.thread.*
import com.asdev.libjam.md.util.*
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.KeyEvent
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
const val VISIBILITY_VISIBLE = 0

/**
 * Flag for [View] parameter. This is to signify that the view is invisible and it should not be drawn.
 */
const val VISIBILITY_INVISIBLE = 1

/**
 * The time between a mouse press and release for it to be considered a click.
 */
const val MOUSE_CLICK_TIME = 400f

/**
 * The time that it takes for a press to be considered a long press.
 */
const val MOUSE_LONG_PRESS_TIME = 900f

/**
 * An open class that defines a View. A View is simply a lightweight widget which all other widgets originate from. It
 * implements the bare minimum functionality, allowing for easy extensibility.
 */
open class View: Comparable<View> {

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
     * The background drawable of this view. Defaults to the null.
     */
    var background: Drawable? = null // should it be ColorDrawable(THEME.getBackgroundColor())?

    /**
     * The ordering index of this view. The higher the index, the further on-top it is drawn.
     */
    var zIndex = 0

    /**
     * The mouse listener associated with this [View].
     */
    var mouseListener: ViewMouseListener? = null

    /**
     * The state listener associated with this [View].
     */
    var stateListener: StateListener? = null

    /**
     * The state of the current view.
     */
    var state: State = State.STATE_NORMAL

    /**
     * The [FloatValueAnimator] for [translationX].
     */
    var translationXAnimator = FloatValueAnimator(0f, LinearInterpolator, 0f, 0f, 0f)

    /**
     * The translationX of this [View].
     */
    var translationX: Float by translationXAnimator

    /**
     * The [FloatValueAnimator] for [translationY].
     */
    var translationYAnimator = FloatValueAnimator(0f, LinearInterpolator, 0f, 0f, 0f)

    /**
     * The translationY of this [View].
     */
    var translationY: Float by translationYAnimator

    /**
     * Called when this view is clicked upon.
     */
    var onClickListener: ((MouseEvent, Point) -> Unit)? = null

    /**
     * Called when this view is long clicked upon.
     */
    var onLongClickListener: ((MouseEvent, Point) -> Unit)? = null

    /**
     * Called when this view is pressed upon.
     */
    var onPressListener: ((MouseEvent, Point) -> Unit)? = null

    /**
     * Called when this view is long pressed upon.
     */
    var onLongPressListener: ((Unit) -> Unit)? = null

    /**
     * The amount to over-clip to the left.
     */
    var overClipLeft = 0f

    /**
     * The amount to over-clip to the right.
     */
    var overClipRight = 0f

    /**
     * The amount to over-clip to the top.
     */
    var overClipTop = 0f

    /**
     * The amount to over-clip to the bottom.
     */
    var overClipBottom = 0f

    var keyListener: ViewKeyListener? = null

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

    private var mousePressTime = -1L
    private var hasLongPressed = false

    /**
     * Called when the mouse is pressed within this [View]'s bounds.
     */
    open fun onMousePress(e: MouseEvent, mPos: Point) {
        mouseListener?.onMousePress(e, mPos)
        // change the state to pressed
        onStateChanged(state, State.STATE_PRESSED)

        onPressListener?.invoke(e, mPos)

        mousePressTime = System.currentTimeMillis()
    }

    /**
     * Called when the mouse is released within this [View]'s bounds.
     */
    open fun onMouseRelease(e: MouseEvent, mPos: Point) {
        mouseListener?.onMouseRelease(e,  mPos)
        // the the state to focused
        onStateChanged(state, State.STATE_FOCUSED)

        if(System.currentTimeMillis() - mousePressTime <= MOUSE_CLICK_TIME) {
            onClickListener?.invoke(e, mPos)
        } else {
            onLongClickListener?.invoke(e, mPos)
        }

        hasLongPressed = false
    }

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
    open fun onMouseEnter(e: MouseEvent, mPos: Point) {
        mouseListener?.onMouseEnter(e, mPos)
        onStateChanged(state, State.STATE_HOVER)
    }

    /**
     * Called when the mouse exits this [View]'s bounds.
     */
    open fun onMouseExit(e: MouseEvent, mPos: Point) {
        mouseListener?.onMouseExit(e, mPos)
        onStateChanged(state, State.STATE_NORMAL)
    }

    /**
     * Called when the state of this view has changed.
     */
    open fun onStateChanged(previous: State, newState: State) {
        // call on listener
        stateListener?.onStateChanged(previous, newState)

        // call on potential stateful drawable
        if(background != null && background is StatefulDrawable)
            (background!! as StatefulDrawable).onStateChanged(this, previous, newState)

        // request a repaint to update a potential stateful drawable.
        requestRepaint()
        state = newState
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

        if(flagRequestingCursor > -1) {
            if(DEBUG)
                println("[View] Setting cursor to $flagRequestingCursor")

            sendMessageToRoot(Message(MESSAGE_TYPE_ROOT_VIEW, MESSAGE_ACTION_SET_CURSOR).apply { data0 = flagRequestingCursor })
            flagRequestingCursor = -1
        }

        // loop the animators
        translationXAnimator.loop()
        translationYAnimator.loop()

        if(translationXAnimator.isRunning() || translationYAnimator.isRunning()) {
            // request a new frame
            requestRepaint()
        }

        // request a repaint if the background is animatable and wants a frame
        if(background != null && background is AnimatedDrawable) {
            if ((background!! as AnimatedDrawable).requestFrame()) {
                requestRepaint()
            }
        }

        // check the long mouse press
        if(state == State.STATE_PRESSED && System.currentTimeMillis() - mousePressTime >= MOUSE_LONG_PRESS_TIME && !hasLongPressed) {
            onLongPressListener?.invoke(Unit)
            hasLongPressed = true
        }
    }

    /**
     * Posts the animation to the Choregrapher and starts it if not already started. MUST be run on the UI Looper for it to work.
     */
    open fun runAnimation(a: Animator, startIt: Boolean) {
        sendMessageToRoot(Message(MESSAGE_TYPE_ANIMATION, MESSAGE_ACTION_RUN_ANIMATION).apply { data0 = a })

        if(!a.isRunning() && startIt) {
            a.start()
        }
    }

    /**
     * Compares this view against the other view based on the z-index.
     */
    override fun compareTo(other: View) = zIndex.compareTo(other.zIndex)

    private var flagRequestingCursor = -1

    /**
     * Sets the current mouse cursor.
     */
    fun setCursor(cursor: Int) {
        flagRequestingCursor = cursor
    }

    /**
     * Sends a message to the root view. MUST be run on the ui looper/thread.
     */
    fun sendMessageToRoot(msg: Message) {
        // post it if the current looper isn't null
        myLooper()?.postMessage(msg)
    }

    /**
     * Called when a key is typed and this View has the state STATE_FOCUSED.
     */
    open fun onKeyTyped(e: KeyEvent) {
        keyListener?.onKeyTyped(e)
    }

    /**
     * Called when a pressed is typed and this View has the state STATE_FOCUSED.
     */
    open fun onKeyPressed(e: KeyEvent) {
        keyListener?.onKeyPressed(e)
    }

    /**
     * Called when a released is typed and this View has the state STATE_FOCUSED.
     */
    open fun onKeyReleased(e: KeyEvent) {
        keyListener?.onKeyReleased(e)
    }

    /**
     * Called when tab was pressed and it was determined that this is the resultant View of the traversal. It should set
     * the current state to HOVER. Returns whether or not this is the last View in a potential chain of Views.
     */
    open fun onTabTraversal(): Boolean {
        if(state == State.STATE_NORMAL) {
            onStateChanged(state, State.STATE_HOVER)
            return false
        } else {
            onStateChanged(state, State.STATE_NORMAL)
            return true
        }
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
        val bg = background
        if(bg is StatefulDrawable)
            bg.draw(g, 0f, 0f, layoutSize.w, layoutSize.h, state)
        else
            bg?.draw(g, 0f, 0f, layoutSize.w, layoutSize.h)

        if(DEBUG_LAYOUT_BOXES) {
            g.color = Color.GREEN
            g.drawRect(0, 0, layoutSize.w.toInt(), layoutSize.h.toInt())
            if(state == State.STATE_HOVER) {
                g.color = Color(hashCode())
                g.fillRect(0, 0, layoutSize.w.toInt(), layoutSize.h.toInt())
            }
        }
    }

    /**
     * A mouse event listener.
     */
    interface ViewMouseListener {

        fun onMousePress(e: MouseEvent, p: Point)
        fun onMouseRelease(e: MouseEvent, p: Point)
        fun onMouseDragged(e: MouseEvent, p: Point)
        fun onMouseMoved(e: MouseEvent, p: Point)
        fun onMouseEnter(e: MouseEvent, p: Point)
        fun onMouseExit(e: MouseEvent, p: Point)
    }

    /***
     * A key event listener.
     */
    interface ViewKeyListener {

        fun onKeyTyped(e: KeyEvent)
        fun onKeyPressed(e: KeyEvent)
        fun onKeyReleased(e: KeyEvent)

    }

    /**
     * A [View].[State] listener.
     */
    interface StateListener {

        fun onStateChanged(prevState: State, newState: State)

    }

    enum class State {
        /**
         * The normal, idle state of the [View].
         */
        STATE_NORMAL,
        /**
         * The state when the mouse is hovering the [View]
         */
        STATE_HOVER,
        /**
         * The state when the mouse is pressing down on the [View]
         */
        STATE_PRESSED,
        /**
         * The state when the mouse has clicked on the [View]
         */
        STATE_FOCUSED;
    }
}