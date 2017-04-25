package com.asdev.libjam.md.glg2d

import com.asdev.libjam.md.animation.AnimationChoreographer
import com.asdev.libjam.md.base.WindowStateManager
import com.asdev.libjam.md.layout.FrameDecoration
import com.asdev.libjam.md.layout.LinearLayout
import com.asdev.libjam.md.layout.newLayoutParams
import com.asdev.libjam.md.menu.ContextMenu
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.thread.*
import com.asdev.libjam.md.util.*
import com.asdev.libjam.md.view.View
import org.jogamp.glg2d.GLG2DCanvas
import java.awt.*
import java.awt.event.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JFrame
import javax.swing.JPanel

/**
 * Created by Asdev on 10/26/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

/**
 * A RootView that uses the GLG2D Canvas.
 */
class GLG2DRootView(view: View, title: String, d: Dimension, val isUndecorated: Boolean = false): JPanel(),
        Loopable, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    /**
     * The frame on the screen.
     */
    private val frame: JFrame = JFrame(title)

    /**
     * The UI looper for this RootView.
     */
    private val looper: Looper

    private val rootView: View

    private var frameDecoration: FrameDecoration? = null

    private val windowStateManager: WindowStateManager

    private val requestingLayout = AtomicBoolean(false)

    /**
     * The context menu associated with this root view.
     */
    private val contextMenu = ContextMenu(this::contextMenuShowing)

    val choreographer = AnimationChoreographer()

    init {
        frame.isUndecorated = isUndecorated

        windowStateManager = WindowStateManager(frame)

        frame.background = Color.WHITE
        background = Color.WHITE
        isDoubleBuffered = true

        if(isUndecorated) {
            frameDecoration = FrameDecoration(title, frame, windowStateManager)

            val container = LinearLayout()
            container.addChild(frameDecoration!!)
            container.addChild(view)

            rootView = container
        } else {
            rootView = view
        }

        looper = Looper(this)
        looper.name = "UILooper"
        looper.isDaemon = true
        looper.start()

        size = Dimension(
                if(d.width % 2 == 0) d.width else d.width + 1,
                if(d.height % 2 == 0) d.height else d.height + 1
        )

        frame.size = size

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setLocationRelativeTo(null)

        frame.add(GLG2DCanvas(this))

        frame.addKeyListener(this)
        frame.focusTraversalKeysEnabled = false
        frame.addMouseWheelListener(this)

        addMouseListener(this)
        addMouseMotionListener(this)

        setTheme(THEME)
    }

    fun getWindowStateManager() = windowStateManager

    fun getFrameDecoration() = frameDecoration

    fun setTheme(theme: Theme) {
        looper.postMessage(Message(MESSAGE_TYPE_ROOT_VIEW, MESSAGE_ACTION_THEME_CHANGED).apply { data0 = theme })
    }

    private var dragXVals = intArrayOf(0)
    private var dragYVals = intArrayOf(0)

    override fun setSize(d: Dimension) {
        super.setSize(d)

        dragXVals = intArrayOf(size.width - 10, size.width, size.width)
        dragYVals = intArrayOf(size.height, size.height, size.height - 10)

        looper.postMessage(Message(MESSAGE_TYPE_ROOT_VIEW, MESSAGE_ACTION_RESIZE).apply { data0 = Dimension(
                if(d.width % 2 == 0) d.width else d.width + 1,
                if(d.height % 2 == 0) d.height else d.height + 1
        ) })
    }

    /**
     * Actually sets the size. Should be called on the UILooper only!!!
     */
    private fun setSize0(attemptSize: Dimension) {
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

        // call on layout of the view group
        reLayout()
    }

    override fun loop() {
        choreographer.loop()
        rootView.loop()

        contextMenu.loop()

        if(choreographer.requestFrame()) {
            requestPaint()
        }

    }

    override fun onPostLoop() {
        if(cursorRequest != -1) {
            frame.cursor = Cursor.getPredefinedCursor(cursorRequest)
            cursorRequest = -1
        }

        rootView.onPostLoop()
    }

    private var cursorRequest = -1
    override fun handleMessage(msg: Message) {
        if(DEBUG && msg != MESSAGE_REQUEST_REPAINT) {
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
                contextMenu.onThemeChange(oldTheme, THEME)
                // call on draw
                requestPaint()
            } else if(msg.action == MESSAGE_ACTION_SET_CURSOR) {
                val cursorInt = msg.data0 as Int
                // take the highest priority cursor
                cursorRequest = maxOf(cursorRequest, cursorInt)
            }
        } else if(msg.type == MESSAGE_TYPE_ANIMATION) {
            choreographer.handleMessage(msg)
        }
    }

    private fun reLayout() {
        requestingLayout.set(true)
        repaint()
    }

    private fun requestPaint() {
        if(frame.isVisible)
            repaint()
    }

    override fun loopsPerSecond() = 70

    fun setCursor(cursor: Int) {
        looper.postMessage(Message(MESSAGE_TYPE_ROOT_VIEW, MESSAGE_ACTION_SET_CURSOR).apply { data0 = cursor })
    }

    private val PULL_TAB_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f)

    override fun paintComponent(g: Graphics?) {

        if(requestingLayout.getAndSet(false)) {
            if (DEBUG)
                println("[RootView] Performing a layout pass...")

            val start = System.nanoTime()

            rootView.onMeasure(newLayoutParams())
            rootView.onLayout(FloatDim(size.width.toFloat(), size.height.toFloat()))
            rootView.onPostLayout()


            if (DEBUG)
                println("[RootView] On layout took ${(System.nanoTime() - start) / 1000000.0}ms")

            // repaint because of a layout change
            requestPaint()
        }

        if(g == null || g !is Graphics2D)
            return

        g.clearRect(0, 0, size.width, size.height)

        // enable anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        // enable text anti-aliasing
        // g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)

        // call on draw on the root view group
        rootView.onDraw(g)
        rootView.onPostDraw(g)
        contextMenu.onDraw(g)
        contextMenu.onPostDraw(g)

        // draw the resize tab
        g.color = Color.BLACK
        g.composite = PULL_TAB_COMPOSITE
        g.fillPolygon(dragXVals, dragYVals, 3)
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

    // mouse events

    override fun mouseEntered(e: MouseEvent?) { }

    override fun mouseClicked(e: MouseEvent?) { }

    override fun mouseExited(e: MouseEvent?) { }

    private var hoveringResizer = false
    private var resizerMouseStart = Point()
    private var orgSize = Dimension()

    private var contextMenuShowing = false

    override fun mouseReleased(e: MouseEvent?) {
        // left click only
        if(e!!.button == MouseEvent.BUTTON1) {
            if(contextMenuShowing) {
                // check if it is in the bounds of the menu
                val x = e.x
                val y = e.y

                val pos = contextMenu.getPosition()
                val size = contextMenu.getSize()

                if(x >= pos.x && x <= pos.x + size.w && y >= pos.y && y <= pos.y + size.h) {
                    // on the context menu
                    val newPos = FloatPoint(x - pos.x, y - pos.y)
                    contextMenu.onMouseRelease(e, newPos)
                } else {
                    rootView.onMouseRelease(e, e.point)
                }
            } else {
                rootView.onMouseRelease(e, e.point)
            }
        }
    }

    override fun mousePressed(e: MouseEvent?) {
        if(e == null)
            return

        // if the mouse is hovering the cursor, then start the drag
        if(hoveringResizer) {
            resizerMouseStart = e.locationOnScreen
            orgSize = frame.size
        } else if(e.button == MouseEvent.BUTTON1) {
            if(contextMenuShowing) {
                // check if it is in the bounds of the menu
                val x = e.x
                val y = e.y

                val pos = contextMenu.getPosition()
                val size = contextMenu.getSize()

                if(x >= pos.x && x <= pos.x + size.w && y >= pos.y && y <= pos.y + size.h) {
                    // on the context menu
                    val newPos = FloatPoint(x - pos.x, y - pos.y)
                    contextMenu.onMousePress(e, newPos)
                } else {
                    // hide the context menu
                    contextMenuShowing = false
                    contextMenu.hide()
                    // perform the actual click event
                    rootView.onMousePress(e, e.point)
                }
            } else {
                rootView.onMousePress(e, e.point)
            }

            requestPaint()
        } else if(e.button == MouseEvent.BUTTON3) {
            contextMenuShowing = true

            val items = rootView.findContextMenuItems(FloatPoint(e.x.toFloat(), e.y.toFloat()))

            if(items == null || items.isEmpty()) {

                // hide any exisiting context menus
                contextMenu.hide()

                contextMenuShowing = false
                return
            }

            contextMenu.setItems(items)

            contextMenu.show(Math.min(e.x.toFloat(), size.width.toFloat() - contextMenu.getSize().w - 15f), Math.min(e.y.toFloat(), size.height - contextMenu.getSize().h - 15f))
            requestPaint()
        }
    }

    override fun mouseMoved(e: MouseEvent?) {
        // if mouse has moved to bottom right corner, set the cursor
        if(e == null)
            return

        if(e.x >= size.width - 10 && e.y >= size.height - 10) {
            // set the cursor
            if(!hoveringResizer)
                setCursor(Cursor.SE_RESIZE_CURSOR)
            hoveringResizer = true
        } else {
            if(hoveringResizer)
                setCursor(Cursor.DEFAULT_CURSOR)
            hoveringResizer = false

            if(contextMenuShowing) {
                val x = e.x
                val y = e.y

                val pos = contextMenu.getPosition()
                val size = contextMenu.getSize()

                // on the context menu
                val newPos = FloatPoint(x - pos.x, y - pos.y)
                contextMenu.onMouseMoved(e, newPos)

                if(!(x >= pos.x && x <= pos.x + size.w && y >= pos.y && y <= pos.y + size.h)) {
                    rootView.onMouseMoved(e, e.point)
                }
            } else {
                rootView.onMouseMoved(e, e.point)
            }
        }
    }

    override fun mouseDragged(e: MouseEvent?) {
        if(e == null)
            return

        if(hoveringResizer) {
            val d = Dimension(
                    orgSize.width + (e.xOnScreen - resizerMouseStart.x),
                    orgSize.height + (e.yOnScreen - resizerMouseStart.y)
            )

            frame.size = Dimension(
                    if(d.width % 2 == 0) d.width else d.width + 1,
                    if(d.height % 2 == 0) d.height else d.height + 1
            )
        } else {
            if(contextMenuShowing) {
                val x = e.x
                val y = e.y

                val pos = contextMenu.getPosition()
                val size = contextMenu.getSize()

                // on the context menu
                val newPos = FloatPoint(x - pos.x, y - pos.y)
                contextMenu.onMouseDragged(e, newPos)

                if(!(x >= pos.x && x <= pos.x + size.w && y >= pos.y && y <= pos.y + size.h)) {
                    rootView.onMouseDragged(e, e.point)
                }
            } else {
                rootView.onMouseDragged(e, e.point)
            }
        }
    }

    override fun keyTyped(e: KeyEvent?) {
        // call on the root view regardless of state
        rootView.onKeyTyped(e!!)
    }

    override fun keyPressed(e: KeyEvent?) {
        if(e == null)
            return

        // check if tab for tab traversal
        if(e.keyCode == KeyEvent.VK_TAB) {
            if(DEBUG)
                println("[RootView] Traversing Views...")

            // call traverse on the root view
            rootView.onTabTraversal()
        }

        // call on the root view regardless of state
        rootView.onKeyPressed(e)
    }

    override fun keyReleased(e: KeyEvent?) {
        // call on the root view regardless of state
        rootView.onKeyReleased(e!!)
    }

    // scrolling events
    override fun mouseWheelMoved(e: MouseWheelEvent?) {
        rootView.onScroll(e!!)
    }
}