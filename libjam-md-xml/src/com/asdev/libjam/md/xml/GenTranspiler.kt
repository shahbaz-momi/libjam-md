package com.asdev.libjam.md.xml

import org.w3c.dom.Element
import java.awt.Color
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by Asdev on 03/03/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.xml
 */

val R_OBJECT_TEMPLATE = "package res\n" +
        "import com.asdev.libjam.md.layout.*\n" +
        "import com.asdev.libjam.md.theme.THEME\n" +
        "import com.asdev.libjam.md.theme.Theme\n" +
        "import com.asdev.libjam.md.util.generateRandomId\n" +
        "import java.awt.Color\n" +
        "import java.awt.Font\n" +
        "import java.io.File\n" +
        "\n" +
        "object R {\n" +
        "%1\$s" +
        "\n\tobject gravity {\n" +
        "\n" +
        "\t\tval top_left = GRAVITY_TOP_LEFT\n" +
        "\t\tval top_middle = GRAVITY_TOP_MIDDLE\n" +
        "\t\tval top_right = GRAVITY_TOP_RIGHT\n" +
        "\t\tval middle_left = GRAVITY_MIDDLE_LEFT\n" +
        "\t\tval middle_middle = GRAVITY_MIDDLE_MIDDLE\n" +
        "\t\tval middle_right = GRAVITY_MIDDLE_RIGHT\n" +
        "\t\tval bottom_left = GRAVITY_BOTTOM_LEFT\n" +
        "\t\tval bottom_middle = GRAVITY_BOTTOM_MIDDLE\n" +
        "\t\tval bottom_right = GRAVITY_BOTTOM_RIGHT\n" +
        "\n" +
        "\t}\n" +
        "}"

val ROOT_ELEMENT_STRINGS = "Strings"
val ELEMENT_STRING_ENTRY = "String"

val STRING_ENTRY_TEMPLATE = "val %1\$s = \"%2\$s\""
val STRING_OBJECT_TEMPLATE = "\tobject strings {\n" +
        "%1\$s\n" +
        "\t}\n"

val ROOT_ELEMENT_INTS = "Ints"
val ELEMENT_INT_ENTRY = "Int"

val INT_ENTRY_TEMPLATE = "val %1\$s = %2\$s"
val INT_OBJECT_TEMPLATE = "\tobject ints {\n" +
        "%1\$s\n" +
        "\t}\n"

val ROOT_ELEMENT_ATTRS = "Attributes"
val ELEMENT_ATTR_ENTRY = "Styleable"
val ELEMENT_PROPERTY_ENTRY = "Property"

val ROOT_ELEMENT_COLORS = "Colors"
val ELEMENT_COLOR_ENTRY = "Color"

val COLOR_ENTRY_TEMPLATE = "val %1\$s = %2\$s"
val COLOR_OBJECT_TEMPLATE = "\tobject colors {\n" +
        "%1\$s\n" +
        "\t}\n"

val ROOT_ELEMENT_LAYOUTS = "Layouts"


fun main(args: Array<String>) {
    // get the parameters
    var inputFolder: String? = null
    var outputFolder: String? = null

    // process the parameters
    for (i in 0 until args.size) {
        if (args[i] == "--help") {
            println("Usage: gen-transpiler --input [input folder] --output [output folder]")
            return
        } else if (args[i] == "--input") {
            if (i + 1 >= args.size) {
                println("Input not specified!")
                return
            }
            // the next arg will be the input folder
            inputFolder = args[i + 1]
        } else if (args[i] == "--output") {
            if (i + 1 >= args.size) {
                println("Output not specified!")
                return
            }
            // the next arg will be the input folder
            outputFolder = args[i + 1]
        }
    }

    if (inputFolder == null) {
        println("No input specified")
        return
    } else if (outputFolder == null) {
        println("No output specified")
        return
    }

    val inputDir = File(inputFolder)
    val outputDir = File(outputFolder)
    run(inputDir, outputDir)

    println("Writing to RGen.kt")
    // build based off the entries of each type
    val rObject = buildRObject()
    val outputFile = File(File(outputDir, "res"), "RGen.kt")
    outputFile.delete()
    outputDir.mkdirs()

    val writer = BufferedWriter(FileWriter(outputFile, false))
    writer.write(rObject)
    writer.flush()
    writer.close()
    println("Done. Finished with no errors.")
}

/**
 * Transpiles the input xml value files into Kotlin objects.
 */
private val documentBuilderFactory = DocumentBuilderFactory.newInstance()

/**
 * Parses the files in the input directory/file out to the output directory.
 */
fun run(inputDir: File, outputDir: File) {
    // get all directories and files in this dir that end with xml or are directories
    val files = inputDir.listFiles { f: File, s: String -> f.isDirectory || s.endsWith(".xml", true) }

    for (file in files) {
        if (file.isDirectory) {
            run(file, outputDir)
        } else {
            println("Parsing file $file")
            // read the contents of the file
            val docBuilder = documentBuilderFactory.newDocumentBuilder()
            val document = docBuilder.parse(file)

            // determine the type of file it is
            // and send it to parse
            val rootElement = document.documentElement

            when (rootElement.nodeName) {
                ROOT_ELEMENT_STRINGS -> {
                    try {
                        parseStrings(rootElement)
                    } catch (e: XMLParseException) {
                        e.printStackTrace()
                        System.exit(-1)
                    }
                }

                ROOT_ELEMENT_INTS -> {
                    try {
                        parseInts(rootElement)
                    } catch (e: XMLParseException) {
                        e.printStackTrace()
                        System.exit(-1)
                    }
                }

                ROOT_ELEMENT_COLORS -> {
                    try {
                        parseColors(rootElement)
                    } catch (e: XMLParseException) {
                        e.printStackTrace()
                        System.exit(-1)
                    }
                }

                ROOT_ELEMENT_ATTRS -> {

                }

                ROOT_ELEMENT_LAYOUTS -> {

                }
            }

            println("Parsed successfully!")

        }
    }
}


val colorEntries = ArrayList<String>()
val colorEntryNames = ArrayList<String>()

/**
 * Parses a file of colors using the given element.
 */
fun parseColors(element: Element) {

    // get all children that are Strings
    val nodes = element.getElementsByTagName(ELEMENT_COLOR_ENTRY)
    for (i in 0 until nodes.length) {
        val node = nodes.item(i)

        // get the name attribute
        val colorName = node.attributes.getNamedItem("name").textContent
        // grab the contents of the string
        val colorContent = node.textContent

        // make sure it has a name
        if (colorName == null) {
            throw XMLParseException("All Color entries must have a name attribute. Entry number ${i + 1} does not.")
        }
        // make sure it has content
        if(colorContent == null || colorContent.isEmpty()) {
            throw XMLParseException("All Color entries must have content. Entry number ${i + 1} does not.")
        }

        // replace any dashes with underscores to fit naming convenetions
        val formattedName = colorName.replace("-", "_")

        if(colorEntryNames.contains(formattedName)) {
            throw XMLParseException("Color entry already exists with the name $formattedName")
        }

        var colorString = ""

        // check if its a hex color
        if(colorContent.startsWith("#")) {
            if(colorContent.length == 7) {
                val color = Color.decode(colorContent)
                // standard rgb color
                colorString = "Color(${color.red}, ${color.green}, ${color.blue})"
            } else if(colorContent.length == 9) {
                // rgb color with alpha
                // get the last two characters for the alpha
                val alphaStr = colorContent.substring(7, 9)
                val colorStr = colorContent.substring(0, 7)

                val color = Color.decode(colorStr) // decode the color part
                val alpha = Integer.parseInt(alphaStr, 16) // parse the alpha str as a hex int
                colorString = "Color(${color.red}, ${color.green}, ${color.blue}, $alpha)"
            } else {
                throw XMLParseException("Invalid hex color for $formattedName: $colorContent. It must either be #RRGGBB or #RRGGBBAA.")
            }
        } else if(colorContent.startsWith("rgba", true)) {
            // grab the content between brackets
            val params = colorContent.substring( colorContent.indexOf("(") + 1, colorContent.indexOf(")") )
            val components = params.split(",").map(String::trim) // split based off of commas and trim the contents

            if(components.size != 4) {
                throw XMLParseException("Invalid RGBA Color for name $formattedName: $colorContent. It must have for parameters, as rgba(r, g, b, a)")
            }

            val red = components[0].toFloat()
            val green = components[1].toFloat()
            val blue = components[2].toFloat()
            val alpha = components[3].toFloat()

            if(red > 255 || green > 255 || blue > 255 || alpha > 255) {
                throw XMLParseException("Invalid RGBA Color for name $formattedName: $colorContent. Either red, green, blue, green is outside of the valid range.")
            }

            // check if its a float color
            if(red <= 1.0 && green <= 1.0 && blue <= 1.0 && alpha <= 1.0) {
                val color = Color(red, green, blue, alpha)
                colorString = "Color(${color.red}, ${color.green}, ${color.blue}, ${color.alpha})"
            } else {
                // not a float color, a standard int color
                val color = Color(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
                colorString = "Color(${color.red}, ${color.green}, ${color.blue}, ${color.alpha})"
            }
        } else if(colorContent.startsWith("rgb", true)) {
            // grab the content between brackets
            val params = colorContent.substring( colorContent.indexOf("(") + 1, colorContent.indexOf(")") )
            val components = params.split(",").map(String::trim) // split based off of commas and trim the contents

            if(components.size != 3) {
                throw XMLParseException("Invalid RGB Color for name $formattedName: $colorContent. It must have for parameters, as rgb(r, g, b)")
            }

            val red = components[0].toFloat()
            val green = components[1].toFloat()
            val blue = components[2].toFloat()

            if(red > 255 || green > 255 || blue > 255) {
                throw XMLParseException("Invalid RGB Color for name $formattedName: $colorContent. Either red, green, blue, green is outside of the valid range.")
            }

            // check if its a float color
            if(red <= 1.0 && green <= 1.0 && blue <= 1.0) {
                val color = Color(red, green, blue)
                colorString = "Color(${color.red}, ${color.green}, ${color.blue})"
            } else {
                // not a float color, a standard int color
                val color = Color(red.toInt(), green.toInt(), blue.toInt())
                colorString = "Color(${color.red}, ${color.green}, ${color.blue})"
            }
        } else if(colorContent.startsWith("hsb", true)) {
            // grab the content between brackets
            val params = colorContent.substring( colorContent.indexOf("(") + 1, colorContent.indexOf(")") )
            val components = params.split(",").map(String::trim) // split based off of commas and trim the contents

            if(components.size != 3) {
                throw XMLParseException("Invalid HSB Color for name $formattedName: $colorContent. It must have for parameters, as hsb(h, s, b)")
            }

            val hue = components[0].toFloat()
            val saturation = components[1].toFloat()
            val brightness = components[2].toFloat()

            val color = Color(Color.HSBtoRGB(hue, saturation, brightness))
            colorString = "Color(${color.red}, ${color.green}, ${color.blue})"
        } else {
            throw XMLParseException("Unknown Color type for name $formattedName: $colorString. It must be one of #, rgb, rgba, or hsb")
        }

        println("Added color: $colorString as $formattedName")
        // add to the entries
        val statement = COLOR_ENTRY_TEMPLATE.format(formattedName, colorString) // first parameter is the name, second is the content
        colorEntries.add(statement)
        colorEntryNames.add(formattedName)
    }
}

fun buildColorsObject(): String {
    // take the int entries, merge them with \n, and then replace the template for the object
    val entries = colorEntries.joinToString(separator = "\n\t\t", prefix = "\t\t")
    return COLOR_OBJECT_TEMPLATE.format(entries)
}

val intEntries = ArrayList<String>()
val intEntryNames = ArrayList<String>()

/**
 * Parses a file of ints using the given element.
 */
fun parseInts(element: Element) {

    // get all children that are Strings
    val nodes = element.getElementsByTagName(ELEMENT_INT_ENTRY)
    for (i in 0 until nodes.length) {
        val node = nodes.item(i)

        // get the name attribute
        val intName = node.attributes.getNamedItem("name").textContent
        // grab the contents of the string
        val intContent = node.textContent

        // make sure it has a name
        if (intName == null) {
            throw XMLParseException("All Int entries must have a name attribute. Entry number ${i + 1} does not.")
        }
        // make sure it has content
        if(intContent == null || intContent.isEmpty()) {
            throw XMLParseException("All Int entries must have content. Entry number ${i + 1} does not.")
        }

        // replace any dashes with underscores to fit naming convenetions
        val formattedName = intName.replace("-", "_")
        // make sure there aren't any special characters
        if(containsSpecialCharacters(formattedName))
            throw XMLParseException("Int name $formattedName contains illegal characters! Only alphanumeric characters are allowed and -_")

        if(intEntryNames.contains(formattedName)) {
            throw XMLParseException("Int entry already exists with the name $formattedName")
        }

        // try and parse the int
        try {
            intContent.toInt()
        } catch (e: NumberFormatException) {
            throw XMLParseException("Invalid Int of name $intName with content $intContent")
        }

        println("Added int: $intContent as $formattedName")

        // add to the entries
        val statement = INT_ENTRY_TEMPLATE.format(formattedName, intContent) // first parameter is the name, second is the content
        intEntries.add(statement)
        intEntryNames.add(formattedName)
    }
}

fun buildIntsObject(): String {
    // take the int entries, merge them with \n, and then replace the template for the object
    val entries = intEntries.joinToString(separator = "\n\t\t", prefix = "\t\t")
    return INT_OBJECT_TEMPLATE.format(entries)
}

/**
 * Strings to add to the string object.
 */
val stringEntries = ArrayList<String>()
val stringEntryNames = ArrayList<String>()

/**
 * Parses a file of strings using the given element.
 */
fun parseStrings(element: Element) {

    // get all children that are Strings
    val nodes = element.getElementsByTagName(ELEMENT_STRING_ENTRY)
    for (i in 0 until nodes.length) {
        val node = nodes.item(i)

        // get the name attribute
        val stringName = node.attributes.getNamedItem("name").textContent
        // grab the contents of the string
        val stringContent = node.textContent

        // make sure it has a name
        if (stringName == null) {
            throw XMLParseException("All String entries must have a name attribute. Entry number ${i + 1} does not.")
        }
        // make sure it has content
        if(stringContent == null || stringContent.isEmpty()) {
            throw XMLParseException("All String entries must have content. Entry number ${i + 1} does not.")
        }

        // replace any dashes with underscores to fit naming convenetions
        val formattedName = stringName.replace("-", "_")
        // make sure there aren't any special characters
        if(containsSpecialCharacters(formattedName))
            throw XMLParseException("String name $formattedName contains illegal characters! Only alphanumeric characters are allowed and -_")

        if(stringEntryNames.contains(formattedName)) {
            throw XMLParseException("String entry already exists with the name $formattedName")
        }

        val escapedContent = escapeMetaCharacters(stringContent)

        println("Added string: $escapedContent as $formattedName")

        // add to the entries
        val statement = STRING_ENTRY_TEMPLATE.format(formattedName, escapedContent) // first parameter is the name, second is the content
        stringEntries.add(statement)
        stringEntryNames.add(formattedName)
    }
}

fun buildStringObject(): String {
    // take the string entries, merge them with \n, and then replace the template for the object
    val entries = stringEntries.joinToString(separator = "\n\t\t", prefix = "\t\t")
    return STRING_OBJECT_TEMPLATE.format(entries)
}

fun buildRObject(): String {
    val contents = buildStringObject() + "\n" + buildIntsObject() + "\n" + buildColorsObject()
    // TODO: add other objects
    return R_OBJECT_TEMPLATE.format(contents)
}

fun containsSpecialCharacters(s: String): Boolean {
    for(c in s) {
        if(!c.isLetterOrDigit() && c != '_') {
            return true
        }
    }

    return false
}

fun escapeMetaCharacters(string: String): String {
    var str = string
    val metaCharacters = arrayOf("\\", "$", "\"", "\'")
    for (i in metaCharacters.indices) {
        if (str.contains(metaCharacters[i])) {
            str = str.replace(metaCharacters[i], "\\" + metaCharacters[i])
        }
    }

    str = str.replace("\n", "\\n")

    return str
}
