package com.asdev.libjam.md.tests

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.*
import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.awt.event.MouseEvent

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
    child.gravity = GRAVITY_BOTTOM_MIDDLE
    child.paddingBottom = 12f

    val desc = TextView("Searching")
    desc.gravity = GRAVITY_TOP_MIDDLE
    val child2 = ProgressView(PROGRESS_TYPE_INDETERMINATE)
    child2.setProgress(0.5f)
    child2.applyLayoutParameters(
            GenericLayoutParamList() with ("gravity" to GRAVITY_MIDDLE_MIDDLE)
    )


    val layout = LinearLayout()
    layout.id = "LinearLayoutContainer"
    layout.addChild(child)
    layout.addChild(desc)

    val button = ButtonView("ProgressView")
    button.minSize = FloatDim(100f, 30f)
    button.maxSize = FloatDim(100f, 30f)
    button.onClickListener = { e: MouseEvent, p: Point ->
        button.text = "${p.x} ${p.y}"
    }

    layout.addChild(PaddingLayout(button, 50f))

    layout.background = ColorDrawable(THEME.getBackgroundColor())

    val overlay = OverlayView(bindViewId = desc.id)
    overlay.maxSize = FloatDim(100f, 100f)
    overlay.applyLayoutParameters(
            GenericLayoutParamList() with ("gravity" to GRAVITY_BOTTOM_LEFT)
    )
    overlay.background = ColorDrawable(Color.PINK)
    layout.setOverlayView(overlay)

    val frame = GLG2DRootView(layout, "Progress Test", Dimension(500, 500), true)
    frame.showFrame()
}