package com.asdev.libjam.md.animation

import kotlin.reflect.KProperty

/**
 * Created by Asdev on 10/17/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.animation
 */

/**
 * Animates a Float value from the given [from] to the given [to]. Can be used as a property delegate.
 */
class FloatValueAnimator(duration: Float, interpolator: Interpolator, startDelay: Float, private var from: Float, private var to: Float): Animator(duration, interpolator, startDelay) {

    /**
     * Stores the difference between [from] and [to]
     */
    private var delta: Float = 0f
    private var assignedValue = 0f

    init {
        delta = to - from
        assignedValue = to
    }

    /**
     * Resets the [from] value of this animator.
     */
    fun setFromValue(from: Float): FloatValueAnimator {
        if(isRunning())
            cancel()

        this.from = from
        delta = to - from

        return this
    }

    /**
     * Resets the [to] value of this animator.
     */
    fun setToValue(to: Float): FloatValueAnimator {
        if(isRunning())
            cancel()

        assignedValue = to

        this.to = to
        delta = to - from

        return this
    }

    override fun getRawValue(): Float {
        return getProgress() * delta + from
    }

    override fun getValue(): Float {
        return if(!isRunning()) assignedValue else getInterpolator().getValue(getProgress()) * delta + from
    }

    ////// Delegate functions /////
    /**
     * Returns the animated value of this animator.
     */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Float {
        return if(!isRunning()) assignedValue else getValue()
    }

    /**
     * Settings the value on this animator will cancel the animation and assign the value.
     */
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
        assignedValue = value
    }

    /**
     * Sets the assigned value of this Animator.
     */
    fun setAssignedValue(value: Float) {
        assignedValue = value
    }

}