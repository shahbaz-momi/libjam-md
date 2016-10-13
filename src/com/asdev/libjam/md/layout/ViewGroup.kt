package com.asdev.libjam.md.layout

import com.asdev.libjam.md.view.View

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

abstract class ViewGroup: View() {

    abstract fun addChild(child: View)
    abstract fun removeChild(child: View)
    abstract fun getChildren(): Array<View>

    open fun getChildCount() = getChildren().size

    override fun loop() {
        super.loop()

        for(i in 0 until getChildCount()) {
            getChildAtIndex(i).loop()
        }
    }

    open fun getChildAtIndex(i: Int): View {
        if(i < 0 || i >= getChildCount())
            throw ArrayIndexOutOfBoundsException("There is no child with the specified index ($i)")

        return getChildren()[i]
    }
}