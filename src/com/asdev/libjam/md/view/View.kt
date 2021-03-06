package com.asdev.libjam.md.view

import com.asdev.libjam.md.animation.Animator
import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.animation.LinearInterpolator
import com.asdev.libjam.md.base.RootView
import com.asdev.libjam.md.drawable.AnimatedDrawable
import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.Drawable
import com.asdev.libjam.md.drawable.StatefulDrawable
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.GenericParamList
import com.asdev.libjam.md.layout.LayoutParams
import com.asdev.libjam.md.menu.ContextMenuItem
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.thread.*
import com.asdev.libjam.md.util.*
import com.asdev.libjam.md.xml.XMLParamList
import res.R
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

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
 * implements the bare minimum functionality, allowing for easy extensibility. It does NOT have a context or a reference
 * to its parent View/ViewGroup.
 */
open class View (
        /**
         * The id associated with this id.
         */
        var id: String = "View:${generateRandomId()}") {

    /**
     * The max and minimum sizes of this view. Consider the maximum size as the preferred size as the layout will always
     * try to grow the view to that size.
     */
    var maxSize = DIM_UNLIMITED.copy()
    var minSize = DIM_UNLIMITED.copy()

    /**
     * Defines the visibility of this view. May either be VISIBLE or INVISIBLE
     */
    var visibility = VISIBILITY_VISIBLE

    /**
     * The size of this view set by the layout. Will be between min and max size once a layout pass has been conducted.
     */
    var layoutSize = DIM_UNSET.copy()

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
    var onMouseListener: ViewMouseListener? = null

    /**
     * The state listener associated with this [View].
     */
    var onStateChangeListener: ((State, State) -> Unit)? = null

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

    /**
     * An interface that listens to key press events.
     */
    var onKeyListener: ViewKeyListener? = null

    /**
     * A listener for scroll events upon this View.
     */
    var onScrollListener: ((MouseWheelEvent) -> Unit)? = null

    /**
     * A list that contains the context menu items of this view, if any.
     * Nullable if there is no context menu for this view.
     */
    var contextMenuItems: List<ContextMenuItem>? = null

    /**
     * Called by the layout before layout to signify that the view should determine its max and min sizes at this point.
     * @return the min and max sizes, respectively.
     */
    open fun onMeasure(result: LayoutParams): LayoutParams = result.apply { minSize = this@View.minSize; maxSize = this@View.maxSize; applyAdditional(paramList); }

    /**
     * Called when the layout has determined the size of this layout.
     */
    open fun onLayout(newSize: FloatDim) {
        if(DEBUG) {
            println("[View] onLayout: $newSize")
        }

        layoutSize = newSize
    }

    /**
     * Called when the layout has been determined and the layout pass has been run.
     */
    open fun onPostLayout() { }

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
     * The addition parameter list ([GenericParamList] or [XMLParamList]) attached to this view. Will be applied in [onMeasure].
     */
    private var paramList: GenericParamList? = null

    /**
     * Applies the given layout parameters. Can be taken by onMeasure
     */
    open fun applyParameters(params: GenericParamList) {
        if(DEBUG) {
            println("[View] Got new parameters: $params")
        }

        paramList = params

        if(params is XMLParamList) {
            params.setToString(R.attrs.View.id, this::id)

            if(params.hasParam(R.attrs.View.visibility)) {
                val vis = params.getString(R.attrs.View.visibility)
                visibility = if (vis == "visible") VISIBILITY_VISIBLE else VISIBILITY_INVISIBLE
            }

            params.setToDim(R.attrs.View.minSize, this::minSize)
            params.setToDim(R.attrs.View.maxSize, this::maxSize)
            params.setToDrawable(R.attrs.View.background, this::background)
            params.setToInt(R.attrs.View.z_index, this::zIndex)

            if(params.hasParam(R.attrs.View.translation_x)) {
                translationX = params.getInt(R.attrs.View.translation_x)!!.toFloat()
            }

            if(params.hasParam(R.attrs.View.translation_y)) {
                translationY = params.getInt(R.attrs.View.translation_y)!!.toFloat()
            }

            if(params.hasParam(R.attrs.View.overclip_bottom)) {
                overClipBottom = params.getInt(R.attrs.View.overclip_bottom)!!.toFloat()
            }

            if(params.hasParam(R.attrs.View.overclip_top)) {
                overClipTop = params.getInt(R.attrs.View.overclip_top)!!.toFloat()
            }

            if(params.hasParam(R.attrs.View.overclip_left)) {
                overClipLeft = params.getInt(R.attrs.View.overclip_left)!!.toFloat()
            }

            if(params.hasParam(R.attrs.View.overclip_right)) {
                overClipRight = params.getInt(R.attrs.View.overclip_right)!!.toFloat()
            }
        }
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
        // flag repaint on ui thread
        flagRequestingRepaint = true
    }

    private var mousePressTime = -1L
    private var hasLongPressed = false

    /**
     * Called when the mouse is pressed within this [View]'s bounds.
     */
    open fun onMousePress(e: MouseEvent, mPos: Point) {
        onMouseListener?.onMousePress(e, mPos)
        // change the state to pressed
        onStateChanged(state, State.STATE_PRESSED)

        onPressListener?.invoke(e, mPos)

        mousePressTime = System.currentTimeMillis()
    }

    /**
     * Called when the mouse is released within this [View]'s bounds.
     */
    open fun onMouseRelease(e: MouseEvent, mPos: Point) {
        onMouseListener?.onMouseRelease(e,  mPos)

        onStateChanged(state, State.STATE_POST_PRESS)

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
    open fun onMouseMoved(e: MouseEvent, mPos: Point) = onMouseListener?.onMouseMoved(e, mPos)

    /**
     * Called when the mouse is dragged within this [View]'s bounds.
     */
    open fun onMouseDragged(e: MouseEvent, mPos: Point) = onMouseListener?.onMouseDragged(e, mPos)

    /**
     * Called when the mouse enters this [View]'s bounds.
     */
    open fun onMouseEnter(e: MouseEvent, mPos: Point) {
        onMouseListener?.onMouseEnter(e, mPos)

        onStateChanged(state, State.STATE_HOVER)
    }

    /**
     * Called when the mouse exits this [View]'s bounds.
     */
    open fun onMouseExit(e: MouseEvent, mPos: Point) {
        onMouseListener?.onMouseExit(e, mPos)

        onStateChanged(state, State.STATE_NORMAL)
    }

    /**
     * Stores whether or not this view has focus.
     */
    protected var hasFocus = false

    /**
     * Returns whether or not this [View] has focus.
     */
    fun hasFocus() = hasFocus

    /**
     * Called when the focus of this view has been gained.
     */
    open fun onFocusGained() {
        onStateChanged(state, State.STATE_POST_PRESS)
        hasFocus = true
    }

    /**
     * Called when the focus of this view is lost.
     */
    open fun onFocusLost() {
        onStateChanged(state, State.STATE_NORMAL)
        hasFocus = false
    }

    /**
     * Called when the state of this view has changed.
     */
    open fun onStateChanged(previous: State, newState: State) {
        // call on listener
        onStateChangeListener?.invoke(previous, newState)

        // call on potential stateful drawable
        if(background != null && background is StatefulDrawable)
            (background!! as StatefulDrawable).onStateChanged(this, previous, newState)

        // request a repaint to update a potential stateful drawable.
        requestRepaint()
        state = newState
    }

    /**
     * An action to be run upon the UI thread.
     */
    private var postAction: ((View) -> Unit)? = null

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

        if(postAnim != null) {
            runAnimation(postAnim!!, false)
            postAnim = null
        }

        if(postCancelAnimId != null) {
            cancelAnimation(postCancelAnimId!!)
            postCancelAnimId = null
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
     * Called after all loop()s have been completed
     */
    open fun onPostLoop() {
        if(postAction != null) {
            postAction!!.invoke(this)
            postAction = null
        }
    }

    /**
     * Posts the specific action to be run upon the ui thread.
     * Returns whether or not the action will actually be run.
     */
    fun postAction(action: (View) -> Unit): Boolean {
        if(postAction != null)
            return false

        this.postAction = action
        return true
    }

    /**
     * An animation to post to be run later.
     */
    private var postAnim: Animator? = null
    private var postCancelAnimId: String? = null

    /**
     * Posts the animation to the Choregrapher and starts it if not already started. Will be posted if the current Thread
     * is not the looper. If an animation with the same id is already running, this animation will not be run.
     */
    open fun runAnimation(a: Animator, startIt: Boolean) {
        if(myLooper() is Looper) {
            sendMessageToRoot(Message(MESSAGE_TYPE_ANIMATION, MESSAGE_ACTION_RUN_ANIMATION).apply { data0 = a })

            if(!a.isRunning() && startIt) {
                a.start()
            }
        } else {
            postAnim = a
            if(!a.isRunning() && startIt)
                a.start()
        }
    }

    /**
     * Cancels any animation with the given id.
     */
    open fun cancelAnimation(animationId: String) {
        if(myLooper() is Looper) {
            sendMessageToRoot(Message(MESSAGE_TYPE_ANIMATION, MESSAGE_ACTION_CANCEL_ANIMATION).apply { data0 = animationId })
        } else {
            postCancelAnimId = animationId
        }
    }

    /**
     * Returns whether the specified animation is running or not. MUST be run on the UILooper, otherwise an IllegalStateException
     * will be thrown.
     */
    open fun isAnimationRunning(animationId: String): Boolean {
        val looper = myLooper() ?: throw IllegalStateException("isAnimationRunning() must be called from the UI Looper!")
        val rootView = looper.loopable

        if(rootView is RootView) {
            val choreographer = rootView.choreographer
            return choreographer.isAnimationRunning(animationId)
        } else if(rootView is GLG2DRootView) {
            val choreographer = rootView.choreographer
            return choreographer.isAnimationRunning(animationId)
        }

        // should never reach this point as the loopable should be a root view
        throw IllegalStateException("No RootView found bound with the current Looper when performing isAnimationRunning()!")
    }

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
        val looper = myLooper()?: throw IllegalStateException("sendMessageToRoot not called on UI Looper")
        looper.postMessage(msg)
    }

    /**
     * Called when a key is typed and this View has the state STATE_POST_PRESS.
     */
    open fun onKeyTyped(e: KeyEvent) {
        onKeyListener?.onKeyTyped(e)
    }

    /**
     * Called when a pressed is typed and this View has the state STATE_POST_PRESS.
     */
    open fun onKeyPressed(e: KeyEvent) {
        onKeyListener?.onKeyPressed(e)
    }

    /**
     * Called when a released is typed and this View has the state STATE_POST_PRESS.
     */
    open fun onKeyReleased(e: KeyEvent) {
        onKeyListener?.onKeyReleased(e)
    }

    /**
     * Called when tab was pressed and it was determined that this is the resultant View of the traversal. If this returns
     * true, it means that this View has been fully traversed, otherwise it will return false.
     */
    open fun onTabTraversal(): Boolean {
        return true
    }

    /**
     * Called when the mouse is scrolled upon this View.
     */
    open fun onScroll(e: MouseWheelEvent) {
        onScrollListener?.invoke(e)
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
            g.color = Color(hashCode() shr state.ordinal shr if(hasFocus) 1 else 0)
            g.fillRect(0, 0, layoutSize.w.toInt(), layoutSize.h.toInt())
        }
    }

    /**
     * Called when the initial drawn has already occurred, and this drawing pass is used to draw over the previous one.
     */
    open fun onPostDraw(g: Graphics2D) {
        if(DEBUG_LAYOUT_BOXES) {
            g.color = Color.GREEN
            g.drawRect(0, 0, layoutSize.w.toInt(), layoutSize.h.toInt())
        }
    }

    /**
     * Finds the context menu items for the view at the given position, or null if none found.
     */
    open fun findContextMenuItems(viewPos: FloatPoint) = contextMenuItems

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
        STATE_POST_PRESS;
    }
}