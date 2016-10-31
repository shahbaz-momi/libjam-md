package com.asdev.libjam.md.tests

import com.asdev.libjam.md.base.RootView
import com.asdev.libjam.md.drawable.*
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.COLOR_TITLE
import com.asdev.libjam.md.theme.FONT_TITLE
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.DIM_UNLIMITED
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.TextView
import com.asdev.libjam.md.view.View
import java.awt.Color
import java.awt.Dimension
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by Asdev on 10/13/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */

fun main(args: Array<String>) {
    THEME.init()

    val toolbarContainer = LinearLayout()
    toolbarContainer.background = ColorDrawable(THEME.getBackgroundColor())

    val navBar = View()
    navBar.background = ColorDrawable(THEME.getDarkPrimaryColor())
    navBar.maxSize = FloatDim(-2f, 35f)

    val toolbar = TextView("My Drive")
    toolbar.background  = ColorDrawable(THEME.getPrimaryColor())
    toolbar.gravity = GRAVITY_MIDDLE_LEFT
    toolbar.setThemeFont(FONT_TITLE)
    toolbar.setThemeColor(COLOR_TITLE)
    toolbar.setPadding(20f)

    toolbar.minSize = FloatDim(-2f, 50f)
    toolbar.maxSize = FloatDim(-2f, 50f)

    val list = GenericLayoutParamList()
    list.putParam("gravity", GRAVITY_MIDDLE_LEFT)
    toolbar.applyLayoutParameters(list)

    // toolbar.addChild(textView)

    toolbarContainer.addChild(navBar)
    toolbarContainer.addChild(toolbar)

    val v = RelativeLayout()

    val dummy = View()
    dummy.background = ColorDrawable(THEME.getAccentColor())
    dummy.maxSize = FloatDim(100f, 100f)

    val elevated = ElevatedLayout(dummy)

    v.addChild(elevated)

    toolbarContainer.addChild(v)

    val rootView = RootView("TextView", Dimension(500, 500), toolbarContainer, false)
    rootView.showFrame()
}