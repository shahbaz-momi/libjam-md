package com.asdev.libjam.md.view

import com.asdev.libjam.md.drawable.*
import com.asdev.libjam.md.layout.GRAVITY_MIDDLE_MIDDLE
import com.asdev.libjam.md.layout.GenericParamList
import com.asdev.libjam.md.theme.COLOR_ACCENT
import com.asdev.libjam.md.theme.COLOR_RIPPLE
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.xml.XMLParamList
import res.R
import java.awt.Color
import java.awt.Cursor
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.MouseEvent

/**
 * Created by Asdev on 10/20/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.view
 */

/**
 * The type of a raised button.
 */
const val BUTTON_TYPE_RAISED = 0

/**
 * The type of a flat button.
 */
const val BUTTON_TYPE_FLAT = 1

/**
 * A simple button widget.
 */
open class ButtonView(): TextView() {

    private var type = BUTTON_TYPE_RAISED

    constructor(text: String, type: Int = BUTTON_TYPE_RAISED): this() {
        this.text = text
        this.type = type

        updateSettings()

        overClipBottom = 20f
        overClipLeft = 20f
        overClipRight = 20f
        overClipTop = 20f
    }

    init {
        updateSettings()

        overClipBottom = 20f
        overClipLeft = 20f
        overClipRight = 20f
        overClipTop = 20f
    }

    private var themeBgColor = COLOR_ACCENT

    /**
     * The ripple effect of this button.
     */
    private var ripple: RippleDrawable? = null

    /**
     * The foreground of this button. May be an image, etc.
     */
    var foreground: Drawable? = null

    override fun applyParameters(params: GenericParamList) {
        super.applyParameters(params)

        if(params is XMLParamList) {
            if(params.hasParam(R.attrs.ButtonView.type)) {
                type = if(params.getString(R.attrs.ButtonView.type) == "flat") BUTTON_TYPE_FLAT else BUTTON_TYPE_RAISED
                updateSettings()
            }
        }
    }

    /**
     * Sets the type of this button.
     */
    private fun setType(type: Int) {
        this.type = type
        updateSettings()
    }

    private fun updateSettings() {
        // set the background to a hover shadow and a rounded rectangle based on the accent color
        if(type == BUTTON_TYPE_RAISED)
            background =
                    AnimatedCompoundDrawable(
                            AnimatedHoverShadowDrawable(opacity = 0.3f, viewYTransHover = 0f),
                            RoundedRectangleDrawable(THEME.getAccentColor(), 6f)
                    )
        // if its a flat style then background should be null
        else if(type == BUTTON_TYPE_FLAT) {
            setThemeColor(getThemeColor())
            background = null
        }

        gravity = GRAVITY_MIDDLE_MIDDLE

        // set the ripple to the ripple
        ripple = RippleDrawable(THEME.getRippleColor())
    }

    private var themeRippleColor = COLOR_RIPPLE

    /**
     * Sets the ripple color of this button.
     */
    fun setRippleColor(color: Color) {
        ripple?.highlight = color
    }

    /**
     * Sets the ripple color of this button according to the specified theme color.
     */
    fun setThemeRippleColor(color: Int) {
        themeRippleColor = color

        if(color != -1)
            setRippleColor(THEME.getColor(color))
    }

    override fun loop() {
        super.loop()

        if(ripple != null && ripple is AnimatedDrawable) {
            if ((ripple!! as AnimatedDrawable).requestFrame()) {
                requestRepaint()
            }
        }

        if(foreground != null && foreground is AnimatedDrawable) {
            if ((foreground!! as AnimatedDrawable).requestFrame()) {
                requestRepaint()
            }
        }
    }

    override fun onStateChanged(previous: State, newState: State) {
        super.onStateChanged(previous, newState)

        if(ripple != null && ripple is StatefulDrawable)
            (ripple!! as StatefulDrawable).onStateChanged(this, previous, newState)

        if(foreground != null && foreground is StatefulDrawable)
            (foreground!! as StatefulDrawable).onStateChanged(this, previous, newState)

        // if hover, set cursor
        if(newState == State.STATE_HOVER) {
            setCursor(Cursor.HAND_CURSOR)
        } else if (newState == State.STATE_NORMAL){
            setCursor(Cursor.DEFAULT_CURSOR)
        }
    }

    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)

        // update the background color
        setThemeBackgroundColor(themeBgColor)
        setThemeRippleColor(themeRippleColor)
    }

    /**
     * Sets this background color according to the theme.
     */
    fun setThemeBackgroundColor(color: Int) {
        themeBgColor = color
        if(themeBgColor != -1)
            setBackgroundColor(THEME.getColor(color))
    }

    /**
     * Sets the background color of this button to the given color.
     * Make sure to call [setThemeBackgroundColor] to -1 to clear the theme preferences.
     */
    fun setBackgroundColor(color: Color) {
        if(type == BUTTON_TYPE_FLAT) {
            // set the text to that color
            if(getThemeColor() == COLOR_ACCENT)
                this.color = color
        } else if(type == BUTTON_TYPE_RAISED) {
            if (background == null || background !is AnimatedCompoundDrawable)
                throw IllegalStateException("The ButtonView background should not be changed!")

            ((background!! as AnimatedCompoundDrawable).getDrawables()[1] as RoundedRectangleDrawable).color = color
        }
    }

    override fun onTabTraversal(): Boolean {
        if(state == State.STATE_NORMAL) {
            onStateChanged(state, State.STATE_HOVER)
            return false
        } else {
            onStateChanged(state, State.STATE_NORMAL)
            return true
        }
    }

    private var mPos = Point(-1, -1)
    override fun onMouseDragged(e: MouseEvent, mPos: Point) {
        super.onMouseDragged(e, mPos)
        this.mPos = mPos
    }

    override fun onMousePress(e: MouseEvent, mPos: Point) {
        super.onMousePress(e, mPos)
        this.mPos = mPos
    }

    override fun onDraw(g: Graphics2D) {
        super.onDraw(g)

        if(foreground is StatefulDrawable)
            (foreground as StatefulDrawable).draw(g, 0f, 0f, layoutSize.w, layoutSize.h, state)
        else
            foreground?.draw(g, 0f, 0f, layoutSize.w, layoutSize.h)

        // draw the ripple
        ripple?.draw(g, 0f, 0f, layoutSize.w, layoutSize.h, mPos.x.toFloat(), mPos.y.toFloat())
    }

}