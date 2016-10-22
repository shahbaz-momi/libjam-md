package com.asdev.libjam.md.base

import com.asdev.libjam.md.animation.AccelerateInterpolator
import com.asdev.libjam.md.animation.FactorableDecelerateInterpolator
import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.layout.ElevatedLayout
import com.asdev.libjam.md.layout.FrameDecoration
import com.asdev.libjam.md.layout.LinearLayout
import com.asdev.libjam.md.layout.newLayoutParams
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.thread.*
import com.asdev.libjam.md.util.*
import com.asdev.libjam.md.view.View
import java.awt.*
import java.awt.event.*
import javax.swing.JFrame
import javax.swing.JPanel

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.base
 *
 * The root view that creates and attaches to a swing window.
 */

class RootView: JPanel, Loopable, MouseListener, MouseMotionListener, WindowFocusListener {

    private val frame: JFrame
    private val rootView: View
    private val looper: Looper
    private val frameDecoration: FrameDecoration?

    constructor(title: String, size: Dimension, rootVG: View, customToolbar: Boolean) {
        frame = JFrame(title)

        frame.isUndecorated = customToolbar

        if(customToolbar) {
            frame.background = Color(0, 0, 0, 0)
            background = Color(0, 0, 0, 0)
            isOpaque = false

            // add a frame decorator
            frameDecoration = FrameDecoration(title, frame, this)

            val l = LinearLayout()
            l.addChild(frameDecoration)
            l.addChild(rootVG)

            // make the window a bit transparent on focus lost, so add a listener for that
            frame.addWindowFocusListener(this)

            // now add the content
            rootView = ElevatedLayout(l, shadowYOffset = 0f)
        } else {
            frameDecoration = null
            this.rootView = rootVG
        }

        // create a new looper and bind it with this loopable
        looper = Looper(this)
        looper.name = "UILooper"
        looper.isDaemon = true
        looper.start()

        setSize(size)

        frame.size = size

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setLocationRelativeTo(null)

        // add resize event thingy
        frame.addComponentListener(object : ComponentListener {
            override fun componentMoved(e: ComponentEvent?) {

            }
            override fun componentResized(e: ComponentEvent?) = setSize(frame.contentPane.size!!)
            override fun componentShown(e: ComponentEvent?) {

            }
            override fun componentHidden(e: ComponentEvent?) {
            }
        })

        frame.add(this)

        frame.addMouseListener(this)
        frame.addMouseMotionListener(this)

        // double buffering
        isDoubleBuffered = true

        // initialize the default theme
        setTheme(THEME)

        frame.addWindowListener(object : WindowListener {
            override fun windowDeiconified(e: WindowEvent?) {
                startMaximizeAnimation(actualSize)
            }

            override fun windowActivated(e: WindowEvent?) {
            }

            override fun windowDeactivated(e: WindowEvent?) {
            }

            override fun windowIconified(e: WindowEvent?) {
            }

            override fun windowClosing(e: WindowEvent?) {
            }

            override fun windowClosed(e: WindowEvent?) {
            }

            override fun windowOpened(e: WindowEvent?) {
            }

        })
    }

    fun getFrameDecoration() = frameDecoration

    fun setTheme(theme: Theme) {
        looper.postMessage(Message(MESSAGE_TYPE_ROOT_VIEW, MESSAGE_ACTION_THEME_CHANGED).apply { data0 = theme })
    }

    // potentially unsafe. E.g. stopSafe() could be called on the looper
    // fun getLooper() = looper

    override fun getSize() = Dimension(actualSize.w.toInt(), actualSize.h.toInt())

    private var actualSize = DIM_UNSET

    /**
     * Resizes this root view. Is thread-safe (actual resizing is done on Looper thread).
     */
    override fun setSize(d: Dimension?) {
        looper.postMessage(Message(MESSAGE_TYPE_ROOT_VIEW, MESSAGE_ACTION_RESIZE).apply { data0 = d })
    }

    /**
     * Internal size-setting method. Only to be used by handleMessage in the Looper thread.
     */
    fun setSize0(attemptSize: Dimension) {
        if(DEBUG)
            println("[RootView] Resetting the size to: $attemptSize")

        // check if it is within the bounds of the min and max of VG
        val dims = rootView.onMeasure(newLayoutParams())

        var size = FloatDim(attemptSize.width.toFloat(), attemptSize.height.toFloat())

        // does a size check
        if(!(dims.minSize to dims.maxSize fits size)) {
            // if size is smaller than use the min size otherwise use the max size
            var newW = size.w
            var newH = size.h
            if(size.w <= dims.minSize.w && dims.minSize != DIM_UNSET && dims.minSize != DIM_UNLIMITED) {
                // use min w
                newW = dims.minSize.w
            } else if(size.w >= dims.maxSize.w && dims.maxSize != DIM_UNSET && dims.maxSize != DIM_UNLIMITED) {
                newW = dims.maxSize.w
            }

            if(size.h <= dims.minSize.h && dims.minSize != DIM_UNSET && dims.minSize != DIM_UNLIMITED) {
                newH = dims.minSize.h
            } else if(size.h >= dims.maxSize.h && dims.maxSize != DIM_UNSET && dims.maxSize != DIM_UNLIMITED) {
                newH = dims.maxSize.h
            }

            size = FloatDim(newW, newH)
        }

        // we now know are size, so set the frame to that
        frame.contentPane.size = Dimension( size.w.toInt(), size.h.toInt() )

        frame.minimumSize = Dimension(dims.minSize.w.toInt() + 200, dims.minSize.h.toInt() + 200)

        // set the actual size for future use
        actualSize = size

        // call on layout of the view group
        reLayout()
    }

    private var minimizing = false

    override fun loop() {
        // loop the views
        rootView.loop()

        // check to minimize
        if(minimizing && maxXAnim.hasEnded()) {
            minimizing = false
            frame.state = Frame.ICONIFIED
        }
    }

    fun setCursor(cursor: Int) {
        looper.postMessage(Message(MESSAGE_TYPE_ROOT_VIEW, MESSAGE_ACTION_SET_CURSOR).apply { data0 = cursor })
    }

    override fun handleMessage(msg: Message) {
        if(DEBUG) {
            println("[RootView] Handling message: $msg")
        }
        if(msg.type == MESSAGE_TYPE_VIEW) {
            if(msg.action == MESSAGE_ACTION_REQUEST_LAYOUT)  {
                reLayout()
            } else if(msg.action == MESSAGE_ACTION_REPAINT) {
                // repaint
                requestPaint()
            }
        } else if(msg.type == MESSAGE_TYPE_ROOT_VIEW) {
            if(msg.action == MESSAGE_ACTION_RESIZE) {
                // resize on the proper thread (this thread)
                setSize0(msg.data0 as Dimension)
            } else if(msg.action == MESSAGE_ACTION_THEME_CHANGED) {
                val theme = msg.data0 as Theme
                // initialize the theme
                theme.init()
                val oldTheme = THEME
                // update the current one
                THEME = theme
                // call onThemeChanged of the view
                rootView.onThemeChange(oldTheme, THEME)
                // call on draw
                requestPaint()
            } else if(msg.action == MESSAGE_ACTION_SET_CURSOR) {
                val cursorInt = msg.data0 as Int
                val cursor = Cursor.getPredefinedCursor(cursorInt)
                // set it
                frame.cursor = cursor
            }
        }
    }

    private val interpolator = FactorableDecelerateInterpolator(3.2f)

    private var maxXAnim = FloatValueAnimator(500f, interpolator, 0f, 0f, 0f)
    private var maxYAnim = FloatValueAnimator(500f, interpolator, 0f, 0f, 0f)
    private var maxWAnim = FloatValueAnimator(500f, interpolator, 0f, 0f, 0f)
    private var maxHAnim = FloatValueAnimator(500f, interpolator, 0f, 0f, 0f)


    // runs the maximize clipping anim
    fun startMaximizeAnimation(size: FloatDim) {
        // if its already running then don't do it
        if(!maxXAnim.hasEnded() || !maxWAnim.hasEnded() ||
                !maxYAnim.hasEnded() || !maxHAnim.hasEnded()) {
            return
        }
        val divFactor = 6f
        // start the x from near left to the left and the width from near zero to full width
        maxXAnim.setFromValue(size.w / divFactor).setToValue(0f).setInterpolator(interpolator).start()
        maxWAnim.setFromValue(size.w / divFactor).setToValue(size.w).setInterpolator(interpolator).start()
        // same for y
        maxYAnim.setFromValue(size.h - size.h / divFactor).setToValue(0f).setInterpolator(interpolator).start()
        maxHAnim.setFromValue(size.h / divFactor).setToValue(size.h).setInterpolator(interpolator).start()
    }

    // runs the maximize clipping anim
    fun startMinimizeAnimation(size: FloatDim) {
        minimizing = true
        // if its already running then don't do it
        if(!maxXAnim.hasEnded() || !maxWAnim.hasEnded() ||
                !maxYAnim.hasEnded() || !maxHAnim.hasEnded()) {
            return
        }
        val divFactor = 6f
        // start the x from near left to the left and the width from near zero to full width
        maxXAnim.setFromValue(0f).setToValue(size.w / divFactor).setInterpolator(AccelerateInterpolator).start()
        maxWAnim.setFromValue(size.w).setToValue(0f).setInterpolator(AccelerateInterpolator).start()
        // same for y
        maxYAnim.setFromValue(0f).setToValue(size.h - size.h / divFactor).setInterpolator(AccelerateInterpolator).start()
        maxHAnim.setFromValue(size.h).setToValue(0f).setInterpolator(AccelerateInterpolator).start()
    }

    fun minimize () {
        startMinimizeAnimation(actualSize)
    }

    private fun reLayout() {
        if(DEBUG)
            println("[RootView] Performing a layout pass...")

        val start = System.nanoTime()

        rootView.onLayout(actualSize)

        if(DEBUG)
            println("[RootView] On layout took ${(System.nanoTime() - start) / 1000000.0}ms")

        // repaint because of a layout change
        requestPaint()
    }

    private fun requestPaint() {
        if(DEBUG)
            println("[RootView] Requesting a repaint...")

        if(frame.isVisible)
            repaint()
    }

    // we want the buttery-smooth 60fps action
    override fun loopsPerSecond() = 60

    override fun paintComponent(g: Graphics?) {
        val start = System.nanoTime()

        if(g == null || g !is Graphics2D)
            return

        g.clearRect(0, 0, size.width, size.height)

        // enable anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        // enable text anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)

        // check for animations
        if(!maxXAnim.hasEnded() || !maxWAnim.hasEnded() ||
            !maxYAnim.hasEnded() || !maxHAnim.hasEnded()) {
            // set the clip to the values
            g.setClip(maxXAnim.getValue().toInt(), maxYAnim.getValue().toInt(), maxWAnim.getValue().toInt(), maxHAnim.getValue().toInt())
            requestPaint()
        }

        // call on draw on the root view group
        rootView.onDraw(g)

        if(DEBUG)
            println("[RootView] onDraw() took ${(System.nanoTime() - start) / 1000000.0}ms")
    }

    /**
     * Shows the frame (internal JFrame).
     */
    fun showFrame() {
        if(DEBUG)
            println("[RootView] Showing frame...")

        frame.isVisible = true
    }

    /**
     * Hides the frame (internal JFrame).
     */
    fun hideFrame() {
        if(DEBUG)
            println("[RootView] Hiding frame...")

        frame.isVisible = false
    }

    override fun mouseEntered(e: MouseEvent?) {
    }

    override fun mouseClicked(e: MouseEvent?) {

    }

    override fun mouseExited(e: MouseEvent?) {

    }

    override fun mouseReleased(e: MouseEvent?) {
        // left click only
        if(e!!.button == MouseEvent.BUTTON1)
            rootView.onMouseRelease(e, e.point)
    }

    override fun mousePressed(e: MouseEvent?) {
        // left click only
        if(e!!.button == MouseEvent.BUTTON1)
            rootView.onMousePress(e, e.point)
    }

    override fun mouseMoved(e: MouseEvent?) {
        rootView.onMouseMoved(e!!, e.point)
    }

    override fun mouseDragged(e: MouseEvent?) {
        rootView.onMouseDragged(e!!, e.point)
    }

    override fun windowLostFocus(e: WindowEvent?) {
        // set opacity a little lower
        // frame.opacity = 0.85f
    }

    override fun windowGainedFocus(e: WindowEvent?) {
        frame.opacity = 1f
    }

}
