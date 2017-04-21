package com.asdev.libjam.md.tests

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.menu.ContextMenuText
import com.asdev.libjam.md.theme.COLOR_PRIMARY_TEXT
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.DIM_UNLIMITED
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.BUTTON_TYPE_FLAT
import com.asdev.libjam.md.view.ButtonView
import com.asdev.libjam.md.view.View
import java.awt.Dimension

/**
 * Created by Asdev on 11/17/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */

fun main(args: Array<String>) {
    THEME.init()

    val adapter = object : ListLayoutAdapter {

        override fun getItemCount() = 40

        override fun bindView(parent: ListLayout, view: View, index: Int) {
        }

        override fun constructView(index: Int): View {
            val v = ButtonView("${index + 1}", BUTTON_TYPE_FLAT)
            v.minSize = FloatDim(DIM_UNLIMITED.w, 100f)
            v.setThemeColor(COLOR_PRIMARY_TEXT)
            v.contextMenuItems = listOf(ContextMenuText("Menu ${index + 1}"))

            return v
        }

    }

    val child = ListLayout(adapter)

    val layout = ScrollLayout(child)
    layout.background = ColorDrawable(THEME.getBackgroundColor())

    val frame = GLG2DRootView(layout, "List Test", Dimension(500, 500), true)
    frame.showFrame()

    while(true) {
        Thread.sleep(3000)
    }
}