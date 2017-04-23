package com.asdev.libjam.md.tests

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.View
import java.awt.Color
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

    val layout = RelativeLayout()

    val v1 = View()
    v1.background = ColorDrawable(Color.PINK)
    v1.applyParameters(GenericParamList() with (LAYOUT_PARAM_GRAVITY to GRAVITY_TOP_LEFT))

    val v2 = View()
    v2.background = ColorDrawable(Color.BLUE)
    v2.applyParameters(GenericParamList() with (LAYOUT_PARAM_GRAVITY to GRAVITY_BOTTOM_MIDDLE))
    v2.maxSize.h = 250f
    v2.minSize.h = 50f

    val v3 = View()
    v3.background = ColorDrawable(Color.RED)
    v3.applyParameters(GenericParamList() with (LAYOUT_PARAM_GRAVITY to GRAVITY_TOP_RIGHT))
    v3.maxSize.w = 100f

    val v4 = View()
    v4.background = ColorDrawable(Color.GREEN)
    v4.applyParameters(GenericParamList() with (LAYOUT_PARAM_GRAVITY to GRAVITY_MIDDLE_LEFT))
    v4.maxSize.w = 200f

    val v5 = View()
    v5.background = ColorDrawable(Color.CYAN)
    v5.applyParameters(GenericParamList() with (LAYOUT_PARAM_GRAVITY to GRAVITY_MIDDLE_RIGHT))

    val v6 = View()
    v6.background = ColorDrawable(Color.MAGENTA)
    v6.applyParameters(GenericParamList() with (LAYOUT_PARAM_GRAVITY to GRAVITY_MIDDLE_MIDDLE))
    v6.maxSize = FloatDim(300f, 200f)
    v6.minSize.h = 60f

    layout.addChild(v1)
    layout.addChild(v2)
    layout.addChild(v3)
    layout.addChild(v4)
    layout.addChild(v5)
    layout.addChild(v6)

    GLG2DRootView(layout, "Testing", Dimension(500, 500), false).showFrame()
}