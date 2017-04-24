package com.asdev.libjam.md.tests

import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.menu.ContextMenuAction
import com.asdev.libjam.md.theme.DarkMaterialTheme
import com.asdev.libjam.md.theme.LightMaterialTheme
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.xml.inflateLayout
import res.R
import java.awt.Dimension

/**
 * Created by Asdev on 04/22/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */

fun main(args: Array<String>) {
    THEME.init()

    val v = inflateLayout(R.layout.layout_text_input)

    val frame = GLG2DRootView(v, "Text Input Test", Dimension(500, 500), true)
    frame.showFrame()

    v.contextMenuItems = listOf(
            ContextMenuAction("Dark theme", { frame.setTheme(DarkMaterialTheme); true }),
            ContextMenuAction("Light theme", { frame.setTheme(LightMaterialTheme); true })
    )
}