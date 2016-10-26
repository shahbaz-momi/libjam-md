package com.asdev.libjam.md.tests

import com.asdev.libjam.md.base.RootView
import com.asdev.libjam.md.drawable.*
import com.asdev.libjam.md.layout.PaddingLayout
import com.asdev.libjam.md.layout.RelativeLayout
import com.asdev.libjam.md.theme.LightMaterialTheme
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.BUTTON_TYPE_RAISED
import com.asdev.libjam.md.view.ButtonView
import java.awt.Dimension
import java.awt.Point
import java.awt.event.MouseEvent

/**
 * Created by Asdev on 10/17/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */

fun main(args: Array<String>) {
    THEME = LightMaterialTheme
    // NOTE: always initiliaze the theme before creating any views // TODO: fix that
    THEME.init()

    val layout = RelativeLayout()
    layout.background = ColorDrawable(THEME.getBackgroundColor())

    val v = ButtonView("Sup Dawg!", BUTTON_TYPE_RAISED)
    v.maxSize = FloatDim(100f, 40f)

    v.onClickListener = { mouseEvent: MouseEvent, point: Point -> println("Dawg") }
    v.onPressListener = { mouseEvent: MouseEvent, point: Point -> print("Sup ")}

    layout.addChild(PaddingLayout(v, 30f))
    val root = RootView("My Drive", Dimension(500, 500), layout, false)

    // show frame decoration above all
    // frame decoration can't be null because we have specified the rootview to use a custom deco
//    val deco = root.getFrameDecoration()!!
//    deco.setDrawAboveAll(false)

    root.showFrame()
}