package com.asdev.libjam.md.drawable

import com.asdev.libjam.md.view.View
import java.awt.Graphics2D

/**
 * Created by Asdev on 10/18/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.drawable
 */

/**
 * An animatable drawable.
 */
abstract class AnimatedDrawable: StatefulDrawable() {

    /**
     * Returns whether or not to draw a new frame.
     */
    abstract fun requestFrame(): Boolean

    /**
     * Draws this animatable drawable with the given state with an invalid progress (-1f).
     */
    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float, state: View.State) {
        draw(g, x, y, w, h, state, -1f)
    }

    /**
     * Draws this drawable with the specified progress (from 0.0 - 1.0).
     */
    abstract fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float, state: View.State, progress: Float)

}