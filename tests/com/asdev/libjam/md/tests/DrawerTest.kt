package com.asdev.libjam.md.tests

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.SCALE_TYPE_COVER
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.COLOR_TITLE
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.TextView
import com.asdev.libjam.md.view.View
import res.R
import java.awt.Color
import java.awt.Dimension

/**
 * Created by Asdev on 04/27/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */

fun main(args: Array<String>) {
    THEME.init()

    val header = RelativeLayout()
    header.maxSize.h = 120f
    header.minSize.h = 100f
    header.background = R.drawables.welcome_card.softClone(SCALE_TYPE_COVER)

    val title = TextView("Hello")
    title.gravity = GRAVITY_BOTTOM_LEFT
    title.applyParameters(GenericParamList() with (LAYOUT_PARAM_GRAVITY to GRAVITY_BOTTOM_LEFT))
    title.setThemeColor(COLOR_TITLE)
    title.setThemeFont(-1)
    title.font = title.font.deriveFont(22f)
    title.paddingLeft = 12f
    title.paddingBottom = 12f

    header.addChild(title)

    val items = Array(20) { DrawerLayout.DrawerItem("Item $it") }

    val drawer = DrawerLayout.makeDrawer(header, *items)

    val container = RelativeLayout()
    container.addChild(
            View().apply {
                // yellow background
                background = ColorDrawable(Color.YELLOW)
                // center it in the layout
                applyParameters(
                        GenericParamList()
                            with (LAYOUT_PARAM_GRAVITY to GRAVITY_MIDDLE_MIDDLE)
                )
                // big
                maxSize = FloatDim(100000f, 10000f)
            }
    )

    container.setOverlayView(drawer.view)

    // apply the translation so it is off screen exactly the width of it
    drawer.view.postAction {
        container.translationX = it.layoutSize.w
    }

    val frame = GLG2DRootView(container, "Drawer test", Dimension(500, 500), true)

    val deco = frame.getFrameDecoration()!!
    deco.enableDrawerToggle(drawer)

    frame.showFrame()
}