package com.asdev.libjam.md.tests

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.GRAVITY_MIDDLE_MIDDLE
import com.asdev.libjam.md.layout.GenericParamList
import com.asdev.libjam.md.layout.LAYOUT_PARAM_GRAVITY
import com.asdev.libjam.md.layout.RelativeLayout
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.view.CheckboxView
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

    val box = CheckboxView.makeWithLabel("Check me!", true)
    box.applyParameters(GenericParamList() with (LAYOUT_PARAM_GRAVITY to GRAVITY_MIDDLE_MIDDLE))

    val layout = RelativeLayout()
    layout.background = ColorDrawable(R.theme.background)
    layout.addChild(box)

    GLG2DRootView(layout, "Checkbox Test", Dimension(500, 500), true).showFrame()
}