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
class ShadowDrawable(val radius: Float = 30f, opacity: Float = 0.3f, val yOffset: Float = 3f): Drawable() {

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
        // apply the alpha composite
        g.composite = composite
        // draw the shadow with the props
        shadow.draw(g, x - radius, y - radius + yOffset, w + radius * 2f, h + radius * 2f + yOffset)
        // reapply the clip
        g.clip = prevClip
        // clear the composite
        g.composite = prevComp
    }

}