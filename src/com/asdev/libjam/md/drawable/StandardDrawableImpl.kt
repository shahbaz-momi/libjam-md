package com.asdev.libjam.md.drawable

import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.drawable
 *
 */

/**
 * A standard drawable that draws a solid color.
 */
class ColorDrawable: Drawable {

    var color: Color

    constructor(c: Color) {
        color = c
    }

    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float) {
        g.color = color
        g.fillRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    }

}

val SCALE_TYPE_CONTAIN = 0
val SCALE_TYPE_COVER = 1
val SCALE_TYPE_FIT = 2

class ImageDrawable: Drawable {

    val img: BufferedImage
    var scaleType: Int = SCALE_TYPE_CONTAIN

    constructor(img: BufferedImage) {
        this.img = img
        scaleType = SCALE_TYPE_CONTAIN
    }

    constructor(img: BufferedImage, scaleType: Int) {
        this.img = img
        this.scaleType = scaleType
    }

    /**
     * Draws the image to this drawable according to the scale type.
     */
    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float) {
        val clipP = g.clip

        g.setClip(x.toInt(), y.toInt(), w.toInt(), h.toInt())
        // check scaling mode
        if(scaleType == SCALE_TYPE_FIT) {
            // stretch w and h to fit the bounding size
            g.drawImage(img, x.toInt(), y.toInt(), w.toInt(), h.toInt(), null)
        } else if(scaleType == SCALE_TYPE_CONTAIN) {
            val sfh = h / img.height
            val newW = img.width * sfh

            val sfw = w / img.width
            val newH = img.height * sfw

            if(newH > h) {
                // center the image horizontally
                g.drawImage(img, x.toInt() + ((w - newW) / 2f).toInt(), y.toInt(), newW.toInt(), h.toInt(), null)
            } else {
                g.drawImage(img, x.toInt(), y.toInt() + ((h - newH) / 2f).toInt(), w.toInt(), newH.toInt(), null)
            }
        } else if(scaleType == SCALE_TYPE_COVER) {
            val sfh = h / img.height
            val newW = img.width * sfh

            // width is the width of the view
            val sfw = w / img.width
            val newH = img.height * sfw

            if(newH < h) {
                // center the image horizontally
                g.drawImage(img, x.toInt() + ((w - newW) / 2f).toInt(), y.toInt(), newW.toInt(), h.toInt(), null)
            } else {
                g.drawImage(img, x.toInt(), y.toInt() + ((h - newH) / 2f).toInt(), w.toInt(), newH.toInt(), null)
            }
        }

        // set the clip back
        g.clip = clipP
    }
}

/**
 * Turns multiple drawables into one.
 */
class CompoundDrawable(vararg drawables: Drawable): Drawable() {

    private val drawables: Array<out Drawable>

    init {
        this.drawables = drawables
    }

    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float) {
        for(d in drawables)
            d.draw(g, x, y, w, h)
    }

}