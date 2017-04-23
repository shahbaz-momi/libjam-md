package com.asdev.libjam.md.tests

import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.xml.inflateLayout
import res.R
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

    GLG2DRootView(inflateLayout(R.layout.layout_text_input), "Text Input Test", Dimension(500, 500), true).showFrame()
}