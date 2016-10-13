package com.asdev.libjam.md.util

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.util
 */
data class FloatDim(var w: Float, var h: Float) {

    override fun toString(): String {
        return "FloatDim[w=$w,h=$h]"
    }
}

/** Constants for float dimensions
 */
val DIM_UNSET = FloatDim(-1f, -1f)
val DIM_UNLIMITED = FloatDim(-2f, -2f)

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

data class FloatPoint(var x: Float, var y: Float) {

    override fun toString(): String {
        return "FloatPoint[x=$x,y=$y]"
    }

}