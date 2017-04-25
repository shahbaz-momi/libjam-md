package com.asdev.libjam.md.view

import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.animation.LinearInterpolator
import com.asdev.libjam.md.layout.LinearLayout
import com.asdev.libjam.md.layout.ORIENTATION_VERTICAL
import com.asdev.libjam.md.theme.*
import res.R
import java.awt.Color
import java.awt.Cursor
import java.awt.Graphics2D

/**
 * Created by Asdev on 04/24/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.view
 */

/**
 * The radius of the rounded corners.
 */
private const val ROUND_CORNER_RADIUS = 3

/**
 * The size of the checkbox itself.
 */
private const val CHECKBOX_SIZE = 14

/**
 * The padding for the contents inside the checkbox.
 */
private const val CHECKBOX_CONTENTS_PAD = 2

/**
 * The maximum padding around the checkbox.
 */
private const val PADDING = 14f

private const val CHECKMARK_START = (CHECKBOX_SIZE - CHECKBOX_CONTENTS_PAD * 2f)
private const val CHECKMARK_SHORT_PART = 5f
private const val CHECKMARK_LONG_PART = 7f
private const val CHECKMARK_STROKE = 2

/**
 * A toggleable checkbox input view featuring material animations.
 */
class CheckboxView(private val onCheckListener: ((CheckboxView, Boolean) -> Unit)? = null, private var checked: Boolean = false): View() {

    companion object {

        /**
         * Creates a check box with a neighbouring label.
         */
        fun makeWithLabel(label: String, checked: Boolean = false, onCheckListener: ((CheckboxView, Boolean) -> Unit)? = null): View {
            // make the actual cb view
            val cb = CheckboxView(onCheckListener, checked)
            val textView = TextView(label)
            // gravity to the middle left
            textView.gravity = R.gravity.middle_left
            // put in a containing linear layout
            val container = LinearLayout()
            container.setOrientation(ORIENTATION_VERTICAL)
            container.addChild(cb)
            container.addChild(textView)

            container.onStateChangeListener = { prev, next -> cb.onStateChanged(prev, next) }
            // default constraints
            container.maxSize.w = 1000000f
            container.maxSize.h = 50f

            return container
        }

    }

    /**
     * The color of the checkbox when it is unactivated (false).
     */
    var colorUnactivated = R.theme.secondary_text

    /**
     * The color of the checkbox when it is activated (true).
     */
    var colorActivated = R.theme.accent

    /**
     * The color of the check mark within this checkbox.
     */
    var colorCheckmark = R.theme.background

    /**
     * The animator bound with this checkbox.
     */
    private val animator = FloatValueAnimator(300f, LinearInterpolator, 0f, 0f, 1f).apply { setAssignedValue(0f) }

    init {
        // by default, the minimum size should be at least the checkbox size
        minSize.w = CHECKBOX_SIZE.toFloat()
        minSize.h = CHECKBOX_SIZE.toFloat()

        maxSize = minSize.copy()
        maxSize.w += PADDING
        maxSize.h += PADDING

        if(checked) {
            animator.setAssignedValue(1f)
        } else {
            animator.setAssignedValue(0f)
        }
    }

    override fun loop() {
        super.loop()

        animator.loop()

        if(animator.isRunning()) {
            requestRepaint()
        }
    }

    override fun onStateChanged(previous: State, newState: State) {
        super.onStateChanged(previous, newState)

        if(newState == State.STATE_NORMAL) {
            setCursor(Cursor.DEFAULT_CURSOR)
        } else {
            setCursor(Cursor.HAND_CURSOR)
        }

        // if the previous was pressed and this is post press, then toggle the checked
        if(previous == State.STATE_PRESSED && newState == State.STATE_POST_PRESS) {
            setChecked(!checked)
        }
    }

    private var themeColorUnactiviated = COLOR_SECONDARY_TEXT
    private var themeColorActivated = COLOR_ACCENT
    private var themeColorCheckmark = COLOR_BACKGROUND

    /**
     * Returns whether or not this checkmark is checked.
     */
    fun isChecked() = checked

    /**
     * Sets the checked state of this checkbox and begins any necessary animations.
     */
    fun setChecked(b: Boolean) {
        checked = b

        if(checked) {
            animator.setFromValue(0f).setToValue(1f).start()
        } else {
            animator.setFromValue(1f).setToValue(0f).start()
        }

        requestRepaint()

        onCheckListener?.invoke(this, checked)
    }

    /**
     * Sets the unactivated color of this checkbox to the specified theme color.
     */
    fun setThemeColorUnactivated(color: Int) {
        themeColorUnactiviated = color

        if(themeColorUnactiviated != -1)
            colorUnactivated = THEME.getColor(themeColorUnactiviated)
    }

    /**
     * Sets the activated color of this checkbox to the specified theme color.
     */
    fun setThemeColorActivated(color: Int) {
        themeColorActivated = color

        if(themeColorActivated != -1)
            colorActivated = THEME.getColor(themeColorActivated)
    }

    /**
     * Sets the checkmark color of this checkbox to the specified theme color.
     */
    fun setThemeColorCheckmark(color: Int) {
        themeColorCheckmark = color

        if(themeColorCheckmark != -1)
            colorCheckmark = THEME.getColor(themeColorCheckmark)
    }

    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)

        setThemeColorActivated(themeColorActivated)
        setThemeColorUnactivated(themeColorUnactiviated)
        setThemeColorCheckmark(themeColorCheckmark)
    }

    override fun onDraw(g: Graphics2D) {
        super.onDraw(g)

        // center this within the actual size
        val x = (layoutSize.w - CHECKBOX_SIZE) / 2.0
        val y = (layoutSize.h - CHECKBOX_SIZE) / 2.0

        g.translate(x, y)

        var prog = animator.getValue() * 2f
        var invProg = 1f - prog

        var secondProg = 0f

        if(prog > 1f) {
            secondProg = prog - 1f

            prog = 1f
            invProg = 0f
        }

        // draw the outside box
        g.color = Color(
                lerp(colorUnactivated.red, colorActivated.red, prog),
                lerp(colorUnactivated.green, colorActivated.green, prog),
                lerp(colorUnactivated.blue, colorActivated.blue, prog))

        g.fillRoundRect(0, 0, CHECKBOX_SIZE, CHECKBOX_SIZE, ROUND_CORNER_RADIUS, ROUND_CORNER_RADIUS)

        // fill the inner box
        g.color = colorCheckmark
        val animSize = ((CHECKBOX_SIZE - CHECKBOX_CONTENTS_PAD * 2) * invProg).toInt()
        val animPos = ((CHECKBOX_SIZE - animSize) / 2f).toInt()
        g.fillRect(animPos, animPos, animSize, animSize)

        if(secondProg > 0f) {
            // begin the check mark
            val checkShort = CHECKMARK_SHORT_PART * secondProg
            val checkLong = CHECKMARK_LONG_PART * secondProg

            //   | long part
            //   |
            //   |
            // ___
            // short part

            g.translate(0.0, -CHECKBOX_CONTENTS_PAD.toDouble())

            // rotate the canvas
            g.rotate(Math.toRadians(45.0), CHECKBOX_SIZE / 2.0, CHECKBOX_SIZE / 2.0)

            // draw the short part
            g.fillRect( (CHECKMARK_START - checkShort).toInt(), CHECKMARK_START.toInt(), checkShort.toInt(), CHECKMARK_STROKE)
            // draw the long part
            g.fillRect( (CHECKMARK_START - CHECKMARK_STROKE).toInt(), (CHECKMARK_START - checkLong).toInt(), CHECKMARK_STROKE, checkLong.toInt())
            g.rotate(-Math.toRadians(45.0), CHECKBOX_SIZE / 2.0, CHECKBOX_SIZE / 2.0)

            g.translate(0.0, CHECKBOX_CONTENTS_PAD.toDouble())
        }

        g.translate(-x, -y)

    }

}

/**
 * Linear interpolates the given values.
 */
private fun lerp(start: Int, end: Int, progress: Float) = (start + (end - start) * progress).toInt()

/**
 * Linear interpolates the given values.
 */
private fun lerp(start: Float, end: Float, progress: Float) = (start + (end - start) * progress)