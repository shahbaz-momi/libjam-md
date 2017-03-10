package com.asdev.libjam.md.xml

import com.asdev.libjam.md.drawable.Drawable
import com.asdev.libjam.md.layout.GenericParamList
import com.asdev.libjam.md.util.FloatDim
import java.awt.Color
import java.awt.Font

/**
 * Created by Asdev on 03/09/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.xml
 */

/**
 * A parameter list generating from a XML file.
 */
class XMLParamList: GenericParamList() {

    override fun hasParam(name: String): Boolean {
        return super.hasParam(name)
    }

    fun hasParam(name: Pair<String, String>): Boolean {
        return super.hasParam(name.first)
    }

    override fun getParam(name: String): Any? {
        return super.getParam(name)
    }

    fun getParam(name: Pair<String, String>): Any? {
        return super.getParam(name.first)
    }

    override fun clearParam(name: String) {
        super.clearParam(name)
    }

    fun clearParam(name: Pair<String, String>) {
        super.clearParam(name.first)
    }

    override fun putParam(name: String, value: Any) {
        super.putParam(name, value)
    }

    fun putParam(name: Pair<String, String>, value: Any) {
        super.putParam(name.first, value)
    }

    fun getInt(name: Pair<String, String>): Int? {
        if(!hasParam(name))
            return null

        val value = getParam(name)
        // check the value
        if(value is Int) {
            return value
        } else {
            return null
        }
    }

    fun getString(name: Pair<String, String>): String? {
        if(!hasParam(name))
            return null

        val value = getParam(name)
        // check the value
        if(value is String) {
            return value
        } else {
            return null
        }
    }

    fun getColor(name: Pair<String, String>): Color? {
        if(!hasParam(name))
            return null

        val value = getParam(name)
        // check the value
        if(value is Color) {
            return value
        } else {
            return null
        }
    }

    fun getDrawable(name: Pair<String, String>): Drawable? {
        if(!hasParam(name))
            return null

        val value = getParam(name)
        // check the value
        if(value is Drawable) {
            return value
        } else {
            return null
        }
    }

    fun getFont(name: Pair<String, String>): Font? {
        if(!hasParam(name))
            return null

        val value = getParam(name)
        // check the value
        if(value is Font) {
            return value
        } else {
            return null
        }
    }

    fun getDim(name: Pair<String, String>): FloatDim? {
        if(!hasParam(name))
            return null

        val value = getParam(name)
        // check the value
        if(value is FloatDim) {
            return value
        } else {
            return null
        }
    }

}