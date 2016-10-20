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
val shadow = NinePatchDrawable(ImageIO.read(File("assets/shadow.9.png")))

/**
 * A class that draws a rectangular shadow.
 */
open class ShadowDrawable(var radius: Float = 30f, opacity: Float = 0.25f, var yOffset: Float = 3f, val clipLeft: Boolean = false, val clipRight: Boolean = false, drawMiddlePatch: Boolean = true): Drawable() {

    /**
     * The composite that will be used when drawing the shadow.
     */
    private val composite: AlphaComposite

    /**
     * The local shadow to draw with.
     */
    private val shadowCache: NinePatchDrawable

    init {
        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity)
        shadowCache = NinePatchDrawable(shadow.ninepatch, true, drawMiddlePatch)
    }

    /**
     * Draws the shadow to the specified bounding box.
     */
    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float) {
        // screw the clip for now, just extend out by the radius
        val prevClip = g.clip
        val prevComp = g.composite
        g.clip = null
        val xOffset = if(clipLeft) radius.toInt() else 0
        val wOffset = if(clipRight) radius.toInt() * 2 else 0
        g.clipRect(x.toInt() - radius.toInt() + xOffset, y.toInt() - radius.toInt() + yOffset.toInt(), w.toInt() + radius.toInt() * 2 - wOffset, h.toInt() + radius.toInt() * 2 + yOffset.toInt())
        // apply the alpha composite
        g.composite = composite

        // draw the shadow with the props
        shadowCache.draw(g, x - radius, y - radius + yOffset, w + radius * 2f, h + radius * 2f + yOffset)

        // reapply the clip
        g.clip = prevClip
        // clear the composite
        g.composite = prevComp
    }

}

/**
 * A class that draws an animated rectangular shadow.
 */
open class AnimatedShadowDrawable(var radius: Float = 30f, opacity: Float = 0.25f, yOffset: Float = 3f, val clipLeft: Boolean = false, val clipRight: Boolean = false, drawMiddlePatch: Boolean = true): AnimatedDrawable() {

    /**
     * The composite that will be used when drawing the shadow.
     */
    private val composite: AlphaComposite

    /**
     * The local shadow to draw with.
     */
    private val shadowCache: NinePatchDrawable

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
        shadowCache = NinePatchDrawable(shadow.ninepatch, true, drawMiddlePatch)
    }

    override fun requestFrame() = !yOffsetAnimator.hasEnded() && yOffsetAnimator.getStartTime() > 0L

    /**
     * Draws the shadow to the specified bounding box.
     */
    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float, state: View.State, progress: Float) {
        // screw the clip for now, just extend out by the radius
        val prevClip = g.clip
        val prevComp = g.composite
        g.clip = null
        val xOffset = if(clipLeft) radius.toInt() else 0
        val wOffset = if(clipRight) radius.toInt() * 2 else 0
        g.clipRect(x.toInt() - radius.toInt() + xOffset, y.toInt() - radius.toInt() + yOffset.toInt(), w.toInt() + radius.toInt() * 2 - wOffset, h.toInt() + radius.toInt() * 2 + yOffset.toInt())
        // apply the alpha composite
        g.composite = composite

        // draw the shadow with the props
        shadowCache.draw(g, x - radius, y - radius + yOffset, w + radius * 2f, h + radius * 2f + yOffset)

        // reapply the clip
        g.clip = prevClip
        // clear the composite
        g.composite = prevComp
    }
}

/**
 * An animated shadow drawable that creates a hovering animation based off of the state.
 */
class AnimatedHoverShadowDrawable(radius: Float = 30f, opacity: Float = 0.25f, val yOffsetNormal: Float = 3f, val yOffsetHover: Float = 9f, val viewYTransNormal: Float = 0f, val viewYTransHover: Float = -5f, val animDuration: Float = 200f): AnimatedShadowDrawable(radius, opacity, yOffsetNormal, false, false, false) {

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