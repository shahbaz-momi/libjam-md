package com.asdev.libjam.md.tests

import com.asdev.libjam.md.base.RootView
import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.view.View
import java.awt.Dimension

/**
 * Created by Asdev on 10/14/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */

fun main(args: Array<String>) {
    THEME.init()

    val v = View()
    v.background = ColorDrawable(THEME.getBackgroundColor())

    val root = RootView("My Drive", Dimension(500, 500), v, true)

    // show frame decoration above all
    val deco = root.getFrameDecoration()!!
    deco.setDrawAboveAll(true)

    root.showFrame()
}