package com.asdev.libjam.md.view

import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.COLOR_PRIMARY_TEXT
import com.asdev.libjam.md.theme.FONT_PRIMARY
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.DEBUG_LAYOUT_BOXES
import com.asdev.libjam.md.util.FloatDim
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D

/**
 * Created by Asdev on 10/13/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.view
 */

/**
 * A [View] that displays the specified text.
 */
open class TextView(var text: String): View() {

    /**
     * The [Font] to be used by this view.
     */
    lateinit var font: Font

    /**
     * The [Color] to draw the text in.
     */
    lateinit var color: Color

    /**
     * The gravity of the text to draw.
     */
    var gravity = GRAVITY_MIDDLE_MIDDLE

    /**
     * The [Font] to be used from the theme. Will be update every time in [onThemeChange]. Use -1 to not use a theme font.
     */
    private var themeFont = FONT_PRIMARY

    /**
     * The [Color] to be used from the theme. Will be update every time in [onThemeChange]. Use -1 to not use a theme color
     */
    private var themeColor = COLOR_PRIMARY_TEXT

    /**
     * The paddings to use when drawing the text.
     */
    var paddingTop = 0f
    var paddingBottom = 0f
    var paddingRight = 0f
    var paddingLeft = 0f

    init {
        // load in the theme font
        setThemeFont(themeFont)

        setThemeColor(themeColor)

        zIndex = 1
    }

    fun setPadding(padding: Float) {
        paddingTop = padding
        paddingBottom = padding
        paddingRight = padding
        paddingLeft = padding
    }

    /**
     * Sets the theme font to use.
     */
    fun setThemeFont(newThemeFont: Int) {
        themeFont = newThemeFont
        if(themeFont != -1)
            font = THEME.getFont(themeFont)
    }

    /**
     * Sets the theme color to use.
     */
    fun setThemeColor(newThemeColor: Int) {
        themeColor = newThemeColor
        if(themeColor != -1)
            color = THEME.getColor(themeColor)
    }

    /**
     * [TextView] implementation of [onThemeChange]. Will update the [Font] and [Color] used by this [TextView].
     */
    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)

        // update the font
        setThemeFont(themeFont)

        // update the color
        setThemeColor(themeColor)
    }

    /**
     * Draws the text with the specified attributes.
     */
    override fun onDraw(g: Graphics2D) {
        // draw the background and stuff
        super.onDraw(g)
        // set the font and color
        g.font = font
        g.color = color

        // compute the size of the text
        val textSize = FloatDim(
                g.fontMetrics.stringWidth(text).toFloat() + paddingLeft + paddingRight,
                g.fontMetrics.ascent.toFloat() - g.fontMetrics.descent.toFloat() + (g.fontMetrics.height - g.fontMetrics.ascent).toFloat() + paddingTop + paddingBottom
        )

        // draw the text within the size according to the gravity.
        val x = calculateXComp(gravity, 0f, layoutSize.w, textSize.w)
        val y = calculateYComp(gravity, 0f, layoutSize.h, textSize.h)

        // draw the text at that spot
        g.drawString(text, x + paddingLeft, y + textSize.h - (g.fontMetrics.height - g.fontMetrics.ascent) / 2 - paddingTop) // shift height by text size h because we are drawing from the basline
        if(DEBUG_LAYOUT_BOXES) {
            g.color = Color.RED
            g.drawRect(x.toInt(), y.toInt(), textSize.w.toInt(), textSize.h.toInt())
        }
    }

    /**
     * Calculates the x-component of the corresponding gravity.
     * @param bX bounding box X
     * @param bW bounding box W
     * @param oW object W
     */
    private fun calculateXComp(gravity: Int, bX: Float, bW: Float, oW: Float): Float {
        if(gravity == GRAVITY_TOP_LEFT ||
                gravity == GRAVITY_MIDDLE_LEFT ||
                gravity == GRAVITY_BOTTOM_LEFT) {
            // just return bounding box x
            return bX
        } else if(gravity == GRAVITY_TOP_RIGHT ||
                gravity == GRAVITY_MIDDLE_RIGHT ||
                gravity == GRAVITY_BOTTOM_RIGHT) {
            // align to the right
            return bX + bW - oW
        } else {
            // gravity has to be a center
            return bX + bW / 2f - oW / 2f
        }
    }

    /**
     * Calculates the y-component of the corresponding gravity.
     * @param bY bounding box Y
     * @param bH bounding box H
     * @param oH object H
     */
    private fun calculateYComp(gravity: Int, bY: Float, bH: Float, oH: Float): Float {
        if(gravity == GRAVITY_TOP_LEFT ||
                gravity == GRAVITY_TOP_MIDDLE ||
                gravity == GRAVITY_TOP_RIGHT) {
            // just return 0f
            return bY
        } else if(gravity == GRAVITY_MIDDLE_LEFT ||
                gravity == GRAVITY_MIDDLE_MIDDLE ||
                gravity == GRAVITY_MIDDLE_RIGHT) {
            // align to the middle
            return bY + bH / 2f - oH / 2f
        } else {
            // align to bottom
            return bY + bH - oH
        }
    }
}