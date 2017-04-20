package com.asdev.libjam.md.tests

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.SCALE_TYPE_COVER
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.LinearLayout
import com.asdev.libjam.md.layout.ViewGroup
import com.asdev.libjam.md.menu.ContextMenu
import com.asdev.libjam.md.menu.ContextMenuAction
import com.asdev.libjam.md.menu.ContextMenuSeparator
import com.asdev.libjam.md.menu.ContextMenuText
import com.asdev.libjam.md.theme.DarkMaterialTheme
import com.asdev.libjam.md.theme.LightMaterialTheme
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.ButtonView
import com.asdev.libjam.md.view.TextView
import com.asdev.libjam.md.view.View
import com.asdev.libjam.md.xml.*
import res.R
import java.awt.Color
import java.awt.Dimension

fun main(args: Array<String>) {
    THEME = LightMaterialTheme
    THEME.init()

    // inflate and grab an xml layout
    val start = System.nanoTime()
    val v = inflateLayout(R.layout.layout_testing) as ViewGroup
    println("Took " + ((System.nanoTime() - start) / 1000000.0) + "ms for XML Layout inflation.")

    // example of finding a view from a inflated xml layout
    val button = v.findViewById(R.id.testing_button) as? ButtonView ?: return

//    val childContainer = v.findViewById(R.id.child_container) as? LinearLayout?: throw Exception("Unable to get LinearLayout")
//    for(i in 1 until childContainer.getChildCount()) {
//        val c = childContainer.getChildAtIndex(i)
//        c.onClickListener = { _, _ ->
//            c.postAction {
//                childContainer.addChild(TextView("Synthetic child").apply { minSize = FloatDim(-1f, 100f); background = ColorDrawable(Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat())) })
//            }
//        }
//
//        (c as TextView).text = "Hello again $i"
//    }

    val frame = GLG2DRootView(v, "ResParseTestKt", Dimension(500, 500), true)

    frame.showFrame()

    button.contextMenuItems = listOf(ContextMenuText("Button menu"))
    v.contextMenuItems = listOf(ContextMenuAction("Dark theme", {
            frame.setTheme(DarkMaterialTheme)
            return@ContextMenuAction true
        }
    ))

    // example on click event.
    button.onClickListener = { _, _ ->
        frame.setTheme(LightMaterialTheme)
    }
}