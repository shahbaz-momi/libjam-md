package com.asdev.libjam.md.xml

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.Drawable
import com.asdev.libjam.md.util.FloatDim
import res.R
import java.awt.Color
import java.awt.Font

/**
 * Created by Asdev on 03/07/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.xml
 */


/**
 * A reference to the R strings class.
 */
private val stringsClass = R.strings.javaClass

/**
 * Parses the input string from a raw xml reference to a valid string literal.
 */
fun parseStringReference(ref: String): String {
    if(ref.startsWith("\${") && ref.endsWith("}")) {
        // is a reference to an object
        // grab the actual code reference
        val codeRef = ref.substring( ref.indexOf("{") + 1, ref.lastIndexOf("}") )
        // must be reference to the R object
        if(codeRef.startsWith("R.strings.")) {
            val fieldName = codeRef.replace("R.strings.", "").replace("-", "_") // get the name and format it
            // use reflection to get the field
            try {
                val field = stringsClass.getDeclaredField(fieldName)
                // force access
                field.isAccessible = true
                return field.get(null) as String
            } catch (e: NoSuchFieldException) {
                throw XMLParseException("Invalid string code reference: $codeRef. No such string.")
            }
        } else {
            throw XMLParseException("Invalid string code reference: $codeRef. Must reference a R.string object.")
        }
    } else {
        // plain string literal
        return ref
    }
}

/**
 * A reference to the R ints class.
 */
private val intsClass = R.ints.javaClass

/**
 * Parses the input string from a raw xml reference to a valid int literal.
 */
fun parseIntReference(ref: String): Int {
    if(ref.startsWith("\${") && ref.endsWith("}")) {
        // is a reference to an object
        // grab the actual code reference
        val codeRef = ref.substring( ref.indexOf("{") + 1, ref.lastIndexOf("}") )
        // must be reference to the R object
        if(codeRef.startsWith("R.ints.")) {
            val fieldName = codeRef.replace("R.ints.", "").replace("-", "_") // get the name and format it
            // use reflection to get the field
            try {
                val field = intsClass.getDeclaredField(fieldName)
                // force access
                field.isAccessible = true
                return field.getInt(null)
            } catch (e: NoSuchFieldException) {
                throw XMLParseException("Invalid int code reference: $codeRef. No such int.")
            }
        } else {
            throw XMLParseException("Invalid int code reference: $codeRef. Must reference a R.int object.")
        }
    } else {
        // plain int literal
        return ref.toInt()
    }
}

private val colorsClass = R.colors.javaClass
private val themeClass = R.theme.javaClass

/**
 * Parses the given color string into a literal Color object.
 */
fun parseColorReference(colorContent: String): Color {

    // check if its a hex color
    if(colorContent.startsWith("#")) {
        if(colorContent.length == 7) {
            return Color.decode(colorContent)
        } else if(colorContent.length == 9) {
            // rgb color with alpha
            // get the last two characters for the alpha
            val alphaStr = colorContent.substring(7, 9)
            val colorStr = colorContent.substring(0, 7)

            val color = Color.decode(colorStr) // decode the color part
            val alpha = Integer.parseInt(alphaStr, 16) // parse the alpha str as a hex int
            return Color(color.red, color.green, color.blue, alpha)
        } else {
            throw XMLParseException("Invalid hex color $colorContent. It must either be #RRGGBB or #RRGGBBAA.")
        }
    } else if(colorContent.startsWith("rgba", true)) {
        // grab the content between brackets
        val params = colorContent.substring( colorContent.indexOf("(") + 1, colorContent.indexOf(")") )
        val components = params.split(",").map(String::trim) // split based off of commas and trim the contents

        if(components.size != 4) {
            throw XMLParseException("Invalid RGBA Color $colorContent. It must have for parameters, as rgba(r, g, b, a)")
        }

        val red = components[0].toFloat()
        val green = components[1].toFloat()
        val blue = components[2].toFloat()
        val alpha = components[3].toFloat()

        if(red > 255 || green > 255 || blue > 255 || alpha > 255) {
            throw XMLParseException("Invalid RGBA Color $colorContent. Either red, green, blue, green is outside of the valid range.")
        }

        // check if its a float color
        if(red <= 1.0 && green <= 1.0 && blue <= 1.0 && alpha <= 1.0) {
            return Color(red, green, blue, alpha)
        } else {
            // not a float color, a standard int color
            return Color(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
        }
    } else if(colorContent.startsWith("rgb", true)) {
        // grab the content between brackets
        val params = colorContent.substring( colorContent.indexOf("(") + 1, colorContent.indexOf(")") )
        val components = params.split(",").map(String::trim) // split based off of commas and trim the contents

        if(components.size != 3) {
            throw XMLParseException("Invalid RGB Color $colorContent. It must have for parameters, as rgb(r, g, b)")
        }

        val red = components[0].toFloat()
        val green = components[1].toFloat()
        val blue = components[2].toFloat()

        if(red > 255 || green > 255 || blue > 255) {
            throw XMLParseException("Invalid RGB Color $colorContent. Either red, green, blue, green is outside of the valid range.")
        }

        // check if its a float color
        if(red <= 1.0 && green <= 1.0 && blue <= 1.0) {
            return Color(red, green, blue)
        } else {
            // not a float color, a standard int color
            return Color(red.toInt(), green.toInt(), blue.toInt())
        }
    } else if(colorContent.startsWith("hsb", true)) {
        // grab the content between brackets
        val params = colorContent.substring( colorContent.indexOf("(") + 1, colorContent.indexOf(")") )
        val components = params.split(",").map(String::trim) // split based off of commas and trim the contents

        if(components.size != 3) {
            throw XMLParseException("Invalid HSB Color $colorContent. It must have for parameters, as hsb(h, s, b)")
        }

        val hue = components[0].toFloat()
        val saturation = components[1].toFloat()
        val brightness = components[2].toFloat()

        return Color(Color.HSBtoRGB(hue, saturation, brightness))
    } else if(colorContent.startsWith("\${") && colorContent.endsWith("}")){
        // is a reference to an object
        // grab the actual code reference
        val codeRef = colorContent.substring( colorContent.indexOf("{") + 1, colorContent.lastIndexOf("}") )
        // must be reference to the R object
        if(codeRef.startsWith("R.colors.")) {
            val fieldName = codeRef.replace("R.colors.", "").replace("-", "_") // get the name and format it
            // use reflection to get the field
            try {
                val field = colorsClass.getDeclaredField(fieldName)
                // force access
                field.isAccessible = true
                return field.get(null) as Color
            } catch (e: NoSuchFieldException) {
                throw XMLParseException("Invalid color code reference: $codeRef. No such color.")
            }
        } else if(codeRef.startsWith("R.theme.")) { // check if its a theme color
            val fieldName = codeRef.replace("R.theme.", "").replace("-", "_") // get the name and format it
            val field = themeClass.getDeclaredField(fieldName)
            field.isAccessible = true
            val value = field.get(null)

            // make sure it is an color and not like a font
            if(value is Color) {
                return value
            } else {
                throw XMLParseException("The reference $codeRef is not a color reference, but a different type.")
            }
        } else {
            throw XMLParseException("Invalid color code reference: $codeRef. Must reference a R.color object.")
        }
    } else {
        throw XMLParseException("Unknown Color type $colorContent. It must be one of #, rgb, rgba, or hsb")
    }
}

/**
 * A reference to the R drawables class.
 */
private val drawableClass = R.drawables.javaClass

/**
 * Parses the given drawable string into a literal Drawable object.
 */
fun parseDrawableReference(ref: String): Drawable {
    // check if it is a valid drawable reference
    if(ref.startsWith("\${") && ref.endsWith("}")) {
        // is a reference
        // is a reference to an object
        // grab the actual code reference
        val codeRef = ref.substring( ref.indexOf("{") + 1, ref.lastIndexOf("}") )
        // must be reference to the R object
        if(codeRef.startsWith("R.drawables.")) {
            val fieldName = codeRef.replace("R.drawables.", "").replace("-", "_") // get the name and format it
            // use reflection to get the field
            try {
                val field = drawableClass.getDeclaredField(fieldName)
                // force access
                field.isAccessible = true
                return field.get(null) as Drawable
            } catch (e: NoSuchFieldException) {
                throw XMLParseException("Invalid drawable code reference: $codeRef. No such drawable.")
            }
        }
    }

    // isn't a drawable reference, so must be a color reference
    return ColorDrawable(parseColorReference(ref))
}

private val fontsClass = R.fonts.javaClass

/**
 * Parses the given font string into a literal Font object.
 */
fun parseFontReference(ref: String): Font {
    if(ref.startsWith("\${") && ref.endsWith("}")) {
        // is a reference to an object
        // grab the actual code reference
        val codeRef = ref.substring( ref.indexOf("{") + 1, ref.lastIndexOf("}") )
        // must be reference to the R object
        if(codeRef.startsWith("R.fonts.")) {
            var fieldName = codeRef.replace("R.fonts.", "").replace("-", "_") // get the name and format it
            var size = -1
            if(fieldName.contains(":")) {
                // has a font size param
                size = parseIntReference(fieldName.substring(fieldName.indexOf(":") + 1))
                fieldName = fieldName.substring(0, fieldName.indexOf(":"))
            }

            // use reflection to get the field
            try {
                val field = fontsClass.getDeclaredField(fieldName)
                // force access
                field.isAccessible = true
                if(size == -1)
                    return field.get(null) as Font
                else
                    return (field.get(null) as Font).deriveFont(size.toFloat())
            } catch (e: NoSuchFieldException) {
                throw XMLParseException("Invalid font code reference: $codeRef. No such font.")
            }
        } else if(codeRef.startsWith("R.theme.")) {
            var fieldName = codeRef.replace("R.theme.", "").replace("-", "_") // get the name and format it

            var size = -1
            if(fieldName.contains(":")) {
                // has a font size param
                size = parseIntReference(fieldName.substring(fieldName.indexOf(":") + 1))
                fieldName = fieldName.substring(0, fieldName.indexOf(":"))
            }

            val field = themeClass.getDeclaredField(fieldName)
            field.isAccessible = true
            val value = field.get(null)

            // make sure it is an color and not like a font
            if(value is Font) {
                if(size == -1)
                    return value
                else
                    return value.deriveFont(size.toFloat())
            } else {
                throw XMLParseException("The reference $codeRef is not a font reference, but a different type.")
            }
        } else {
            throw XMLParseException("Invalid font code reference: $codeRef. Must reference a R.fonts object.")
        }
    } else {
        throw XMLParseException("Invalid font code reference: $ref. Must reference a R.fonts object.")
    }
}

private val dimsClass = R.dims.javaClass

/**
 * Parses the given dimension string into a literal FloatDim object.
 */
fun parseDimReference(ref: String): FloatDim {
    if(ref.startsWith("\${") && ref.endsWith("}")) {
        // is a reference to an object
        // grab the actual code reference
        val codeRef = ref.substring( ref.indexOf("{") + 1, ref.lastIndexOf("}") )
        // must be reference to the R object
        if(codeRef.startsWith("R.dims.")) {
            val fieldName = codeRef.replace("R.dims.", "").replace("-", "_") // get the name and format it
            // use reflection to get the field
            try {
                val field = dimsClass.getDeclaredField(fieldName)
                // force access
                field.isAccessible = true
                return field.get(null) as FloatDim
            } catch (e: NoSuchFieldException) {
                throw XMLParseException("Invalid dim code reference: $codeRef. No such dim.")
            }
        }
    }

    // split on x
    if(ref.contains("\$")) {
        // contains a reference
        val parts = ref.replace(" ", "").split("}x\$", ignoreCase = true)
        val w = parseIntReference(parts[0] + "}")
        val h = parseIntReference("\$" + parts[1])
        return FloatDim(w.toFloat(), h.toFloat())
    } else {
        val parts = ref.split("x")
        return FloatDim(parts[0].toFloat(), parts[1].toFloat())
    }
}