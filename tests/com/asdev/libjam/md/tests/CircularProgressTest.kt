package com.asdev.libjam.md.tests

import com.asdev.libjam.md.animation.AccelerateInterpolator
import com.asdev.libjam.md.animation.DecelerateInterpolator
import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.CompoundDrawable
import com.asdev.libjam.md.drawable.SCALE_TYPE_COVER
import com.asdev.libjam.md.drawable.ShadowDrawable
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.*
import res.R
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
    child.applyParameters(
            GenericParamList() with (LAYOUT_PARAM_GRAVITY to R.gravity.middle_middle)
    )
    child.gravity = GRAVITY_BOTTOM_MIDDLE
    child.paddingBottom = 12f
    child.minSize = FloatDim(0f, child.circleRadius * 2.5f)

    val desc = TextView(R.strings.searching)
    desc.gravity = GRAVITY_TOP_MIDDLE

    val layout = LinearLayout()
    layout.addChild(View())
    layout.addChild(child)
    layout.addChild(desc)

    // layout.background = ColorDrawable(R.theme.background)
    R.drawables.texture.scaleType = SCALE_TYPE_COVER
    layout.background = R.drawables.texture // example of using a drawable from the resources

    val snackbarText = TextView(R.strings.snackbar_test)
    snackbarText.gravity = GRAVITY_MIDDLE_LEFT
    snackbarText.paddingLeft = 12f
    snackbarText.setThemeColor(-1)
    snackbarText.color = Color(230, 230, 230)


    val overlay = OverlayLinearLayout()
    overlay.setOrientation(ORIENTATION_VERTICAL)

    overlay.maxSize = FloatDim(1000000f, 40f)

    overlay.applyParameters(
            GenericParamList() with (LAYOUT_PARAM_GRAVITY to R.gravity.bottom_middle) // use either R.gravity.xxx or GRAVITY_XXX
                                        with (LAYOUT_PARAM_ANCHOR to ANCHOR_INSIDE)
    )

    overlay.background = CompoundDrawable(
            ShadowDrawable(yOffset = -3f),
            ColorDrawable(Color.DARK_GRAY)
    )

    overlay.translationY = 10000f // move offscreen

    overlay.addChild(snackbarText)

    val dismissButton = ButtonView(R.strings.dismiss_buttom, BUTTON_TYPE_FLAT)
    dismissButton.maxSize = FloatDim(100f, 1000000f)
    dismissButton.setThemeRippleColor(-1)
    dismissButton.setRippleColor(Color.WHITE)

    dismissButton.onClickListener = { _: MouseEvent, _:Point ->
        // cancel any snackbar hide animations
        overlay.cancelAnimation("Animator:SnackBarHide")

        // run a snack bar dismiss animation
        val anim = FloatValueAnimator(300f, AccelerateInterpolator, 0f, overlay.translationY, overlay.layoutSize.h + 20f)
        anim.action = { overlay.translationY = it.getValue() }
        anim.id = "Animator:SnackBarDismiss"

        overlay.runAnimation(anim, true)
    }

    overlay.addChild(dismissButton)

    layout.setOverlayView(overlay)

    val button = ButtonView("Show snackbar")
    button.minSize = FloatDim(100f, 30f)
    button.maxSize = FloatDim(100f, 30f)
    button.onClickListener = { _: MouseEvent, _: Point ->
        if(overlay.translationY != 0f && !overlay.translationYAnimator.isRunning())
            overlay.translationYAnimator.setFromValue(overlay.layoutSize.h + 20f).setToValue(0f).setDuration(300f).setInterpolator(DecelerateInterpolator).start()

        val anim = FloatValueAnimator(300f, AccelerateInterpolator, 2300f, 0f, overlay.layoutSize.h + 20f)
        anim.action = {overlay.translationY = it.getValue()}
        anim.id = "Animator:SnackBarHide"

        overlay.runAnimation(anim, true)
    }

    layout.addChild(PaddingLayout(button, 100f))

    println(R.strings.example_string)

    val frame = GLG2DRootView(layout, "Progress Test", Dimension(500, 500), true)
    frame.showFrame()

    Thread.sleep(5000)
    // frame.setTheme(DarkMaterialTheme)
}