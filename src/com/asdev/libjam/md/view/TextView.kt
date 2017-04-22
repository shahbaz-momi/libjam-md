package com.asdev.libjam.md.view

import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.COLOR_PRIMARY_TEXT
import com.asdev.libjam.md.theme.FONT_PRIMARY
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.DEBUG_LAYOUT_BOXES
import com.asdev.libjam.md.util.DIM_UNLIMITED
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.xml.XMLParamList
import res.R
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.font.FontRenderContext
import java.util.regex.Pattern
import kotlin.properties.Delegates

/**
 * Created by Asdev on 10/13/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.view
 */

/**
 * A font renderer context to be used with text views.
 */
private val fontRendererContext = FontRenderContext(null, true, true)

/**
 * A pattern for matching new lines.
 */
private val newLine = Pattern.compile("[\\r\\n]")

private val whitespaceRegex = Pattern.compile("[\\t\\s]+")

/**
 * A [View] that displays the specified text.
 */
open class TextView(): OverlayView() {

    constructor(text: String): this() {
        this.text = text
    }

    /**
     * The text to be displayed in this view.
     */
    var text by Delegates.observable("") { _, o, n -> if(o != n) layoutText() }

    /**
     * The [Font] to be used by this view.
     */
    var font by Delegates.observable(R.theme.font_primary) {_, o, n -> if(o != n) layoutText() }

    /**
     * The multiplier on the line height, used to adjust the vertical spacing between lines.
     * E.g. 1.0 line height is standard, 2.0 is double spaced, etc.
     */
    var lineHeightMultiplier by Delegates.observable(1.2f) {_, o, n -> if(o != n) layoutText() }

    /**
     * Whether or not to wrap long lines of text.
     */
    var wrapLines by Delegates.observable(true) {_, o, n -> if(o != n) layoutText() }

    /**
     * The [Color] to draw the text in.
     */
    lateinit var color: Color

    /**
     * The gravity of the text to draw.
     */
    var gravity = GRAVITY_MIDDLE_MIDDLE

    /**
     * The [Font] to be used from the theme. Will be update every time in [onThemeChange]. Use -1 to not use a theme font.
     */
    private var themeFont = FONT_PRIMARY

    /**
     * The [Color] to be used from the theme. Will be update every time in [onThemeChange]. Use -1 to not use a theme color
     */
    private var themeColor = COLOR_PRIMARY_TEXT

    /**
     * The paddings to use when drawing the text.
     */
    var paddingTop = 0f
    var paddingBottom = 0f
    var paddingRight = 0f
    var paddingLeft = 0f

    init {
        // load in the theme font
        setThemeFont(themeFont)

        setThemeColor(themeColor)

        zIndex = 1

        layoutText()
    }

    override fun applyParameters(params: GenericParamList) {
        super.applyParameters(params)

        if(params is XMLParamList) {
            params.setToString(R.attrs.TextView.text, this::text)
            params.setToInt(R.attrs.TextView.text_gravity, this::gravity)

            if(params.hasParam(R.attrs.TextView.text_font)) {
                setThemeFont(-1)
                params.setToFont(R.attrs.TextView.text_font, this::font)
            }

            if(params.hasParam(R.attrs.TextView.text_color)) {
                setThemeColor(-1)
                params.setToColor(R.attrs.TextView.text_color, this::color)
            }

            if(params.hasParam(R.attrs.TextView.padding_bottom)) {
                paddingBottom = params.getInt(R.attrs.TextView.padding_bottom)!!.toFloat()
            }

            if(params.hasParam(R.attrs.TextView.padding_top)) {
                paddingTop = params.getInt(R.attrs.TextView.padding_top)!!.toFloat()
            }

            if(params.hasParam(R.attrs.TextView.padding_left)) {
                paddingLeft = params.getInt(R.attrs.TextView.padding_left)!!.toFloat()
            }

            if(params.hasParam(R.attrs.TextView.padding_right)) {
                paddingRight = params.getInt(R.attrs.TextView.padding_right)!!.toFloat()
            }

            params.setToFloat(R.attrs.TextView.line_height, this::lineHeightMultiplier)

            if(params.hasParam(R.attrs.TextView.wrap_lines)) {
                wrapLines = params.getString(R.attrs.TextView.wrap_lines)!! == "true"
            }
        }

        layoutText()
    }

    fun setPadding(padding: Float) {
        paddingTop = padding
        paddingBottom = padding
        paddingRight = padding
        paddingLeft = padding
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
     * Returns the theme color this [TextView] will use to draw the text.
     */
    fun getThemeColor() = themeColor

    /**
     * The height of each line of text.
     */
    private var lineHeight = 1f

    /**
     * The width of each line of text, in order.
     */
    private var lineWidths = floatArrayOf(0f).toTypedArray()

    /**
     * The individual lines of text.
     */
    private var lines = listOf("")

    override fun onMeasure(result: LayoutParams): LayoutParams {
        return super.onMeasure(result)
    }

    private var isMinPass = false
    override fun onPostLayout() {
        super.onPostLayout()

        if(!isMinPass) {
            layoutText()
            minSizeSynthetic = _calculateMinimumSize()
            isMinPass = true
            requestLayout()
        } else {
            isMinPass = false
        }

    }

    /**
     * Measures and layouts out the text.
     */
    fun layoutText() {
        // to avoid calling layoutText() every time we access text, cache the value
        // also, it is modified softly by wrapping, so account for that as well
        var t = text

        val metrics = font.getLineMetrics(t, fontRendererContext)

        // perform wrapping here
        if(wrapLines && layoutSize.w > 0f) {
            t = t.replace(newLine.toRegex(), "¶").replace("\t", "¬") /// SUPER HACKY CHANGE THIS PLS

            // split the text into straight words
            val words = t.split(whitespaceRegex) // WARN: this will obliterate new lines and tabs
            val lineList = ArrayList<String>()

            val builder = StringBuilder()

            for(i in words.indices) {
                val before = builder.toString()

                // add the space back
                if(!builder.isEmpty())
                    builder.append(" ")

                builder.append(words[i])

                val after = builder.toString()

                // compare sizes
                if(font.getStringBounds(after, fontRendererContext).width + paddingLeft + paddingRight >= layoutSize.w - font.size) {
                    // we went over, push before to stack and reset
                    lineList.add(before)
                    builder.setLength(0)
                    builder.append(words[i])
                }
            }

            // push any residual text
            if(!builder.isEmpty()) {
                lineList.add(builder.toString())
            }

            // join to a string
            t = lineList.joinToString(separator = "\n").replace("¶", "\n").replace("¬", "\t")
        }

        // break the text into individual lines
        lines = t.split(newLine)
        // create a new array with that many lines for the widths
        lineWidths = Array(lines.size) {0f}

        // measure the overall text height
        lineHeight = metrics.ascent - metrics.descent + (metrics.height - metrics.ascent)
        // adjust by the multiplier
        lineHeight *= lineHeightMultiplier

        // measure the width of each line
        for((i, line) in lines.withIndex()) {
            val bounds = font.getStringBounds(line, fontRendererContext)
            lineWidths[i] = bounds.width.toFloat()
        }
    }

    private fun _calculateMinimumSize(): FloatDim {
        val actual: FloatDim
        if(wrapLines && layoutSize.w > 0f) {
            actual = FloatDim(DIM_UNLIMITED.w, lines.size * lineHeight)
        } else {
            actual = FloatDim(lineWidths.max()?: DIM_UNLIMITED.w, lines.size * lineHeight)
        }

        return FloatDim(maxOf(minSize.w, actual.w), maxOf(minSize.h, actual.h))
    }

    /**
     * [TextView] implementation of [onThemeChange]. Will update the [Font] and [Color] used by this [TextView].
     */
    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)

        // update the font
        setThemeFont(themeFont)

        // update the color
        setThemeColor(themeColor)
    }

    /**
     * Draws the text with the specified attributes.
     */
    override fun onDraw(g: Graphics2D) {
        // draw the background and stuff
        super.onDraw(g)
        // set the font and color
        g.font = font
        g.color = color

        // the base y coordinate for rendering
        val yBase = calculateYComp(gravity, paddingTop, layoutSize.h - paddingTop - paddingBottom, lineHeight * lines.size)

        for(i in lines.indices) {
            val line = lines[i]

            // x coordinate justified by total width
            val x = calculateXComp(gravity, paddingLeft, layoutSize.w - paddingLeft - paddingRight, lineWidths[i])
            val y = yBase + lineHeight * i + (lineHeight / lineHeightMultiplier) // last part shifts for the baseline

            g.drawString(line, x, y)

            if(DEBUG_LAYOUT_BOXES)
                g.drawRect(x.toInt(), y.toInt() - lineHeight.toInt(), lineWidths[i].toInt(), lineHeight.toInt())
        }

        if(DEBUG_LAYOUT_BOXES) {
            g.color = Color.BLUE
            g.drawRect(paddingLeft.toInt(), yBase.toInt(), lineWidths.max()!!.toInt(), minSize.h.toInt() - paddingTop.toInt() + paddingBottom.toInt())
        }
    }

    /**
     * Calculates the x-component of the corresponding gravity.
     * @param bX bounding box X
     * @param bW bounding box W
     * @param oW object W
     */
    private fun calculateXComp(gravity: Int, bX: Float, bW: Float, oW: Float): Float {
        if(gravity == GRAVITY_TOP_LEFT ||
                gravity == GRAVITY_MIDDLE_LEFT ||
                gravity == GRAVITY_BOTTOM_LEFT) {
            // just return bounding box x
            return bX
        } else if(gravity == GRAVITY_TOP_RIGHT ||
                gravity == GRAVITY_MIDDLE_RIGHT ||
                gravity == GRAVITY_BOTTOM_RIGHT) {
            // align to the right
            return bX + bW - oW
        } else {
            // gravity has to be a center
            return bX + bW / 2f - oW / 2f
        }
    }

    /**
     * Calculates the y-component of the corresponding gravity.
     * @param bY bounding box Y
     * @param bH bounding box H
     * @param oH object H
     */
    private fun calculateYComp(gravity: Int, bY: Float, bH: Float, oH: Float): Float {
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
}