package com.asdev.libjam.md.tests

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.ImageDrawable
import com.asdev.libjam.md.drawable.ImageDrawable.Companion.SCALE_TYPE_ORIGINAL
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.ScrollLayout
import com.asdev.libjam.md.menu.ContextMenuAction
import com.asdev.libjam.md.theme.COLOR_TITLE
import com.asdev.libjam.md.theme.DarkMaterialTheme
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.DIM_UNLIMITED
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.TextView
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

    val child = TextView("Hello!")
    child.background = ImageDrawable(ImageIO.read(File("assets/welcome_card.jpg")), SCALE_TYPE_ORIGINAL)
    child.setThemeFont(-1)
    child.setThemeColor(COLOR_TITLE)
    child.font = child.font.deriveFont(48f)
    child.minSize = FloatDim(DIM_UNLIMITED.w, 1000f)

    val layout = ScrollLayout(child)
    layout.background = ColorDrawable(THEME.getBackgroundColor())

    val frame = GLG2DRootView(layout, "Scroll Test", Dimension(500, 500), true)
    frame.showFrame()

    child.contextMenuItems = listOf(ContextMenuAction("Dark theme", {frame.setTheme(DarkMaterialTheme); return@ContextMenuAction true;}))

}