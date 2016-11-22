package com.asdev.libjam.md.tests

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.GRAVITY_MIDDLE_MIDDLE
import com.asdev.libjam.md.layout.ListLayout
import com.asdev.libjam.md.layout.ListLayoutAdapter
import com.asdev.libjam.md.layout.ScrollLayout
import com.asdev.libjam.md.theme.COLOR_BACKGROUND
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.ButtonView
import com.asdev.libjam.md.view.TextView
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

        override fun getItemCount() = 50

        override fun bindView(view: View, index: Int) {
            (view as TextView).text = "$index"
        }

        override fun constructView(index: Int): View {
            val v = ButtonView("$index")
            v.gravity = GRAVITY_MIDDLE_MIDDLE
            v.minSize = FloatDim(600f, 100f)
            v.setThemeBackgroundColor(COLOR_BACKGROUND)
            return v
        }

    }

    val child = ListLayout(adapter)

    val layout = ScrollLayout(child)
    layout.background = ColorDrawable(THEME.getBackgroundColor())

    val frame = GLG2DRootView(layout, "List Test", Dimension(500, 500), true)
    frame.showFrame()
}