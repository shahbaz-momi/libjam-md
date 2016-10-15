package com.asdev.libjam.md.drawable

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
class ShadowDrawable(val radius: Float = 30f, opacity: Float = 0.25f, val yOffset: Float = 3f, val clipLeft: Boolean = false, val clipRight: Boolean = false): Drawable() {

    /**
     * The composite that will be used when drawing the shadow.
     */
    private val composite: AlphaComposite

    init {
        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity)
    }

    /**
     * Draws the shadow to the specified bounding box.
     */
    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float) {
        // screw the clip for now, just extend out by the radius
        val prevClip = g.clip
        val prevComp = g.composite
        g.clip = null
        // translate to the left radius
        // g.translate(-radius.toDouble(), -radius.toDouble())
        val xOffset = if(clipLeft) radius.toInt() else 0
        val wOffset = if(clipRight) radius.toInt() * 2 else 0
        g.clipRect(x.toInt() - radius.toInt() + xOffset, y.toInt() - radius.toInt() + yOffset.toInt(), w.toInt() + radius.toInt() * 2 - wOffset, h.toInt() + radius.toInt() * 2 + yOffset.toInt())
        // apply the alpha composite
        g.composite = composite
        // draw the shadow with the props
        shadow.draw(g, x - radius, y - radius + yOffset, w + radius * 2f, h + radius * 2f + yOffset)
        // reapply the clip
        // g.translate(radius.toDouble(), radius.toDouble())
        g.clip = prevClip
        // clear the composite
        g.composite = prevComp
    }

}