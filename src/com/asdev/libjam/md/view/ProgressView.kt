package com.asdev.libjam.md.view

import com.asdev.libjam.md.animation.*
import com.asdev.libjam.md.theme.COLOR_ACCENT
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.FloatDim
import java.awt.AlphaComposite
import java.awt.Graphics2D

/**
 * Created by Asdev on 11/26/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.view
 */

/**
 * The type for a progress view where the progress is known.
 */
const val PROGRESS_TYPE_DETERMINATE = 0

/**
 * The type for a progress view where the progress is unknown.
 */
const val PROGRESS_TYPE_INDETERMINATE = 1

private val ALPHA_COMP = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.25f)
private val WIDTH_INTERPOLATOR = FactorableDecelerateInterpolator(1f)
private val START_INTERPOLATOR = FactorableAccelerateInterpolator(1f)

/**
 * A View that shows progress on a horizontal progress bar.
 */
class ProgressView(
        /**
         * The type of progress view.
         */
        val type: Int = PROGRESS_TYPE_INDETERMINATE) : View() {

    /**
     * The amount of spacing between the progress bar to the left.
     */
    var paddingLeft = 12f

    /**
     * The amount of spacing between the progress bar to the right.
     */
    var paddingRight = 12f

    /**
     * The progress of the view.
     */
    private var progress = 1f

    /**
     * The y of the progress bar.
     */
    private var y = 0f

    /**
     * The color of the progress bar.
     */
    var color = THEME.getAccentColor()

    private var themeColor = COLOR_ACCENT

    private var anim = FloatValueAnimator(1500f, LinearInterpolator, 0f, 0f, 1f)

    init {
        if(type == PROGRESS_TYPE_INDETERMINATE) {
            anim.start()
        }
    }

    /**
     * Sets the progress of the View to the given value between [0.0f - 1.0f]
     */
    fun setProgress(f: Float) {
        if(progress < 0f || progress > 1f) {
            throw IllegalArgumentException("The specified progress is invalid.")
        }

        progress = f

        requestRepaint()
    }

    /**
     * Sets the color of this progress bar to the specified color from the color.
     */
    fun setThemeColor(which: Int) {
        themeColor = which

        if(themeColor != -1) {
            color = THEME.getColor(themeColor)
        }
    }

    override fun loop() {
        super.loop()

        if(type == PROGRESS_TYPE_INDETERMINATE) {
            if(anim.hasEnded()) {
                anim.start()
            }

            requestRepaint()
        }
    }

    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)
        // update the y coord
        onLayout(layoutSize)
    }

    override fun onLayout(newSize: FloatDim) {
        super.onLayout(newSize)

        // center the bar within the view.
        y = (layoutSize.h - THEME.getProgressBarWidth()) / 2f
    }

    override fun onDraw(g: Graphics2D) {
        super.onDraw(g)

        val prevComp = g.composite

        val startX = (layoutSize.w * START_INTERPOLATOR.getValue(anim.getValue())).toInt()

        g.composite = ALPHA_COMP
        // draw the background of the bar
        g.color = color
        g.fillRect(paddingLeft.toInt(), y.toInt(), (layoutSize.w - paddingLeft - paddingRight).toInt(), THEME.getProgressBarWidth().toInt())

        // draw the actual bar now
        g.composite = prevComp
        if(type == PROGRESS_TYPE_DETERMINATE) {
            g.fillRect(paddingLeft.toInt(), y.toInt(), ((layoutSize.w - paddingLeft - paddingRight) * progress).toInt(), THEME.getProgressBarWidth().toInt())
        } else if(type == PROGRESS_TYPE_INDETERMINATE) {
           // val prevClip = g.clip
            // g.clipRect(paddingLeft.toInt(), 0, (layoutSize.w - paddingLeft - paddingRight).toInt(), layoutSize.h.toInt())
            g.fillRect(paddingLeft.toInt() + startX, y.toInt(), ((layoutSize.w - paddingLeft - paddingRight) * WIDTH_INTERPOLATOR.getValue(anim.getValue())).toInt() - startX, THEME.getProgressBarWidth().toInt())
            // g.clip = prevClip
        }
    }

}