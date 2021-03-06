package com.asdev.libjam.md.tests

import com.asdev.libjam.md.animation.AccelerateInterpolator
import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.drawable.*
import com.asdev.libjam.md.drawable.ImageDrawable.Companion.SCALE_TYPE_COVER
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.*
import com.asdev.libjam.md.util.DIM_UNLIMITED
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.BUTTON_TYPE_FLAT
import com.asdev.libjam.md.view.ButtonView
import com.asdev.libjam.md.view.TextView
import com.asdev.libjam.md.view.View
import java.awt.Dimension
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by Asdev on 10/25/16. All rights reserved.
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

    title.applyParameters(
            GenericParamList() with ("gravity" to GRAVITY_BOTTOM_LEFT)
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

    card.onKeyListener = object : View.ViewKeyListener {
        override fun onKeyTyped(e: KeyEvent) {
            content.text = content.text + e.keyChar
            content.requestRepaint()
        }
        override fun onKeyPressed(e: KeyEvent) {
        }
        override fun onKeyReleased(e: KeyEvent) {
        }
    }

    layout.addChild(ElevatedLayout(card, roundRadius = 5f))

    val root = GLG2DRootView(layout, "Hello!", Dimension(501, 502), true)

    val a = FloatValueAnimator(500f, AccelerateInterpolator, 0f, 0f, 100f)

    a.action = {
        buttonShare.maxSize.w = a.getValue() + 5f
        buttonShare.requestLayout()
    }

    buttonShare.onClickListener = { mouseEvent: MouseEvent, point: Point ->
        root.choreographer.run(a)
        a.start()
    }

    root.showFrame()

    root.getFrameDecoration()?.setDrawAboveAll(true)
}

fun createCardView(): View {
    val card = LinearLayout()

    card.maxSize = FloatDim(280f, 400f)

    val cardHeader = RelativeLayout()
    cardHeader.id = "View:CardBg"
    cardHeader.minSize = FloatDim(DIM_UNLIMITED.w, 130f)
    cardHeader.maxSize = FloatDim(DIM_UNLIMITED.w, 130f)
    cardHeader.background = ImageDrawable(ImageIO.read(File("assets/welcome_card.jpg")), SCALE_TYPE_COVER)

    val title = TextView("Welcome")
    title.setThemeColor(COLOR_TITLE)
    title.gravity = GRAVITY_BOTTOM_LEFT
    title.setPadding(15f)
    title.setThemeFont(FONT_SUBTITLE)

    title.applyParameters(
            GenericParamList() with ("gravity" to GRAVITY_BOTTOM_LEFT)
    )

    cardHeader.addChild(title)

    val content = TextView("Hey there! How are you doing?")
    content.id = "View:CardContent"
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

    card.onKeyListener = object : View.ViewKeyListener {
        override fun onKeyTyped(e: KeyEvent) {
            content.text = content.text + e.keyChar
            content.requestRepaint()
        }
        override fun onKeyPressed(e: KeyEvent) {
        }
        override fun onKeyReleased(e: KeyEvent) {
        }
    }

    return ElevatedLayout(card, roundRadius = 5f)
}