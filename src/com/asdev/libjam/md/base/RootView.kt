package com.asdev.libjam.md.base

import com.asdev.libjam.md.layout.ElevatedLayout
import com.asdev.libjam.md.layout.FrameDecoration
import com.asdev.libjam.md.layout.LinearLayout
import com.asdev.libjam.md.layout.newLayoutParams
import com.asdev.libjam.md.theme.LightMaterialTheme
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.thread.*
import com.asdev.libjam.md.util.*
import com.asdev.libjam.md.view.View
import java.awt.*
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.BorderFactory
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.border.EtchedBorder

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

class RootView: JPanel, Loopable {

    private val frame: JFrame
    private val rootView: View
    private val looper: Looper
    private val frameDecoration: FrameDecoration?

    constructor(title: String, size: Dimension, rootVG: View, customToolbar: Boolean) {
        frame = JFrame(title)

        // TODO: custom toolbars like chrome os
        frame.isUndecorated = customToolbar
        // TODO: toolbar semi transparent when not focused

        if(customToolbar) {
            frame.background = Color(0, 0, 0, 0)
            background = Color(0, 0, 0, 0)
            isOpaque = false

            // add a frame decorator
            frameDecoration = FrameDecoration(title)

            val l = LinearLayout()
            l.addChild(frameDecoration)
            l.addChild(rootVG)

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

        // double buffer this biotch
        isDoubleBuffered = true

        setTheme(LightMaterialTheme)
    }

    fun setTheme(theme: Theme) {
        looper.postMessage(Message(MESSAGE_TYPE_ROOT_VIEW, MESSAGE_ACTION_THEME_CHANGED).apply { data0 = theme })
    }

    fun getLooper() = looper

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

    override fun loop() {
        // loop the views
        rootView.loop()
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
            }
        }
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

}
