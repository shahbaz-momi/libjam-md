package com.asdev.libjam.md.thread

import com.asdev.libjam.md.util.DEBUG
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.thread
 */

class Looper(val loopable: Loopable): Thread() {

    private val isRunning = AtomicBoolean(false)
    private val loopDelay: Long

    private val msgQueue = ConcurrentLinkedQueue<Message>()

    init {
        loopDelay = (1000.0 / loopable.loopsPerSecond()).toLong()
    }

    /**
     * Post a message to this looper's queue. Will be handled by the Loopable.
     */
    fun postMessage(msg: Message) = msgQueue.add(msg)

    /**
     * Safely stop this looper.
     */
    fun stopSafe() {
        isRunning.set(false)
    }

    // java-thread implementation of run
    override fun run() {
        if(DEBUG)
            println("[Looper] Starting $name looper...")
        isRunning.set(true)

        while(isRunning.get()) {

            // handle all of the messages in the queue
            var e = msgQueue.poll()
            while(e != null) {
                loopable.handleMessage(e)
                e = msgQueue.poll()
            }

            loopable.loop()

            Thread.sleep(loopDelay)
        }
    }

    override fun toString(): String {
        return "Looper:" + name
    }
}

fun myLooper() = if(Thread.currentThread() != null && Thread.currentThread() is Looper) Thread.currentThread() as Looper else null