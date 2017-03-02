package com.asdev.libjam.md.drawable

import com.asdev.libjam.md.animation.AccelerateInterpolator
import com.asdev.libjam.md.animation.DecelerateInterpolator
import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.animation.LinearInterpolator
import com.asdev.libjam.md.view.View
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D

/**
 * Created by Asdev on 10/21/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.drawable
 */

const val HIGHLIGHT_OPACITY = 0.1f
const val RIPPLE_OPACITY = 0.2f

/**
 * A drawable that draws a material ripple effect.
 */
class RippleDrawable(
        /**
         * The color of the ripple.
         */
        var highlight: Color): AnimatedDrawable() {

    private val highlightOpacityAnimator = FloatValueAnimator(500f, AccelerateInterpolator, 0f, 0f, 0f)

    private var highlightOpacity: Float by highlightOpacityAnimator

    private val rippleAnimator = FloatValueAnimator(2000f, LinearInterpolator, 0f, 0f, 0f)

    private val rippleOpacityAnimator = FloatValueAnimator(20f, AccelerateInterpolator, 0f, 0f, 0f)

    private var rippleOpacity: Float by rippleOpacityAnimator

    override fun onStateChanged(bounder: View?, prevState: View.State, newState: View.State) {
        // draw the highlight on hover
        if(newState == View.State.STATE_HOVER) {
            // start the highlight
            highlightOpacityAnimator.setFromValue(highlightOpacity).setToValue(HIGHLIGHT_OPACITY).setDuration(400f).setInterpolator(DecelerateInterpolator).start()
        } else if(newState == View.State.STATE_NORMAL) {
            // end the highlight
            // end the ripple as well
            rippleAnimator.setFromValue(rippleAnimator.getValue()).setToValue(2f).setDuration(340f).setInterpolator(LinearInterpolator).start()
            rippleOpacityAnimator.setFromValue(rippleOpacity).setToValue(0f).setDuration(340f).setInterpolator(DecelerateInterpolator).start()
            highlightOpacityAnimator.setFromValue(highlightOpacity).setToValue(0f).setDuration(400f).setInterpolator(DecelerateInterpolator).start()
        } else if(newState == View.State.STATE_PRESSED) {
            // start the ripple
            rippleAnimator.setFromValue(0f).setToValue(2f).setDuration(1400f).setInterpolator(LinearInterpolator).start()
            rippleOpacityAnimator.setFromValue(rippleOpacity).setToValue(RIPPLE_OPACITY).setDuration(200f).setInterpolator(LinearInterpolator).start()
        } else if(newState == View.State.STATE_FOCUSED) {
            // end the ripple quickly outwards
            // for nougat ripples - around 400ms
            // for marshmellow - around 340ms
            rippleAnimator.setFromValue(rippleAnimator.getValue()).setToValue(2f).setDuration(340f).setInterpolator(LinearInterpolator).start()
            rippleOpacityAnimator.setFromValue(rippleOpacity).setToValue(0f).setDuration(340f).setInterpolator(DecelerateInterpolator).start()
        }
    }

    override fun requestFrame(): Boolean {
        // update calcs in here
        return highlightOpacityAnimator.isRunning() || rippleAnimator.isRunning() || rippleOpacityAnimator.isRunning()
    }

    /**
     * Draws the ripple with the origin at the center of the drawing space.
     */
    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float, state: View.State, progress: Float) = draw(g, x, y, w, h, x + w / 2f, y + h / 2f)

    /**
     * Draws the ripple with the origin at the mx, my.
     */
    fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float, mx: Float, my: Float) {
        // draw the basic highlight
        g.color = highlight
        // apply the composite
        val prevComp = g.composite
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, highlightOpacity)

        val prevClipBounds = g.clip

        g.clipRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())

        g.fillRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())

        val midX = /* x + w / 2f */ mx
        val midY = y + h / 2f

        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, rippleOpacity)

        // old ripple drawing effect - just straight up one oval
//        g.fillOval((midX - w / 2f * rippleAnimator.getValue()).toInt() - w.toInt() / 8,
//                (midY - w / 2f * rippleAnimator.getValue()).toInt() - h.toInt() / 2,
//                (w * rippleAnimator.getValue() ).toInt() + w.toInt() / 4,
//                (w * rippleAnimator.getValue() ).toInt() + h.toInt())

        var rippleX = (midX - w / 2f * rippleAnimator.getValue()).toInt()
        var rippleW = (w * rippleAnimator.getValue() )

        rippleX -= 10

        g.clipRect(rippleX,
                y.toInt(),
                10,
                h.toInt())

        // draw a circle at the end of each ripple
        g.fillOval(
                rippleX,
                y.toInt() - h.toInt() / 2,
                h.toInt(),
                h.toInt() * 2
        )

        rippleX += 20

        g.clip = prevClipBounds

        g.clipRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())

        g.clipRect((rippleX + rippleW).toInt() - 10,
                y.toInt(),
                20,
                h.toInt())

        g.fillOval(
                (rippleX + rippleW - h).toInt(),
                y.toInt() - h.toInt() / 2,
                h.toInt(),
                h.toInt() * 2
        )

        rippleX -= 20
        rippleW += 20

        g.clip = prevClipBounds

        g.clipRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())

        g.fillRect(rippleX + 10, y.toInt() - 1, rippleW.toInt() - 20, h.toInt() + 2)

        g.composite = prevComp
        g.clip = prevClipBounds
    }

}