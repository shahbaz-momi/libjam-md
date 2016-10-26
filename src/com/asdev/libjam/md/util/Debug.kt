package com.asdev.libjam.md.util

/**
 * Created by Asdev on 10/06/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.util
 */

/**
 * Enables debug logging.
 * NOTICE: Using DEBUG will cause significant lag because there will be a lot of output on each onMeasure(), onLayout(), and onDraw()
 */
const val DEBUG = true

/**
 * Enables debug drawing layout boundaries.
 */
const val DEBUG_LAYOUT_BOXES = false

class Debug {

    private var time = -1L

    fun startTimer() {
        time = System.nanoTime()
    }

    fun stopTimer(tag: String) {
        if(DEBUG)
            println("$tag took ${(System.nanoTime() - time) / 1000000.0}ms")
    }

}