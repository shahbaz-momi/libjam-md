package com.asdev.libjam.md.layout

import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.view.View

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

/**
 * A [View] that holds multiple other [View] children.
 */
abstract class ViewGroup: View() {

    /**
     * Adds the specified child to this [ViewGroup]
     */
    abstract fun addChild(child: View)

    /**
     * Removes the specified child from this [ViewGroup]
     */
    abstract fun removeChild(child: View)

    /**
     * Returns the children managed by this [ViewGroup]
     */
    abstract fun getChildren(): Array<View>

    /**
     * Returns the number of children managed by this [ViewGroup]
     */
    open fun getChildCount() = getChildren().size

    /**
     * Implementation of [onThemeChange]. Calls the [View] implementation and then notifies its children of the theme change.
     */
    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)

        // theme change the children as well
        for(c in getChildren())
            c.onThemeChange(prevTheme, newTheme)
    }

    /**
     * Implementation of [loop]. Calls the [View] implementation and then loops its children.
     */
    override fun loop() {
        super.loop()

        for(i in 0 until getChildCount()) {
            getChildAtIndex(i).loop()
        }
    }

    /**
     * Returns the child [View] at the specified index ($i).
     */
    open fun getChildAtIndex(i: Int): View {
        if(i < 0 || i >= getChildCount())
            throw ArrayIndexOutOfBoundsException("There is no child with the specified index ($i)")

        return getChildren()[i]
    }

    /**
     * Returns the view with the specified id, or null if none found.
     */
    open fun findViewById(id: String): View? {
        if(this.id == id) {
            return this
        }

        for(i in 0 until getChildCount()) {
            val child = getChildAtIndex(i)
            if(child is ViewGroup) {
                val v = child.findViewById(id)
                if(v != null)
                    return v
            } else if(child.id == id){
                return child
            }
        }

        return null
    }
}