package com.asdev.libjam.md.tests

import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.menu.ContextMenuAction
import com.asdev.libjam.md.theme.DarkMaterialTheme
import com.asdev.libjam.md.theme.LightMaterialTheme
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.view.TextView
import com.asdev.libjam.md.view.View
import com.asdev.libjam.md.xml.inflateLayout
import res.R
import java.awt.Dimension
import java.awt.event.KeyEvent

/**
 * Created by Asdev on 04/21/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */

fun main(args: Array<String>) {
    THEME = LightMaterialTheme
    THEME.init()

    val content = inflateLayout(R.layout.layout_text_wrap) as LinearLayout

    val textInner = content.getChildAtIndex(0) as TextView
    textInner.onKeyListener = object : View.ViewKeyListener {
        override fun onKeyPressed(e: KeyEvent) {
            if(e.keyCode == KeyEvent.VK_BACK_SPACE || e.keyCode == KeyEvent.VK_DELETE) {
                if(textInner.text.isNotEmpty())
                    textInner.text = textInner.text.substring(0, textInner.text.length - 1)
            } else if(e.keyCode == KeyEvent.VK_ENTER) {
                textInner.text += "\n"
            } else if(R.theme.font_primary.canDisplay(e.keyChar)) {
                textInner.text += e.keyChar
            }

            textInner.requestRepaint()
        }

        override fun onKeyReleased(e: KeyEvent) {
        }

        override fun onKeyTyped(e: KeyEvent) {

        }
    }

    val frame = GLG2DRootView(content, "TextWrappingKt", Dimension(500, 500), true)
    frame.showFrame()

    content.contextMenuItems = listOf(
            ContextMenuAction("Dark theme", { frame.setTheme(DarkMaterialTheme); true }),
            ContextMenuAction("Light theme", { frame.setTheme(LightMaterialTheme); true })
    )
}