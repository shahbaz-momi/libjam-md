package com.asdev.libjam.md.tests

import com.asdev.libjam.md.base.RootView
import com.asdev.libjam.md.drawable.*
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.*
import com.asdev.libjam.md.util.DIM_UNLIMITED
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.BUTTON_TYPE_FLAT
import com.asdev.libjam.md.view.ButtonView
import com.asdev.libjam.md.view.TextView
import com.asdev.libjam.md.view.View
import java.awt.Dimension
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by Asdev on 10/22/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */

fun main(args: Array<String>) {
    THEME = DarkMaterialTheme
    // NOTE: always initiliaze the theme before creating any views // TODO: fix that
    THEME.init()

    val layout = RelativeLayout()
    layout.background = ColorDrawable(THEME.getBackgroundColor())

    val card = LinearLayout()

    card.maxSize = FloatDim(280f, 400f)

    val cardHeader = RelativeLayout()
    cardHeader.minSize = FloatDim(DIM_UNLIMITED.w, 130f)
    cardHeader.maxSize = FloatDim(DIM_UNLIMITED.w, 130f)
    cardHeader.background = ImageDrawable(ImageIO.read(File("assets/welcome_card.jpg")), SCALE_TYPE_COVER)

    val title = TextView("Welcome")
    title.setThemeColor(COLOR_TITLE)
    title.gravity = GRAVITY_BOTTOM_LEFT
    title.setPadding(15f)
    title.setThemeFont(FONT_SUBTITLE)

    title.applyLayoutParameters(
            GenericLayoutParamList() with ("gravity" to GRAVITY_BOTTOM_LEFT)
    )

    cardHeader.addChild(title)

    val content = TextView("Hey there! How are you doing?")
    content.gravity = GRAVITY_TOP_LEFT
    content.setPadding(10f)
    content.paddingBottom = 20f

    val actions = LinearLayout()
    actions.setOrientation(ORIENTATION_VERTICAL)
    actions.maxSize = FloatDim(DIM_UNLIMITED.w, 40f)

    val buttonOpen = ButtonView("OPEN", BUTTON_TYPE_FLAT)
    buttonOpen.maxSize = FloatDim(60f, DIM_UNLIMITED.h)
    buttonOpen.setThemeFont(FONT_SECONDARY)

    val buttonShare = ButtonView("SHARE", BUTTON_TYPE_FLAT)
    buttonShare.maxSize = FloatDim(60f, DIM_UNLIMITED.h)
    buttonShare.setThemeFont(FONT_SECONDARY)

    actions.addChild(buttonOpen)
    actions.addChild(buttonShare)
    actions.addChild(View())

    card.addChild(cardHeader)
    card.addChild(content)

    // add divider
    val divider = View()
    divider.background = ColorDrawable(THEME.getDividerColor())
    divider.maxSize = FloatDim(DIM_UNLIMITED.w, 1f)

    card.addChild(divider)
    card.addChild(actions)

    layout.addChild(PaddingLayout(ElevatedLayout(card, roundRadius = 5f), 30f))
    val root = RootView("My Drive", Dimension(500, 500), layout, true)

    // show frame decoration above all
    // frame decoration can't be null because we have specified the rootview to use a custom deco
//    val deco = root.getFrameDecoration()!!
//    deco.setDrawAboveAll(false)

    root.showFrame()
}