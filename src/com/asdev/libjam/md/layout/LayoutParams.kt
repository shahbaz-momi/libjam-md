package com.asdev.libjam.md.layout

import com.asdev.libjam.md.util.DEBUG
import com.asdev.libjam.md.util.DIM_UNSET
import com.asdev.libjam.md.util.FloatDim
import java.util.*

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

open class LayoutParams(var minSize: FloatDim, var maxSize: FloatDim) {

    override fun toString() = "LayoutParams[minSize=$minSize,maxSize=$maxSize]"

    open fun newInstance() = newLayoutParams()

    open fun applyAdditional(params: GenericLayoutParamList?) {

    }
}

fun newLayoutParams() = LayoutParams(DIM_UNSET, DIM_UNSET)

/**
 * Gravities used by various layouts
 */
val GRAVITY_TOP_LEFT = 0
val GRAVITY_TOP_MIDDLE = 1
val GRAVITY_TOP_RIGHT = 2
val GRAVITY_MIDDLE_LEFT = 3
val GRAVITY_MIDDLE_MIDDLE = 4
val GRAVITY_MIDDLE_RIGHT = 5
val GRAVITY_BOTTOM_LEFT = 6
val GRAVITY_BOTTOM_MIDDLE = 7
val GRAVITY_BOTTOM_RIGHT = 8

val GRAVITY_ARRAY_ROW_LEFT = arrayOf(GRAVITY_TOP_LEFT, GRAVITY_MIDDLE_LEFT, GRAVITY_BOTTOM_LEFT)
val GRAVITY_ARRAY_ROW_MIDDLE = arrayOf(GRAVITY_TOP_MIDDLE, GRAVITY_MIDDLE_MIDDLE, GRAVITY_BOTTOM_MIDDLE)
val GRAVITY_ARRAY_ROW_RIGHT = arrayOf(GRAVITY_TOP_RIGHT, GRAVITY_MIDDLE_RIGHT, GRAVITY_BOTTOM_RIGHT)

val LAYOUT_PARAM_GRAVITY = "gravity"

class RelativeLayoutParams(minSize: FloatDim, maxSize: FloatDim, var gravity: Int = GRAVITY_MIDDLE_MIDDLE): LayoutParams(minSize, maxSize) {

    override fun toString() = "RelativeLayoutParams[minSize=$minSize,maxSize=$maxSize,gravity=$gravity]"

    override fun newInstance() = newRelativeLayoutParams()

    override fun applyAdditional(params: GenericLayoutParamList?) {
        if(DEBUG) {
            println("[RelativeLayoutParams] Applying additional layout params from generic layout param list: $params")
        }
        if(params == null)
            return

        // check if there is a gravity
        if(params.hasParam(LAYOUT_PARAM_GRAVITY)) {
            val p = params.getParam(LAYOUT_PARAM_GRAVITY)
            // apply it
            if(p != null && p is Int) {
                gravity = p
            }
        }
    }
}

fun newRelativeLayoutParams() = RelativeLayoutParams(DIM_UNSET, DIM_UNSET, GRAVITY_MIDDLE_MIDDLE)

val ANCHOR_INSIDE = 0
val ANCHOR_ABOVE = 1
val ANCHOR_BELOW =  2
val ANCHOR_TO_LEFT = 3
val ANCHOR_TO_RIGHT = 4

val LAYOUT_PARAM_ANCHOR = "anchor"

class OverlayLayoutParams(minSize: FloatDim, maxSize: FloatDim, var gravity: Int = GRAVITY_MIDDLE_MIDDLE, var anchor: Int = ANCHOR_INSIDE): LayoutParams(minSize, maxSize) {

    override fun newInstance() = newOverlayLayoutParams()

    override fun applyAdditional(params: GenericLayoutParamList?) {
        if(DEBUG) {
            println("[RelativeLayoutParams] Applying additional layout params from generic layout param list: $params")
        }
        if(params == null)
            return

        // check if there is a gravity
        if(params.hasParam(LAYOUT_PARAM_GRAVITY)) {
            val p = params.getParam(LAYOUT_PARAM_GRAVITY)
            // apply it
            if(p != null && p is Int) {
                gravity = p
            }
        }

        if(params.hasParam(LAYOUT_PARAM_ANCHOR)) {
            val p = params.getParam(LAYOUT_PARAM_ANCHOR)
            // apply it
            if(p != null && p is Int) {
                anchor = p
            }
        }
    }
}

fun newOverlayLayoutParams() = OverlayLayoutParams(DIM_UNSET, DIM_UNSET, GRAVITY_MIDDLE_MIDDLE, ANCHOR_INSIDE)


class LinearLayoutParams(minSize: FloatDim, maxSize: FloatDim, var gravity: Int = GRAVITY_MIDDLE_MIDDLE): LayoutParams(minSize, maxSize) {

    override fun applyAdditional(params: GenericLayoutParamList?) {
        if(DEBUG) {
            println("[LinearLayoutParams] Applying additional layout params from generic layout param list: $params")
        }

        if(params == null)
            return

        // check if there is a gravity
        if(params.hasParam(LAYOUT_PARAM_GRAVITY)) {
            val p = params.getParam(LAYOUT_PARAM_GRAVITY)
            // apply it
            if(p != null && p is Int) {
                gravity = p
            }
        }
    }

}

fun newLinearLayoutParams() = LinearLayoutParams(DIM_UNSET, DIM_UNSET, GRAVITY_MIDDLE_MIDDLE)

class GenericLayoutParamList {

    // a map of the actual values
    private val map = HashMap<String, Any>()

    /**
     * Puts a layout parameter onto this list.
     */
    fun putParam(name: String, value: Any) = map.put(name, value)

    /**
     * Returns whether this list has a parameter.
     */
    fun hasParam(name: String) = map.containsKey(name)

    /**
     * Clears a layout parameter from this list.
     */
    fun clearParam(name: String) = map.remove(name)

    /**
     * Gets a layout parameter from this list or null if it isn't present.
     */
    fun getParam(name: String): Any? {
        val value = map.getOrDefault(name, Unit)

        if(value == Unit)
            return null

        return value
    }

    infix fun with(param: Pair<String, Any>): GenericLayoutParamList {
        putParam(param.first, param.second)
        return this
    }
}