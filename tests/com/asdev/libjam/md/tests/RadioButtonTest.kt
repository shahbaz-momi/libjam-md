package com.asdev.libjam.md.tests

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.menu.ContextMenuAction
import com.asdev.libjam.md.theme.DarkMaterialTheme
import com.asdev.libjam.md.theme.LightMaterialTheme
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.view.RadioButtonGroup
import com.asdev.libjam.md.view.RadioButtonView
import com.asdev.libjam.md.view.View
import res.R
import java.awt.Dimension

/**
 * Created by Asdev on 04/24/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */

fun main(args: Array<String>) {
    THEME.init()

    val group = RadioButtonGroup(onChangeListener = {println(it.groupValue + " was selected")})

    val box = RadioButtonView.makeWithLabel("Check me!", group = group, groupValue = "0")

    val box2 = RadioButtonView.makeWithLabel("Check me 2!", group = group, groupValue = "1")

    val box3 = RadioButtonView.makeWithLabel("Check me 3!", group = group, groupValue = "2")

    val layout = LinearLayout()
    layout.setOrientation(ORIENTATION_HORIZONTAL)
    layout.background = ColorDrawable(R.theme.background)
    layout.addChild(box)
    layout.addChild(box2)
    layout.addChild(box3)
    layout.addChild(View())

    val frame = GLG2DRootView(layout, "Radio Button Test", Dimension(500, 500), true)
    frame.showFrame()

    layout.contextMenuItems = listOf(
            ContextMenuAction("Dark theme", { frame.setTheme(DarkMaterialTheme); true }),
            ContextMenuAction("Light theme", { frame.setTheme(LightMaterialTheme); true })
    )
}