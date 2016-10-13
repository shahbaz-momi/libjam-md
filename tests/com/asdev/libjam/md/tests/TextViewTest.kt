package com.asdev.libjam.md.tests

import com.asdev.libjam.md.base.RootView
import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.COLOR_TITLE
import com.asdev.libjam.md.theme.FONT_TITLE
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.DIM_UNLIMITED
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.TextView
import com.asdev.libjam.md.view.View
import java.awt.Dimension

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

//    val toolbar = LinearLayout()
//    toolbar.setOrientation(ORIENTATION_VERTICAL)
//    toolbar.background = ColorDrawable(THEME.getPrimaryColor())

    val textView = TextView("My Drive")
    textView.background  = ColorDrawable(THEME.getPrimaryColor())
    textView.gravity = GRAVITY_MIDDLE_LEFT
    textView.setThemeFont(FONT_TITLE)
    textView.setThemeColor(COLOR_TITLE)
    textView.setPadding(20f)

    textView.minSize = FloatDim(-2f, 50f)
    textView.maxSize = FloatDim(-2f, 50f)

    val list = GenericLayoutParamList()
    list.putParam("gravity", GRAVITY_MIDDLE_LEFT)
    textView.applyLayoutParameters(list)

    // toolbar.addChild(textView)

    toolbarContainer.addChild(navBar)
    toolbarContainer.addChild(textView)
    toolbarContainer.addChild(View())

    val rootView = RootView("TextView", Dimension(500, 500), toolbarContainer, false)
    rootView.showFrame()
}