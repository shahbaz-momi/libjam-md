package com.asdev.libjam.md.xml

import com.asdev.libjam.md.layout.ViewGroup
import com.asdev.libjam.md.view.View
import org.w3c.dom.Element
import java.io.File
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

    // inflate the single element
    val root = inflateSingleElement(rootElement)

    if(root is ViewGroup) {
        // TODO
    }

    return root
}

/**
 * Inflates the single element with its attributes.
 */
private fun inflateSingleElement(element: Element): View {
    // list to store the attributes in
    val list = XMLParamList()
    val tagName = element.tagName

    val definedParams = ArrayList<Pair<String, String>>()

    // get the name of the element
    val className = getQualifiedName(tagName)
    // find the attributes
    // check if its from the default packages
    if(!tagName.contains(".")) {
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
            throw XMLParseException("View class not found in attributes: $tagName")
        }
    } else {
        try {
            val clazz = Class.forName(className.replace(".",  "_"))
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
            throw XMLParseException("View class not found in attributes: $tagName")
        }
    }

    // get the raw xml attrs
    val xmlAttrs = element.attributes
    for(i in 0 until xmlAttrs.length) {
        val attr = xmlAttrs.item(i)
        val attrName = attr.nodeName
        val attrValue = attr.textContent

        val definition = definedParams.find { it.first == attrName } ?: throw XMLParseException("Attribute $attrName is not defined in the attrs class for view $tagName")
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
                list.putParam(definition, attrValue)
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