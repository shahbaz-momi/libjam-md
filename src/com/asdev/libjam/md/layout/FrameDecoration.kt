package com.asdev.libjam.md.layout

import com.asdev.libjam.md.base.RootView
import com.asdev.libjam.md.base.STATE_MAXIMIZED
import com.asdev.libjam.md.base.STATE_ORIGINAL
import com.asdev.libjam.md.drawable.*
import com.asdev.libjam.md.theme.*
import com.asdev.libjam.md.util.DIM_UNLIMITED
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.BUTTON_TYPE_FLAT
import com.asdev.libjam.md.view.ButtonView
import com.asdev.libjam.md.view.TextView
import java.awt.*
import java.awt.event.MouseEvent
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame

/**
 * Created by Asdev on 10/14/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

/**
 * A custom material design frame decoration.
 */
class FrameDecoration(title: String, val frame: JFrame, val rootView: RootView?): LinearLayout() {

    private val navBar: RelativeLayout
    private val toolbar: RelativeLayout
    private val titleText: TextView

    init {
        navBar = RelativeLayout()
        toolbar = RelativeLayout()
        titleText = TextView(title)

        // set fonts and colors of title text
        titleText.setThemeColor(COLOR_TITLE)
        titleText.setThemeFont(FONT_TITLE)
        titleText.gravity = GRAVITY_MIDDLE_LEFT
        titleText.setPadding(20f)
        // set the gravity of title text to middle left
        titleText.applyLayoutParameters(
                GenericLayoutParamList() with ("gravity" to GRAVITY_MIDDLE_LEFT)
        )

        // set the background colors for the navbar and toolbars
        onThemeChange(THEME, THEME)

        // set min and max sizes
        navBar.maxSize = FloatDim(-2f, 35f)
        navBar.minSize = navBar.maxSize
        toolbar.maxSize = FloatDim(-2f, 50f)
        toolbar.minSize = toolbar.maxSize
        maxSize = FloatDim(-2f, 85f)
        minSize = maxSize

        // add the title text to the toolbar
        toolbar.addChild(titleText)

        // add the nav buttons
        val buttonMinimize = ButtonView("", BUTTON_TYPE_FLAT)
        buttonMinimize.foreground = ImageDrawable(ImageIO.read(File("assets/minimize.png")), SCALE_TYPE_ORIGINAL)
        buttonMinimize.setThemeColor(COLOR_TITLE)

        buttonMinimize.onClickListener = { mouseEvent: MouseEvent, point: Point -> rootView?.minimize()}

        val maximize = ImageDrawable(ImageIO.read(File("assets/maximize.png")), SCALE_TYPE_ORIGINAL)
        val unmaximize = ImageDrawable(ImageIO.read(File("assets/unmaximize.png")), SCALE_TYPE_ORIGINAL)

        val buttonMaximize = ButtonView("", BUTTON_TYPE_FLAT)
        buttonMaximize.foreground = maximize

        buttonMaximize.onClickListener = { mouseEvent: MouseEvent, point: Point ->
            if(rootView?.getLocalFrameState() == STATE_ORIGINAL) {
                buttonMaximize.foreground = unmaximize
            } else {
                buttonMaximize.foreground = maximize

            }

            // set to maximize
            rootView?.setLocalFrameState(STATE_MAXIMIZED)
        }

        buttonMaximize.setThemeColor(COLOR_TITLE)

        val buttonClose = ButtonView("", BUTTON_TYPE_FLAT)
        buttonClose.foreground = ImageDrawable(ImageIO.read(File("assets/close.png")), SCALE_TYPE_ORIGINAL)
        buttonClose.onClickListener = {mouseEvent: MouseEvent, point: Point -> rootView?.destroy() }
        buttonClose.setThemeColor(COLOR_TITLE)
        // override to a red ripple
        buttonClose.setThemeRippleColor(-1)
        buttonClose.setRippleColor(Color.RED)

        val navBarButtonsContainer = LinearLayout()
        navBarButtonsContainer.setOrientation(ORIENTATION_VERTICAL)
        navBarButtonsContainer.applyLayoutParameters(
                GenericLayoutParamList() with ("gravity" to GRAVITY_MIDDLE_RIGHT)
        )

        navBarButtonsContainer.maxSize = FloatDim(120f, DIM_UNLIMITED.h)

        navBarButtonsContainer.addChild(buttonMinimize)
        navBarButtonsContainer.addChild(buttonMaximize)
        navBarButtonsContainer.addChild(buttonClose)
        navBar.addChild(navBarButtonsContainer)

        // make a listener to drag the window on mouse dragged
        navBar.mouseListener = object : ViewMouseListener {

            override fun onMouseEnter(e: MouseEvent, p: Point) {
            }

            override fun onMouseExit(e: MouseEvent, p: Point) {
            }

            private var startP = Point(-1, -1)

            override fun onMousePress(e: MouseEvent, p: Point) {
                startP.x = e.x
                startP.y = e.y
            }

            override fun onMouseRelease(e: MouseEvent, p: Point) {
            }

            override fun onMouseDragged(e: MouseEvent, p: Point) {
                frame.setLocation(e.xOnScreen - startP.x, e.yOnScreen - startP.y)
            }

            override fun onMouseMoved(e: MouseEvent, p: Point) {
            }

        }

        addChild(navBar)
        addChild(toolbar)
    }

    /**
     * Sets whether to draw this frame decoration above all other views. Results in a shadow being cast on the content underneath.
     */
    fun setDrawAboveAll(b: Boolean) = if(b) zIndex = 99 else zIndex = 0

    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        // on theme change the children
        super.onThemeChange(prevTheme, newTheme)

        // container is just background
        background = ColorDrawable(newTheme.getBackgroundColor())
        // set the navbar as dark primary
        navBar.background = ColorDrawable(newTheme.getDarkPrimaryColor())
        // the toolbar is just primary
        toolbar.background = ColorDrawable(newTheme.getPrimaryColor())
    }

    override fun onDraw(g: Graphics2D) {
        super.onDraw(g)
    }

}