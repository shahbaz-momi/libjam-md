package com.asdev.libjam.md.layout

import com.asdev.libjam.md.animation.AccelerateInterpolator
import com.asdev.libjam.md.animation.DecelerateInterpolator
import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.CompoundDrawable
import com.asdev.libjam.md.drawable.ShadowDrawable
import com.asdev.libjam.md.theme.COLOR_ACCENT
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.util.generateRandomId
import com.asdev.libjam.md.view.BUTTON_TYPE_FLAT
import com.asdev.libjam.md.view.ButtonView
import com.asdev.libjam.md.view.TextView
import com.asdev.libjam.md.view.View
import res.R
import java.awt.Color
import java.awt.Point
import java.awt.event.MouseEvent

/**
 * Created by Asdev on 04/20/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

/**
 * A container for a snack bar object.
 */
class Snackbar private constructor(private val viewGroup: OverlayViewGroup, val text: String) {

    val id = generateRandomId()

    companion object {

        /**
         * Returns a plain snackbar with the given text and no dismiss function.
         */
        fun makeText(text: String, parent: ViewGroup): Snackbar {
            val snackbarText = TextView(text)
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
                    ColorDrawable(Color(50, 50, 50))
            )

            overlay.translationY = 10000f // move offscreen

            overlay.addChild(snackbarText)

            val sb = Snackbar(overlay, text)
            parent.setOverlayView(sb.viewGroup)

            return sb
        }

        /**
         * Returns a snackbar with the given text and a dismiss action.
         */
        fun makeTextAndDismiss(text: String, parent: ViewGroup): Snackbar {
            val sb = makeText(text, parent)

            val overlay = sb.viewGroup

            val dismissButton = ButtonView("DISMISS", BUTTON_TYPE_FLAT)
            dismissButton.maxSize = FloatDim(100f, 1000000f)
            dismissButton.setThemeRippleColor(-1)
            dismissButton.setRippleColor(Color.WHITE)
            dismissButton.setThemeColor(COLOR_ACCENT)

            dismissButton.onClickListener = { _: MouseEvent, _: Point ->
                // cancel any snackbar hide animations
                overlay.cancelAnimation("Animator${sb.id}:SnackBarHide")

                // run a snack bar dismiss animation
                val anim = FloatValueAnimator(300f, AccelerateInterpolator, 0f, overlay.translationY, maxOf(sb.viewGroup.minSize.h, 60f))
                anim.action = { overlay.translationY = it.getValue() }
                anim.id = "Animator${sb.id}:SnackBarDismiss"

                overlay.runAnimation(anim, true)
            }

            overlay.addChild(dismissButton)
            parent.setOverlayView(sb.viewGroup)
            return sb
        }

        /**
         * Returns a snackbar with the given text and given action.
         */
        fun makeTextAndAction(text: String, actionText: String, action: (View, Snackbar) -> Unit, parent: ViewGroup): Snackbar {
            val sb = makeText(text, parent)

            val overlay = sb.viewGroup

            val button = ButtonView(actionText, BUTTON_TYPE_FLAT)
            button.maxSize = FloatDim(100f, 1000000f)
            button.setThemeRippleColor(-1)
            button.setRippleColor(Color.WHITE)
            button.setThemeColor(COLOR_ACCENT)

            button.onClickListener = { _: MouseEvent, _: Point ->
                action.invoke(button, sb)
            }

            overlay.addChild(button)
            parent.setOverlayView(sb.viewGroup)
            return sb
        }
    }

    /**
     * Force hides this snackbar.
     */
    fun hide() {
        viewGroup.cancelAnimation("Animator$id:SnackBarHide")

        // run a snack bar dismiss animation
        val anim = FloatValueAnimator(300f, AccelerateInterpolator, 0f, viewGroup.translationY, maxOf(viewGroup.minSize.h, 60f))
        anim.action = { viewGroup.translationY = it.getValue() }
        anim.id = "Animator$id:SnackBarDismiss"

        viewGroup.runAnimation(anim, true)
    }

    /**
     * Reveals this snackbar.
     */
    fun show() {
        viewGroup.translationYAnimator.setFromValue(60f).setToValue(0f).setDuration(300f).setInterpolator(DecelerateInterpolator).start()

        val anim = FloatValueAnimator(300f, AccelerateInterpolator, 2300f, 0f, maxOf(viewGroup.minSize.h, 60f))
        anim.action = {viewGroup.translationY = it.getValue()}
        anim.id = "Animator$id:SnackBarHide"

        viewGroup.runAnimation(anim, true)
    }

}