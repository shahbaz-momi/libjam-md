package com.asdev.libjam.md.tests

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.ImageDrawable
import com.asdev.libjam.md.drawable.SCALE_TYPE_ORIGINAL
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.GRAVITY_MIDDLE_MIDDLE
import com.asdev.libjam.md.layout.GenericLayoutParamList
import com.asdev.libjam.md.layout.LinearLayout
import com.asdev.libjam.md.layout.ScrollLayout
import com.asdev.libjam.md.theme.COLOR_TITLE
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.DIM_UNLIMITED
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.*
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

    val child = CircularProgressView()
    child.applyLayoutParameters(
            GenericLayoutParamList() with ("gravity" to GRAVITY_MIDDLE_MIDDLE)
    )

    val child2 = ProgressView(PROGRESS_TYPE_INDETERMINATE)
    child2.setProgress(0.5f)
    child2.applyLayoutParameters(
            GenericLayoutParamList() with ("gravity" to GRAVITY_MIDDLE_MIDDLE)
    )


    val layout = LinearLayout()
    layout.addChild(child)
    layout.addChild(child2)
    layout.background = ColorDrawable(THEME.getBackgroundColor())

    val frame = GLG2DRootView(layout, "Progress Test", Dimension(500, 500), true)
    frame.showFrame()
}