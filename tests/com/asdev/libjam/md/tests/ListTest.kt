package com.asdev.libjam.md.tests

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.ImageDrawable
import com.asdev.libjam.md.drawable.SCALE_TYPE_COVER
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.DIM_UNLIMITED
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.TextView
import com.asdev.libjam.md.view.View
import java.awt.Dimension
import java.io.File
import javax.imageio.ImageIO

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

        override fun getItemCount() = 3

        override fun bindView(view: View, index: Int) {
            (((view as ElevatedLayout).child as ViewGroup).findViewById("View:CardContent") as TextView).text = "Card number ${index + 1}"
            if(index == 1) {
                val bg = ((view).child as ViewGroup).findViewById("View:CardBg")!!
                bg.background = ImageDrawable(ImageIO.read(File("assets/roadtrip.jpg")), SCALE_TYPE_COVER)
            } else if(index == 2) {
                val bg = ((view).child as ViewGroup).findViewById("View:CardBg")!!
                bg.background = ImageDrawable(ImageIO.read(File("assets/texture.png")), SCALE_TYPE_COVER)
            }
        }

        override fun constructView(index: Int): View {
            val v = createCardView()
            v.minSize = FloatDim(DIM_UNLIMITED.w, 400f)
            return v
        }

    }

    val child = ListLayout(adapter)

    val layout = ScrollLayout(child)
    layout.background = ColorDrawable(THEME.getBackgroundColor())

    val frame = GLG2DRootView(layout, "List Test", Dimension(500, 500), true)
    frame.showFrame()
}