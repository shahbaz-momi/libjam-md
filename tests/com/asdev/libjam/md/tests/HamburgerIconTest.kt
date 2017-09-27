package com.asdev.libjam.md.tests

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.RelativeLayout
import com.asdev.libjam.md.menu.ContextMenuAction
import com.asdev.libjam.md.theme.DarkMaterialTheme
import com.asdev.libjam.md.theme.LightMaterialTheme
import com.asdev.libjam.md.theme.THEME
import res.R
import java.awt.Dimension

/**
 * Created by Asdev on 04/26/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */

fun main(args: Array<String>) {
    THEME = DarkMaterialTheme
    THEME.init()

    val layout = RelativeLayout()
    layout.background = ColorDrawable(R.theme.background)

    val frame = GLG2DRootView(layout, "Hamburger icon test", Dimension(500, 500), true)

    // add theme switching
    layout.contextMenuItems = listOf(
            ContextMenuAction("Dark theme", { frame.setTheme(DarkMaterialTheme); true }),
            ContextMenuAction("Light theme", { frame.setTheme(LightMaterialTheme); true })
    )

    frame.getFrameDecoration()?.enableDrawerToggle(null)

    frame.showFrame()
}