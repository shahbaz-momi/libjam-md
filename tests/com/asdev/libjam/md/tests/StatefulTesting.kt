package com.asdev.libjam.md.tests

import com.asdev.libjam.md.base.RootView
import com.asdev.libjam.md.drawable.*
import com.asdev.libjam.md.layout.RelativeLayout
import com.asdev.libjam.md.theme.LightMaterialTheme
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.View
import java.awt.Dimension

/**
 * Created by Asdev on 10/17/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */

fun main(args: Array<String>) {
    // NOTE: always initiliaze the theme before creating any views // TODO: fix that
    THEME.init()

    val layout = RelativeLayout()
    layout.background = ColorDrawable(THEME.getBackgroundColor())
    val v = View()
    v.background =
            AnimatedCompoundDrawable(
                    AnimatedHoverShadowDrawable(),
                    ColorDrawable(THEME.getAccentColor())
            )
    v.maxSize = FloatDim(130f, 130f)
    layout.addChild(v)
    val root = RootView("My Drive", Dimension(500, 500), layout, true)
    root.setTheme(LightMaterialTheme) // or DarkMaterialTheme

    // show frame decoration above all
    // frame decoration can't be null because we have specified the rootview to use a custom deco
    val deco = root.getFrameDecoration()!!
    deco.setDrawAboveAll(false)

    root.showFrame()
}