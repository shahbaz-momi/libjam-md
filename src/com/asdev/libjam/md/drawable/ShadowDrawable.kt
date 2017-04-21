package com.asdev.libjam.md.drawable

import com.asdev.libjam.md.animation.DecelerateInterpolator
import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.animation.LinearInterpolator
import com.asdev.libjam.md.view.View
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by Asdev on 10/14/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.drawable
 */

/**
 * A nine-patch drawable containing the raw shadow.
 */
val shadow = NinePatchDrawable(ImageIO.read(File("assets/shadow.9.png")), false, false)

/**
 * A class that draws a rectangular shadow.
 */
open class ShadowDrawable(var radius: Float = 10f, opacity: Float = 0.3f, var yOffset: Float = 1f): Drawable() {

    /**
     * The composite that will be used when drawing the shadow.
     */
    private val composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity)

    /**
     * Draws the shadow to the specified bounding box.
     */
    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float) {
        // screw the clip for now, just extend dout by the padding
        val prevComp = g.composite
        // apply the alpha composite
        g.composite = composite

        // draw the shadow with the props
        shadow.draw(g, x - radius, y - radius + yOffset, w + radius * 2f, h + radius * 2f + yOffset)

        // clear the composite
        g.composite = prevComp
    }

}

/**
 * A class that draws an animated rectangular shadow.
 */
open class AnimatedShadowDrawable(var radius: Float = 10f, opacity: Float = 0.3f, yOffset: Float = 1f): AnimatedDrawable() {

    /**
     * The composite that will be used when drawing the shadow.
     */
    private val composite: AlphaComposite

    /**
     * The animator for the yOffset of this [Drawable].
     */
    val yOffsetAnimator = FloatValueAnimator(0f, LinearInterpolator, 0f, 0f, 0f)

    /**
     * The y offset of the shadow.
     */
    var yOffset: Float by yOffsetAnimator

    init {
        this.yOffset = yOffset

        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity)
    }

    override fun requestFrame() = yOffsetAnimator.isRunning()

    /**
     * Draws the shadow to the specified bounding box.
     */
    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float, state: View.State, progress: Float) {
        val prevComp = g.composite
        // apply the alpha composite
        g.composite = composite

        // draw the shadow with the props
        shadow.draw(g, x - radius, y - radius + yOffset, w + radius * 2f, h + radius * 2f + yOffset)

        // clear the composite
        g.composite = prevComp
    }
}

/**
 * An animated shadow drawable that creates a hovering animation based off of the state.
 */
class AnimatedHoverShadowDrawable(radius: Float = 10f, opacity: Float = 0.3f, val yOffsetNormal: Float = 1f, val yOffsetHover: Float = 5f, val viewYTransNormal: Float = 0f, val viewYTransHover: Float = -4f, val animDuration: Float = 200f): AnimatedShadowDrawable(radius, opacity, yOffsetNormal) {

    /**
     * Creates a hovering animation based off of the state.
     */
    override fun onStateChanged(bounder: View?, prevState: View.State, newState: View.State) {
        // update the yOffset animator
        if(newState == View.State.STATE_HOVER) {
            // apply a large y offset
            yOffsetAnimator.setFromValue(yOffset).setToValue(yOffsetHover).setInterpolator(DecelerateInterpolator).setDuration(animDuration).start()
            if(bounder != null)
                bounder.translationYAnimator.setFromValue(bounder.translationY).setToValue(viewYTransHover).setDuration(animDuration).setInterpolator(DecelerateInterpolator).start()
        } else if(newState == View.State.STATE_NORMAL) {
            // reset the y offset
            yOffsetAnimator.setFromValue(yOffset).setToValue(yOffsetNormal).setInterpolator(DecelerateInterpolator).setDuration(animDuration).start()
            if(bounder != null)
                bounder.translationYAnimator.setFromValue(bounder.translationY).setToValue(viewYTransNormal).setDuration(animDuration).setInterpolator(DecelerateInterpolator).start()
        } else if(newState == View.State.STATE_PRESSED) {
            yOffsetAnimator.setFromValue(yOffset).setToValue(yOffsetHover + viewYTransHover).setInterpolator(DecelerateInterpolator).setDuration(animDuration).start()
            if(bounder != null)
                bounder.translationYAnimator.setFromValue(bounder.translationY).setToValue(viewYTransNormal).setDuration(animDuration).setInterpolator(DecelerateInterpolator).start()
        } else if(newState == View.State.STATE_FOCUSED) {
            yOffsetAnimator.setFromValue(yOffset).setToValue(yOffsetHover).setInterpolator(DecelerateInterpolator).setDuration(animDuration).start()
            if(bounder != null)
                bounder.translationYAnimator.setFromValue(bounder.translationY).setToValue(viewYTransHover).setDuration(animDuration).setInterpolator(DecelerateInterpolator).start()
        }
    }

}