package com.asdev.libjam.md.view

import com.asdev.libjam.md.animation.DecelerateInterpolator
import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.animation.LinearInterpolator
import com.asdev.libjam.md.theme.COLOR_ACCENT
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.FloatDim
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

/**
 * Created by Asdev on 11/24/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.view
 */

/**
 * A view that shows indeterminate circular progress.
 */
class CircularProgressView : View() {

    /**
     * The radius of the circle to draw.
     */
    var circleRadius: Float = THEME.getCircularProgressRadius()

    /**
     * The width of the stroke to draw.
     */
    var circleStrokeWidth: Float = THEME.getCircularProgressStrokeWidth()

    /**
     * The color of the circle to draw.
     */
    var color: Color = THEME.getAccentColor()

    /*
     * The properties to use from the theme.
     */
    private var themeCircleRadius = true
    private var themeCircleStrokeWidth = true
    private var themeColor = COLOR_ACCENT

    /**
     * Variables that determine the arc's position.
     */
    private var arcX = 0f
    private var arcY = 0f

    /**
     * Parameters for the arc.
     */
    private var angAI = 0f
    private var angAF = 0f
    private var angBI = 30f
    private var angBF = 300f

    private var angAAnim = FloatValueAnimator(710f, DecelerateInterpolator, 0f, 0f, 0f)
    private var angBAnim = FloatValueAnimator(710f, DecelerateInterpolator, 0f, 0f, 0f)

    /**
     * The stroke of the arc.
     */
    private var stroke = BasicStroke(circleStrokeWidth)

    /**
     * The animator for the starting angle.
     */
    private var startAngleAnim = FloatValueAnimator(2000f, LinearInterpolator, 0f, 0f, 360f)

    init {
        startAngleAnim.start()
        updateAnim()
    }

    /**
     * Set whether or not the use the circle radius as defined in the theme.
     */
    fun setUseThemeCircleRadius(b: Boolean) {
        themeCircleRadius = b

        if(themeCircleRadius) {
            circleRadius = THEME.getCircularProgressRadius()
        }
    }

    /**
     * Set whether or not the use the circle stroke width as defined in the theme.
     */
    fun setUseThemeCircleStrokeWidth(b: Boolean) {
        themeCircleStrokeWidth = b

        if(themeCircleStrokeWidth) {
            circleStrokeWidth = THEME.getCircularProgressStrokeWidth()
        }

        updateStroke()
    }

    /**
     * Set whether or not to use rounded caps on the stroke.
     */
    fun setRoundedCaps(b: Boolean) {
        if(b)
            stroke = BasicStroke(stroke.lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        else
            stroke = BasicStroke(stroke.lineWidth)
    }

    /**
     * Updates the internal stroke to the new parameters.
     */
    fun updateStroke() {
        stroke = BasicStroke(circleStrokeWidth)
    }

    /**
     * Set which color from the theme to use or -1 to not use any.
     */
    fun setThemeColor(which: Int) {
        themeColor = which

        if(themeColor != -1)
            color = THEME.getColor(themeColor)
    }

    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)
        // update the theme params if they apply
        setThemeColor(themeColor)
        setUseThemeCircleRadius(themeCircleRadius)
        setUseThemeCircleStrokeWidth(themeCircleStrokeWidth)
    }

    /**
     * Loops and animates this circular progress view.
     */
    override fun loop() {
        super.loop()

        // stage the animations
        // loop if needed
        if(startAngleAnim.hasEnded())
            startAngleAnim.start()

        if(angAAnim.hasEnded() || angBAnim.hasEnded())
            updateAnim()

        startAngleAnim.loop()

        requestRepaint()
    }

    private var growing = true
    private var swap = 0
    private fun updateAnim() {
        growing = !growing

        swap --

        if(growing) {
            // a stays put, b changes
            angAI = (angAF)
            angBI = safeAng(angAF + 30f)
            angBF = angBI + 270f
            if(angBF > angAF) {
                // reverse the direction
                swap = 2
            }
        } else {
            // b stays put, a changes
            angBI = (angBF)
            angAI = safeAng(angBI + 60f)
            angAF = angAI + 270f
            if(angAF > angBF) {
                swap = 2
            }
        }

        // update the a and b animators
        angAAnim.setFromValue(angAI).setToValue(angAF).start()
        angBAnim.setFromValue(angBI).setToValue(angBF).start()
    }

    override fun onLayout(newSize: FloatDim) {
        super.onLayout(newSize)
        // center the arc within the size
        arcX = (newSize.w - circleRadius * 2f) / 2f
        arcY = (newSize.h - circleRadius * 2f) / 2f
    }

    /**
     * Draws the circular progress view.
     */
    override fun onDraw(g: Graphics2D) {
        super.onDraw(g)

        val angA = angAAnim.getValue().toInt()
        val angB = angBAnim.getValue().toInt()

        g.color = color
        g.stroke = stroke
        if(swap > 0 || angB - angA > 0) {
            g.drawArc(arcX.toInt(),
                    arcY.toInt(),
                    circleRadius.toInt() * 2,
                    circleRadius.toInt() * 2,
                    angA + startAngleAnim.getValue().toInt(), // relative to starting point
                    angB - angA) // also relative to starting point
        } else if(angB - angA < 0) {
            g.drawArc(arcX.toInt(),
                    arcY.toInt(),
                    circleRadius.toInt() * 2,
                    circleRadius.toInt() * 2,
                    angA + startAngleAnim.getValue().toInt(), // relative to starting point
                    360 + (angB - angA)) // also relative to starting point
        }
    }

    /**
     * Converts the input angle to a proper angle between 0-360
     */
    private fun safeAng(f: Float): Float {
        val a = f % 360
        if(a < 0f)
            return a + 360f
        else
            return a
    }
}