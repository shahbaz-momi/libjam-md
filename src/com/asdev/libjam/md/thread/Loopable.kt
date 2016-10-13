package com.asdev.libjam.md.thread

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.thread
 */
interface Loopable {

    /**
     * Called when a loop is scheduled.
     * Will be after handleMessage but on the same thread.
     */
    fun loop()

    /**
     * Called when a message is sent to this looper
     * Will be before loop and on the same thread.
     */
    fun handleMessage(msg: Message)

    /**
     * The number of loops to perform per second
     */
    fun loopsPerSecond(): Int

}