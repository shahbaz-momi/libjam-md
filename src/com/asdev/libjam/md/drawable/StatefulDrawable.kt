package com.asdev.libjam.md.drawable

import com.asdev.libjam.md.view.View
import java.awt.Graphics2D

/**
 * Created by Asdev on 10/17/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.drawable
 */

/**
 * A drawable that draws according to the passed in state, or STATE_NORMAL if no state is passed in.
 */
abstract class StatefulDrawable: Drawable() {

    /**
     * Draws the drawable with STATE_NORMAL.
     */
    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float) = draw(g, x, y, w, h, View.State.STATE_NORMAL)

    /**
     * Draws the drawable with the specified state.
     */
    abstract fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float, state: View.State)

}