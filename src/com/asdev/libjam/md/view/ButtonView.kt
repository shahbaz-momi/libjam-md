package com.asdev.libjam.md.view

import com.asdev.libjam.md.drawable.*
import com.asdev.libjam.md.layout.GRAVITY_MIDDLE_MIDDLE
import com.asdev.libjam.md.theme.COLOR_ACCENT
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import java.awt.Color
import java.awt.Cursor
import java.awt.Graphics2D

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
class ButtonView(text: String, val type: Int = BUTTON_TYPE_RAISED): TextView(text) {

    private var themeBgColor = COLOR_ACCENT

    /**
     * The foreground of this button. Usually, this will be a ripple effect.
     */
    private var foreground: Drawable? = null // TODO: ripple foreground

    init {
        // set the background to a hover shadow and a rounded rectangle based on the accent color
        if(type == BUTTON_TYPE_RAISED)
            background =
                    AnimatedCompoundDrawable(
                            AnimatedHoverShadowDrawable(opacity = 0.3f),
                            RoundedRectangleDrawable(THEME.getAccentColor(), 6f)
                    )
        // if its a flat style then background should be null
        else if(type == BUTTON_TYPE_FLAT) {
            setThemeColor(COLOR_ACCENT)
            background = null
        }

        gravity = GRAVITY_MIDDLE_MIDDLE

        // set the foreground to the ripple
    }

    override fun loop() {
        super.loop()

        if(foreground != null && foreground is AnimatedDrawable) {
            if ((foreground!! as AnimatedDrawable).requestFrame()) {
                requestRepaint()
            }
        }
    }

    override fun onStateChanged(previous: State, newState: State) {
        super.onStateChanged(previous, newState)

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
    }

    /**
     * Sets this background color according to the theme.
     */
    fun setThemeBackgroundColor(color: Int) {
        themeBgColor = color
        if(themeBgColor != -1)
            setBackgroundColor(THEME.getAccentColor())
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

    override fun onDraw(g: Graphics2D) {
        super.onDraw(g)

        // draw the foreground
        val fg = foreground
        if(fg is StatefulDrawable)
            fg.draw(g, 0f, 0f, layoutSize.w, layoutSize.h, state)
        else
            fg?.draw(g, 0f, 0f, layoutSize.w, layoutSize.h)
    }

}