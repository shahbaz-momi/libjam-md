package com.asdev.libjam.md.drawable

import java.awt.Color
import java.awt.Graphics2D

/**
 * Created by Asdev on 10/20/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.drawable
 */

/**
 * A drawable that draws a rounded rectangle with the given padding.
 */
class RoundedRectangleDrawable(var color: Color, var radius: Float): Drawable() {

    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float) {
        g.color = color
        g.fillRoundRect(x.toInt(), y.toInt(), w.toInt(), h.toInt(), radius.toInt(), radius.toInt())
    }

}