package com.asdev.libjam.md.tests

import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.menu.ContextMenuAction
import com.asdev.libjam.md.menu.ContextMenuText
import com.asdev.libjam.md.theme.*
import com.asdev.libjam.md.view.ButtonView
import com.asdev.libjam.md.xml.*
import res.R
import java.awt.Dimension

fun main(args: Array<String>) {
    THEME = LightMaterialTheme
    THEME.init()

    // inflate and grab an xml layout
    val start = System.nanoTime()
    val v = inflateLayout(R.layout.layout_testing) as ViewGroup
    println("Took " + ((System.nanoTime() - start) / 1000000.0) + "ms for XML Layout inflation.")

    // example of finding a view from a inflated xml layout
    val button = v.findViewById(R.id.testing_button)!! as ButtonView

    val frame = GLG2DRootView(v, "ResParseTestKt", Dimension(500, 500), true)

    frame.showFrame()

    button.contextMenuItems = listOf(ContextMenuText("Button menu"))
    v.contextMenuItems = listOf(
            ContextMenuAction("Dark theme", { frame.setTheme(DarkMaterialTheme); true }),
            ContextMenuAction("Light theme", { frame.setTheme(LightMaterialTheme); true })
    )

    // example on click event.
    button.onClickListener = { _, _ ->
        Snackbar.makeTextAndAction(R.strings.snackbar_test, "UNDO", {_, _ -> print("Will undo!")}, v).show()
    }
}