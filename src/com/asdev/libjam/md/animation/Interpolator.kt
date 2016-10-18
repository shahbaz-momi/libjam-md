package com.asdev.libjam.md.animation

/**
 * Created by Asdev on 10/16/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.animation
 */
abstract class Interpolator {

    /**
     * Interpolates the given value (between 0.0 - 1.0) to the interpolated value between (0.0 - 1.0).
     * The interpolated value should always give an output of 1.0 for the input of 1.0.
     */
    abstract fun getValue(input: Float): Float

}

/**
 * An interpolator where the rate of change starts and ends slowly but accelerates through the middle.
 */
object AccelerateDecelerateInterpolator: Interpolator() {

    override fun getValue(input: Float) = (Math.cos((input + 1) * Math.PI) / 2.0f).toFloat() + 0.5f

}

/**
 * An interpolator where the rate of change starts out slowly and and then accelerates.
 */
object AccelerateInterpolator: Interpolator() {

    override fun getValue(input: Float) = input * input

}

/**
 * An interpolator where the change starts backward then flings forward.
 */
object AnticipateInterpolator: Interpolator() {

    override fun getValue(input: Float) = input * input * (3.0f * input - 2.0f).toFloat()

}

/**
 * An interpolator where the change bounces at the end.
 */
object BounceInterpolator: Interpolator() {

    private fun bounce(t: Float) = t * t * 8.0f

    override fun getValue(input: Float): Float {
        val t = input * 1.1226f

        if (t < 0.3535f) return bounce(t)
        else if (t < 0.7408f) return bounce(t - 0.54719f) + 0.7f
        else if (t < 0.9644f) return bounce(t - 0.8526f) + 0.9f
        else return bounce(t - 1.0435f) + 0.95f
    }

}

/**
 * An interpolator where the change bounces at the end.
 */
object CycleInterpolator: Interpolator() {

    override fun getValue(input: Float) = (Math.sin(2 * 1f * Math.PI * input)).toFloat()

}

/**
 * An interpolator where the rate of change starts out quickly and and then decelerates.
 */
object DecelerateInterpolator: Interpolator() {

    override fun getValue(input: Float) = (1.0f - (1.0f - input) * (1.0f - input))

}

/**
 * An interpolator where the change flings forward and overshoots the last value then comes back.
 */
object OvershootInterpolator: Interpolator() {

    override fun getValue(input: Float): Float {
        val t = input - 1.0f
        return t * t * ((2.0f + 1.0f) * t + 2.0f) + 1.0f
    }

}

/**
 * An interpolator where the input value is equal to the output value, therefore interpolating the value in a linear fashion.
 */
object LinearInterpolator: Interpolator() {

    override fun getValue(input: Float) = input

}