package com.asdev.libjam.md.util

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.util
 */

/**
 * A dimension holding two [Float]s pertaining to width ($w) and height ($h).
 */
data class FloatDim(
        /**
         * The width of this dimension.
         */
        var w: Float,
        /**
         * The height of this dimension.
         */
        var h: Float) {

    override fun toString(): String {
        return "FloatDim[w=$w,h=$h]"
    }
}

/**
 * Constant float dimensions
 */
val DIM_UNSET = FloatDim(-1f, -1f)
val DIM_UNLIMITED = FloatDim(-2f, -2f)

/**
 * Returns whether the given [FloatDim], $d, fits in between the [Pair] of dimensions.
 */
infix fun Pair<FloatDim, FloatDim>.fits(d: FloatDim) =
        // actually compare the values
        (d.w >= first.w && d.w <= second.w && d.h >= first.h && d.h <= second.h) ||
        // check if either of them are unset/unlimited
        ((first == DIM_UNLIMITED || first == DIM_UNSET) && (second == DIM_UNLIMITED || second == DIM_UNSET)) ||
        // check min
        ((first != DIM_UNLIMITED && first != DIM_UNSET && d.w >= first.w && d.h >= first.h) && (second == DIM_UNLIMITED || second == DIM_UNSET)) ||
        // check max aswell
        ((first == DIM_UNLIMITED || first == DIM_UNSET) && (second != DIM_UNLIMITED && second != DIM_UNSET && d.w <= second.w && d.h <= second.h))

val POINT_UNSET = FloatPoint(-1f, -1f)

/**
 * A point holding two [Float]s pertaining to x and y positions.
 */
data class FloatPoint(
        /**
         * The x-element of this point.
         */
        var x: Float,
        /**
         * The y-element of this point.
         */
        var y: Float) {

    operator fun plus(other: FloatPoint) = FloatPoint(x + other.x, y + other.y)
    operator fun minus(other: FloatPoint) = FloatPoint(x - other.x, y - other.y)
    operator fun times(other: FloatPoint) = FloatPoint(x * other.x, y * other.y)
    operator fun div(other: FloatPoint) = FloatPoint(x / other.x, y / other.y)


    override fun toString(): String {
        return "FloatPoint[x=$x,y=$y]"
    }

}