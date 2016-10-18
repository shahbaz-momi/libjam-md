package com.asdev.libjam.md.drawable

import java.awt.Graphics2D

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.drawable
 */

/**
 * An object that may drawn to the screen with the given parameters.
 */
abstract class Drawable {

    /**
     * Draw this drawable at the specified coordinates (x, y) with the specified size (w, h)
     */
    abstract fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float)

}