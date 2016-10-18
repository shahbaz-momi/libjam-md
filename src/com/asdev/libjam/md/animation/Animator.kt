package com.asdev.libjam.md.animation

/**
 * Created by Asdev on 10/17/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.animation
 */

/**
 * Manages the duration, interpolator, and start time for an animation.
 */
open class Animator(
        /**
         * The duration of the animation in milliseconds.
         */
        private var duration: Float,
        /**
         * The interpolator of this animation.
         */
        private var interpolator: Interpolator = LinearInterpolator,
        /**
         * The start delay of this animation in milliseconds
         */
        private var startDelay: Float) {

    private var startTime = -1L
    private var endTime = -1L

    /**
     * Starts this animations
     */
    open fun start() {
        if(startTime != -1L)
            throw IllegalStateException("This animation is already started!")

        startTime = System.nanoTime() + (startDelay * 1000000.0f).toLong()
        endTime = startTime + (duration * 1000000.0f).toLong()
    }

    /**
     * Cancels and resets this animator.
     */
    open fun cancel() {
        startTime = -1L
        endTime = -1L
    }

    /**
     * Returns whether this animation has ended or not yet.
     */
    open fun hasEnded() = System.nanoTime() >= endTime

    /**
     * Returns the progress (between 0.0 - 1.0) of this animation.
     */
    open fun getProgress(): Float {
        if(hasEnded())
            return 1f
        else if(startTime == -1L || System.nanoTime() < startTime)
            return 0f
        else
            return (System.nanoTime() - startTime).toFloat() / (endTime - startTime).toFloat()
    }

    /**
     * Returns the raw (non-interpolated) value of this animation.
     */
    open fun getRawValue() = getProgress()

    /**
     * Returns the interpolated value of this animation.
     */
    open fun getValue() = interpolator.getValue(getRawValue())

    /**
     * Loops/updates this animator. Calls this on the UILooper to keep it in sync.
     */
    open fun loop() {
        // we don't serve any special function
    }

    /**
     * Resets the duration of this animator.
     */
    open fun setDuration(duration: Float): Animator {
        if(!hasEnded())
            cancel()

        this.duration = duration

        return this
    }

    /**
     * Resets the start delay of this animator.
     */
    open fun setStartDelay(delay: Float): Animator {
        if(!hasEnded())
            cancel()

        this.startDelay = delay

        return this
    }

    /**
     * Resets the interpolator of this animator.
     */
    open fun setInterpolator(interpolator: Interpolator): Animator {
        if(!hasEnded())
            cancel()

        this.interpolator = interpolator

        return this
    }

    /**
     * Returns the duration of this animator.
     */
    fun getDuration() = duration

    /**
     * Returns the start delay of this animator.
     */
    fun getStartDelay() = startDelay

    /**
     * Returns the interpolator of this animator.
     */
    fun getInterpolator() = interpolator
}