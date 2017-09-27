package com.asdev.libjam.md.drawable

import com.asdev.libjam.md.animation.FactorableDecelerateInterpolator
import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.view.View
import res.R
import java.awt.Graphics2D

/**
 * Created by Asdev on 04/26/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.drawable
 */

private const val STROKE = 2
private const val SPACING = 6

private const val LENGTH = 18
private const val HEIGHT = 14

private const val OFFSET = 2

/**
 * An array containing the x components for the top rectangle.
 */
private val topXsStart = intArrayOf(0, 0, LENGTH, LENGTH)
/**
 * An array containing the y components for the top rectangle.
 */
private val topYsStart = intArrayOf(0, STROKE, STROKE, 0)

private val topXsEnd = intArrayOf(0, 0, LENGTH / 2, LENGTH / 2)

private val topYsEnd = intArrayOf(SPACING, STROKE + SPACING, STROKE - OFFSET, -OFFSET)

/**
 * An array containing the x components for the bottom rectangle.
 */
private val bottomXsStart = intArrayOf(0, 0, LENGTH, LENGTH)
/**
 * An array containing the y components for the bottom rectangle.
 */
private val bottomYsStart = intArrayOf(SPACING * 2, STROKE + SPACING * 2, STROKE + SPACING * 2, SPACING * 2)

private val bottomXsEnd = intArrayOf(0, 0, LENGTH / 2, LENGTH / 2)

private val bottomYsEnd = intArrayOf(SPACING, SPACING + STROKE, SPACING * 2 + STROKE + OFFSET, SPACING * 2 + OFFSET)

/**
 * Draws an animated hamburger icon that can transition from the back button to a hamburger stack.
 */
class HamburgerDrawable: AnimatedDrawable() {

    /**
     * A cache for x-components.
     */
    private val pointCacheX = topXsStart.clone()
    /**
     * A cache for y-components.
     */
    private val pointCacheY = topYsStart.clone()

    private val animator = FloatValueAnimator(500f, FactorableDecelerateInterpolator(1.3f), 0f, 0f, 1f).apply { setAssignedValue(0f) }

    private var state = false

    override fun onStateChanged(bounder: View?, prevState: View.State, newState: View.State) {
        super.onStateChanged(bounder, prevState, newState)

        // toggle the state and animation
        if(prevState == View.State.STATE_PRESSED && newState == View.State.STATE_POST_PRESS) {
            setState(!state) // TODO: remove this automatic state changing
        }
    }

    /**
     * Sets the state of this hamburger drawable, with true being fully animated into a back button and false being
     * the original hamburger.
     */
    fun setState(state: Boolean) {
        if(!this.state) {
            animator.setFromValue(0f).setToValue(1f).start()
        } else {
            animator.setFromValue(1f).setToValue(0f).start()
        }

        this.state = state
    }

    override fun requestFrame(): Boolean {
        animator.loop()
        return animator.isRunning()
    }

    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float, state: View.State, progress: Float) {
        g.color = R.theme.title

        // center within the drawing spacing
        val cx = x + (w - LENGTH) / 2.0
        val cy = y + (h - HEIGHT) / 2.0

        g.translate(cx, cy)

        val prog = animator.getValue()

        if(this.state) {
            g.rotate(Math.toRadians(prog * 180.0 + 180.0), LENGTH / 2.0, HEIGHT / 2.0)
        } else {
            g.rotate(Math.toRadians((1.0 - prog + 1.0) * 180.0 + 180.0), LENGTH / 2.0, HEIGHT / 2.0)
        }

        // draw the top points animated using the progress
        for(i in pointCacheX.indices) {
            pointCacheX[i] = lerp(topXsStart[i], topXsEnd[i], prog)
            pointCacheY[i] = lerp(topYsStart[i], topYsEnd[i], prog)
        }


        g.fillPolygon(pointCacheX, pointCacheY, 4)

        g.fillRect(0, SPACING, LENGTH, STROKE)

        // draw the bottom points animated using the progress
        for(i in pointCacheX.indices) {
            pointCacheX[i] = lerp(bottomXsStart[i], bottomXsEnd[i], prog)
            pointCacheY[i] = lerp(bottomYsStart[i], bottomYsEnd[i], prog)
        }

        g.fillPolygon(pointCacheX, pointCacheY, 4)

        if(this.state) {
            g.rotate(Math.toRadians(-prog * 180.0 + 180.0), LENGTH / 2.0, HEIGHT / 2.0)
        } else {
            g.rotate(Math.toRadians(-(1.0 - prog + 1.0) * 180.0 + 180.0), LENGTH / 2.0, HEIGHT / 2.0)
        }

        g.translate(-cx, -cy)
    }

}

/**
 * Linear interpolates the given values.
 */
private fun lerp(start: Int, end: Int, progress: Float) = (start + (end - start) * progress).toInt()