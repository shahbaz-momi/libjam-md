package com.asdev.libjam.md.xml

import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.view.View
import org.w3c.dom.Element
import java.io.File
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by Asdev on 03/07/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.xml
 */

private val documentBuilderFactory = DocumentBuilderFactory.newInstance()


/**
 * Inflates the passed layout reference string.
 */
fun inflateLayout(layoutRef: String): View {
    // grab the xml content
    val docBuilder = documentBuilderFactory.newDocumentBuilder()
    val document = docBuilder.parse(File(layoutRef))

    val rootElement = document.documentElement

    return inflateLayout0(rootElement)
}

private fun inflateLayout0(rootElement: Element): View {
    val root = inflateSingleElement(rootElement)

    if(root is ViewGroup && rootElement.hasChildNodes()) {
        val children = rootElement.childNodes
        for(i in 0 until children.length) {
            val child = children.item(i) as? Element ?: continue
            root.addChild(inflateLayout0(child))
        }
    } else if(rootElement.hasChildNodes()) {
        throw XMLParseException("Non-ViewGroup element cannot have children. ${rootElement.tagName} does.")
    }

    return root
}

/**
 * Inflates the single element with its attributes.
 */
@Suppress("UNCHECKED_CAST") // we know the type because it is auto-generated
private fun inflateSingleElement(element: Element): View {
    // list to store the attributes in
    val list = XMLParamList()
    val tagName = element.tagName

    val definedParams = ArrayList<Pair<String, String>>()

    val className: String

    // get the name of the element
    if(tagName == "CustomView") {
        className = element.attributes.getNamedItem("class").textContent
        element.attributes.removeNamedItem("class")
    } else {
        className = getQualifiedName(tagName)
    }

    // find the attributes
    // check if its from the default packages
    if(!tagName.contains(".") && tagName != "CustomView") {
        // not a fully qualified name so it must be from the default packages
        // lookup the name in the attrs class
        try {
            val clazz = Class.forName("res.R\$attrs\$$tagName")
            // get the defined params
            val fields = clazz.declaredFields

            for(f in fields) {
                f.isAccessible = true
                val value = f.get(null)

                if(value is Pair<*, *>)
                    definedParams.add(value as Pair<String, String>)
            }

        } catch (e: ClassNotFoundException) {
            // lookup failed; no such class
            throw XMLParseException("View class not found in attributes: $className")
        }
    } else {
        try {
            val clazz = Class.forName("res.R\$attrs\$" + className.replace(".",  "_"))
            // get the defined params
            val fields = clazz.declaredFields

            for(f in fields) {
                f.isAccessible = true
                val value = f.get(null)

                if(value is Pair<*, *>)
                    definedParams.add(value as Pair<String, String>)
            }

        } catch (e: ClassNotFoundException) {
            // lookup failed; no such class
            throw XMLParseException("View class not found in attributes: $className")
        }
    }

    // get the raw xml attrs
    val xmlAttrs = element.attributes
    for(i in 0 until xmlAttrs.length) {
        val attr = xmlAttrs.item(i)
        val attrName = attr.nodeName
        val attrValue = attr.textContent

        val definition = definedParams.find { it.first == attrName } ?: throw XMLParseException("Attribute $attrName is not defined in the attrs class for view $className")
        // use the definition type to figure out type
        val type = definition.second

        when (type) {

            type_string -> {
                list.putParam(definition, parseStringReference(attrValue))
            }

            type_int -> {
                list.putParam(definition, parseIntReference(attrValue))
            }

            type_color -> {
                list.putParam(definition, parseColorReference(attrValue))
            }

            type_drawable -> {
                list.putParam(definition, parseDrawableReference(attrValue))
            }

            type_font -> {
                list.putParam(definition, parseFontReference(attrValue))
            }

            type_float -> {
                list.putParam(definition, attrValue.toFloat())
            }

            type_gravity -> {
                var integer = -1
                when(attrValue) {
                    "top-left" -> integer = GRAVITY_TOP_LEFT
                    "top-middle" -> integer = GRAVITY_TOP_MIDDLE
                    "top-right" -> integer = GRAVITY_TOP_RIGHT
                    "middle-left" -> integer = GRAVITY_MIDDLE_LEFT
                    "middle-middle" -> integer = GRAVITY_MIDDLE_MIDDLE
                    "middle-right" -> integer = GRAVITY_MIDDLE_RIGHT
                    "bottom-left" -> integer = GRAVITY_BOTTOM_LEFT
                    "bottom-middle" -> integer = GRAVITY_BOTTOM_MIDDLE
                    "bottom-right" -> integer = GRAVITY_BOTTOM_RIGHT
                }

                list.putParam(definition, integer)
            }

            type_dim -> {
                list.putParam(definition, parseDimReference(attrValue))
            }

        }
    }

    try {
        val view = Class.forName(className).newInstance() as? View ?: throw XMLParseException("The given view $className is not actually a view.")

        view.applyParameters(list)

        return view
    } catch (e: InstantiationException) {
        throw XMLParseException("The given view $className does not have a public nullinary constructor.")
    }
}

/**
 * Converts the given simplified class name into a fully qualified class name.
 */
fun getQualifiedName(referenceName: String): String {
    if(!referenceName.contains(".")) {
        // is a default class view type
        // try the view and layout packages
        try {
            Class.forName("com.asdev.libjam.md.view.$referenceName")
            return "com.asdev.libjam.md.view.$referenceName"
        } catch(e: ClassNotFoundException) {
            try {
                Class.forName("com.asdev.libjam.md.layout.$referenceName")
                return "com.asdev.libjam.md.layout.$referenceName"
            } catch (e: ClassNotFoundException) {
                throw XMLParseException("Unable to find qualified class name for $referenceName")
            }
        }
    } else {
        // should already be of qualified type
        return referenceName
    }
}