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

/**
 * A class that handles looping operations and manages them onto a single [Thread]. Also, it handles the synchronization
 * of messages between itself and other threads.
 */
class Looper(val loopable: Loopable): Thread() {

    /**
     * Whether this [Looper] is running or not.
     */
    private val isRunning = AtomicBoolean(false)
    /**
     * The delay between each [Loopable] loop().
     */
    private val loopDelay: Long

    /**
     * The message queue for this [Looper]. This will store messages until the next loop() and then call handleMessage() of the [Loopable].
     */
    private val msgQueue = ConcurrentLinkedQueue<Message>()

    init {
        loopDelay = (1000.0 / loopable.loopsPerSecond()).toLong()
    }

    /**
     * Post a message to this looper's queue. Will be handled by the Loopable.
     */
    fun postMessage(msg: Message) = msgQueue.add(msg)

    /**
     * Safely stops this looper.
     */
    fun stopSafe() {
        isRunning.set(false)
    }

    // java-thread implementation of run
    /**
     * The implementation of [run] for this [Thread].
     */
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

    /**
     * Returns this class and the name of this [Looper]
     */
    override fun toString(): String {
        return "Looper:" + name
    }
}

/**
 * Returns the [Looper] associated with your [Thread] or null if no [Looper] is associated.
 */
fun myLooper() = if(Thread.currentThread() != null && Thread.currentThread() is Looper) Thread.currentThread() as Looper else null