package com.asdev.libjam.md.view

import com.asdev.libjam.md.animation.FactorableDecelerateInterpolator
import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.*
import com.asdev.libjam.md.util.DEBUG_LAYOUT_BOXES
import com.asdev.libjam.md.xml.XMLParamList
import res.R
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.font.FontRenderContext
import java.awt.geom.AffineTransform
import kotlin.properties.Delegates

/**
 * Created by Asdev on 04/22/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.view
 */

/**
 * The label text scale change during animation.
 */
const val SCALE_CHANGE = 0.15f

const val BLINK_TIME = 530

private val fontRendererContext = FontRenderContext(AffineTransform(), true, true)

/**
 * A view for text input.
 */
class TextInputView(): View() {

    /**
     * The text to use as the label for this input text.
     */
    var labelText by Delegates.observable("Enter text") {_, _, _ -> layoutText() }

    /**
     * The [Font] to be used by this View.
     */
    var font by Delegates.observable(R.theme.font_primary) {_, _, _ -> layoutText() }

    /**
     * The color to draw the primary text with.
     */
    var color = R.theme.scrollbar

    /**
     * The accent color to use with this text input.
     */
    var colorAccent = R.theme.accent

    /**
     * The gravity to draw the actual component in. It will always fill horizontally, and react vertically using the given
     * gravity.
     */
    var gravity = GRAVITY_MIDDLE_MIDDLE

    /**
     * The [Font] to be used from the theme. Will be update every time in [onThemeChange]. Use -1 to not use a theme font.
     */
    private var themeFont = FONT_PRIMARY

    /**
     * The [Color] to be used from the theme. Will be update every time in [onThemeChange]. Use -1 to not use a theme color
     */
    private var themeColor = COLOR_SCROLLBAR

    /**
     * The accent [Color] to be used from the theme. Will be update every time in [onThemeChange]. Use -1 to not use a theme color
     */
    private var themeAccentColor = COLOR_ACCENT

    /**
     * The animator for this text input when the view itself is clicked.
     */
    private val animator = FloatValueAnimator(250f, FactorableDecelerateInterpolator(1.7f), 0f, 0f, 1f)

    /**
     * The text that is currently entered by the user.
     */
    private var inputText = ""

    /**
     * The time since the last cursor blink.
     */
    private var lastBlinkT = System.currentTimeMillis()

    /**
     * Whether or not to draw the blinking text cursor.
     */
    private var blink = false

    constructor(label: String): this() {
        this.labelText = label
    }

    override fun applyParameters(params: GenericParamList) {
        super.applyParameters(params)

        if(params is XMLParamList) {
            if(params.hasParam(R.attrs.TextInputView.color)) {
                setThemeColor(-1)
                params.setToColor(R.attrs.TextInputView.color, this::color)
            }

            if(params.hasParam(R.attrs.TextInputView.color_accent)) {
                setThemeAccentColor(-1)
                params.setToColor(R.attrs.TextInputView.color_accent, this::colorAccent)
            }

            if(params.hasParam(R.attrs.TextInputView.font)) {
                setThemeFont(-1)
                params.setToFont(R.attrs.TextInputView.font, this::font)
            }

            params.setToFloat(R.attrs.TextInputView.padding_horizontal, this::paddingHorizontal)
            params.setToString(R.attrs.TextInputView.label_text, this::labelText)
            params.setToInt(R.attrs.TextInputView.content_gravity, this::gravity)
        }

        layoutText()
    }

    override fun loop() {
        super.loop()

        animator.loop()

        if(animator.isRunning()) {
            requestRepaint()
        }

        if(state == State.STATE_FOCUSED) {
            if(System.currentTimeMillis() - lastBlinkT > BLINK_TIME) {
                lastBlinkT = System.currentTimeMillis()

                blink = !blink

                requestRepaint()
            }
        }
    }

    override fun onMousePress(e: MouseEvent, mPos: Point) {
        if(state != State.STATE_FOCUSED)
            super.onMousePress(e, mPos)
    }

    override fun onMouseRelease(e: MouseEvent, mPos: Point) {
        if(state != State.STATE_FOCUSED)
            super.onMouseRelease(e, mPos)
    }

    override fun onStateChanged(previous: State, newState: State) {
        super.onStateChanged(previous, newState)

        if(newState == State.STATE_FOCUSED) {
            // begin the animation
            if(!animator.isRunning() && inputText.isEmpty())
                animator.setFromValue(0f).setToValue(1f).start()
            setCursor(Cursor.DEFAULT_CURSOR)
        } else if(newState == State.STATE_HOVER) {
            setCursor(Cursor.TEXT_CURSOR)
            blink = false
        } else if (newState != State.STATE_PRESSED){
            if(previous == State.STATE_FOCUSED && inputText.isEmpty()) {
                animator.setFromValue(1f).setToValue(0f).start()
            }

            setCursor(Cursor.DEFAULT_CURSOR)
            blink = false
        }
    }

    override fun onTabTraversal(): Boolean {
        if(state == State.STATE_NORMAL) {
            onStateChanged(state, State.STATE_FOCUSED)
            return false
        } else {
            onStateChanged(state, State.STATE_NORMAL)
            return true
        }
    }

    override fun onPostLayout() {
        super.onPostLayout()

        layoutText()
    }

    override fun onKeyPressed(e: KeyEvent) {
        super.onKeyPressed(e)

        if(state != State.STATE_FOCUSED)
            return

        if(e.keyCode == KeyEvent.VK_BACK_SPACE && inputText.isNotEmpty()) {
            inputText = inputText.substring(0, inputText.length - 1)

            updateText()
            requestRepaint()
        }
    }

    override fun onKeyTyped(e: KeyEvent) {
        super.onKeyTyped(e)

        if(state != State.STATE_FOCUSED)
            return

        // add to the input text
        val char = e.keyChar

        if(font.canDisplay(char) && char != '\t' && char != '\n') {
            inputText += char

            updateText()
            requestRepaint()
        }
    }

    private var baselineY = 0f

    /**
     * The padding to the left and right of this text input.
     */
    var paddingHorizontal = 8f

    private var labelY = 0f
    private var labelYPostAnim = 0f
    private var inputTextLineHeight = 0f
    private var shiftY = 0f

    private fun layoutText() {
        val offsetY = 8
        val labelYPad = 6

        baselineY = layoutSize.h - offsetY

        val metrics = font.getLineMetrics(inputText, fontRendererContext)

        // line height of the text
        inputTextLineHeight = metrics.ascent - metrics.descent + (metrics.height - metrics.ascent)

        // the y of the text drawing is from the baseline, so offset it a little bit from there
        labelY = baselineY - labelYPad

        // the animation should go above the lineHeight of the inputText
        labelYPostAnim = labelY - inputTextLineHeight - labelYPad / 2f

        // calculate the y according to the gravity
        val actualH = (layoutSize.h - labelYPostAnim) + inputTextLineHeight
        val actualY = calculateYComp(gravity, 0f, layoutSize.h, actualH)
        shiftY = -actualY
    }

    /**
     * Calculates the y-component of the corresponding gravity.
     * @param bY bounding box Y
     * @param bH bounding box H
     * @param oH object H
     */
    private fun calculateYComp(gravity: Int, bY: Float, bH: Float, oH: Float): Float {
        /// NOTE: top and bottom are swapped cuz of calculations
        if(gravity == GRAVITY_TOP_LEFT ||
                gravity == GRAVITY_TOP_MIDDLE ||
                gravity == GRAVITY_TOP_RIGHT) {
            // just return 0f
            return bY
        } else if(gravity == GRAVITY_MIDDLE_LEFT ||
                gravity == GRAVITY_MIDDLE_MIDDLE ||
                gravity == GRAVITY_MIDDLE_RIGHT) {
            // align to the middle
            return bY + bH / 2f - oH / 2f
        } else {
            // align to bottom
            return bY + bH - oH
        }
    }

    /**
     * The truncated display text.
     */
    private var displayText = inputText

    private fun updateText() {
        displayText = inputText

        if(font.getStringBounds(inputText, fontRendererContext).width + font.size > layoutSize.w - paddingHorizontal * 2f) {
            // overflowing, begin cutoff
            for(i in inputText.length - 2 downTo 0) {
                val str = inputText.substring(i)

                if(font.getStringBounds(str, fontRendererContext).width + font.size < layoutSize.w - paddingHorizontal * 2f) {
                    // i + 1 is our str
                    displayText = inputText.substring(i)
                }
            }
        }
    }

    /**
     * Sets the theme font to use.
     */
    fun setThemeFont(newThemeFont: Int) {
        themeFont = newThemeFont
        if(themeFont != -1)
            font = THEME.getFont(themeFont)
    }

    /**
     * Sets the theme color to use.
     */
    fun setThemeColor(newThemeColor: Int) {
        themeColor = newThemeColor
        if(themeColor != -1)
            color = THEME.getColor(themeColor)
    }

    /**
     * Sets the theme accent color to use.
     */
    fun setThemeAccentColor(newThemeColor: Int) {
        themeAccentColor = newThemeColor
        if(themeAccentColor != -1)
            colorAccent = THEME.getColor(themeAccentColor)
    }

    /**
     * Returns the text the user has input into this text input.
     */
    fun getInputText() = inputText

    /**
     * Returns the label of this text input.
     */
    fun getLabel() = labelText

    /**
     * Sets the label of this text input.
     */
    fun setLabel(label: String) {
        labelText = label

        updateText()
        layoutText()
        requestRepaint()
    }

    /**
     * Sets the input text of this text input.
     */
    fun setInputText(input: String) {
        inputText = input

        updateText()
        layoutText()
        requestRepaint()
    }

    /**
     * Updates the colors and fonts of this text input.
     */
    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)

        setThemeAccentColor(themeAccentColor)
        setThemeColor(themeColor)
        setThemeFont(themeFont)
    }

    override fun onDraw(g: Graphics2D) {
        super.onDraw(g)

        val baselineThickness = 1

        g.translate(0.0, shiftY.toDouble())

        // start with drawing the baseline
        g.color = color

        // 2px rect baseline, gray
        g.fillRect(paddingHorizontal.toInt(), baselineY.toInt(), layoutSize.w.toInt() - (paddingHorizontal * 2f).toInt(), baselineThickness)

        g.font = font

        // draw the label above it
        if(state == State.STATE_FOCUSED || inputText.isNotEmpty() || animator.isRunning()) {
            val prog = animator.getValue()

            // animate the color as well
            g.color = Color(
                    lerp(color.red, colorAccent.red, prog),
                    lerp(color.green, colorAccent.green, prog),
                    lerp(color.blue, colorAccent.blue, prog))

            val scale = (1f - prog) * SCALE_CHANGE + (1f - SCALE_CHANGE)

            g.translate(0.0, labelYPostAnim.toDouble())
            g.scale(scale.toDouble(), scale.toDouble())
            g.translate(0.0, -labelYPostAnim.toDouble())

            g.drawString(labelText, paddingHorizontal, lerp(labelY, labelYPostAnim, prog))

            g.translate(0.0, labelYPostAnim.toDouble())
            g.scale(1.0 / scale.toDouble(), 1.0 / scale.toDouble())
            g.translate(0.0, -labelYPostAnim.toDouble())

            // draw the over baseline
            g.color = colorAccent
            val mid = (layoutSize.w / 2f) - paddingHorizontal
            g.fillRect(paddingHorizontal.toInt() + (mid * (1f - prog)).toInt(), baselineY.toInt(), (mid * prog * 2f).toInt(), baselineThickness * 2)

            g.color = color

            if(blink) {
                val bounds = g.getFontMetrics(font).getStringBounds(displayText, g)

                // draw the blink
                g.fillRect(bounds.width.toInt() + paddingHorizontal.toInt(), labelY.toInt() - inputTextLineHeight.toInt() + 2, baselineThickness, inputTextLineHeight.toInt() + 1)
            }

            // cut off part of the text if the text has been truncated
            g.drawString(displayText, paddingHorizontal, labelY)
        } else {
            g.drawString(labelText, paddingHorizontal, labelY)
        }

        if(DEBUG_LAYOUT_BOXES) {
            g.color = Color.RED
            g.drawRect(0, layoutSize.h.toInt() - labelYPostAnim.toInt() - inputTextLineHeight.toInt(), layoutSize.w.toInt(), labelYPostAnim.toInt() + inputTextLineHeight.toInt())
        }

        g.translate(0.0, -shiftY.toDouble())
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

