package com.asdev.libjam.md.theme

import java.awt.Color
import java.awt.Font
import java.io.File

/**
 * Created by Asdev on 10/12/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.theme
 */

/**
 * The current theme of this application.
 */
var THEME: Theme = LightMaterialTheme

/**
 * Color property indexing constants. For use in Theme.getColor(which).
 */
val COLOR_PRIMARY = 1
val COLOR_ACCENT = 2
val COLOR_DARK_PRIMARY = 3
val COLOR_TITLE = 4
val COLOR_SUBTITLE = 5
val COLOR_PRIMARY_TEXT = 6
val COLOR_SECONDARY_TEXT = 7
val COLOR_BACKGROUND = 8
val COLOR_DIVIDER = 9
val COLOR_RIPPLE = 10

/**
 * Font property indexing constants. For use in Theme.getFont(which)
 */
val FONT_TITLE = 1
val FONT_SUBTITLE = 2
val FONT_PRIMARY = 3
val FONT_SECONDARY = 4

/**
 * Contains various properties linked to the theme of this application.
 * Notes: themes should be objects to avoid duplication/instantiation.
 */
abstract class Theme {

    /**
     * Called once upon in request to initialize this theme.
     */
    abstract fun init()

    //// Utility methods ////

    /**
     * Returns the requested color from this theme.
     */
    open infix fun getColor(which: Int): Color {
        when (which) {
            COLOR_PRIMARY -> return getPrimaryColor()
            COLOR_ACCENT -> return getAccentColor()
            COLOR_DARK_PRIMARY -> return getDarkPrimaryColor()
            COLOR_TITLE -> return getTitleColor()
            COLOR_SUBTITLE -> return getSubtitleColor()
            COLOR_PRIMARY_TEXT -> return getPrimaryTextColor()
            COLOR_SECONDARY_TEXT -> return getSecondaryTextColor()
            COLOR_BACKGROUND -> return getBackgroundColor()
            COLOR_DIVIDER -> return getDividerColor()
            COLOR_RIPPLE -> return getRippleColor()
        }

        throw IllegalArgumentException("The color index must be a valid color property index. $which is not.")
    }

    /**
     * Returns the request font from this theme.
     */
    open infix fun getFont(which: Int): Font {
        when (which) {
            FONT_PRIMARY -> return getPrimaryTextFont()
            FONT_SECONDARY -> return getSecondaryTextFont()
            FONT_TITLE -> return getTitleFont()
            FONT_SUBTITLE -> return getSubtitleFont()
        }

        throw IllegalArgumentException("The font index must be a valid font property index. $which is not.")
    }

    //// General colors ////

    /**
     * The primary color.
     */
    abstract fun getPrimaryColor(): Color

    /**
     * The darker primary color.
     */
    abstract fun getDarkPrimaryColor(): Color

    /**
     * The accent color.
     */
    abstract fun getAccentColor(): Color

    /**
     * The title text color.
     */
    abstract fun getTitleColor(): Color

    /**
     * The subtitle color.
     */
    abstract fun getSubtitleColor(): Color

    /**
     * The primary text color.
     */
    abstract fun getPrimaryTextColor(): Color

    /**
     * The secondary text color.
     */
    abstract fun getSecondaryTextColor(): Color

    /**
     * The background color.
     */
    abstract fun getBackgroundColor(): Color

    /**
     * The divider color. Dividers may be list dividers, horizontal lines/separators, etc.
     */
    abstract fun getDividerColor(): Color

    /**
     * The ripple (on buttons, etc) color.
     */
    abstract fun getRippleColor(): Color

    //// Fonts /////

    /**
     * The title font.
     */
    abstract fun getTitleFont(): Font

    /**
     * The subtitle font.
     */
    abstract fun getSubtitleFont(): Font

    /**
     * The primary text font.
     */
    abstract fun getPrimaryTextFont(): Font

    /**
     * The secondary text font.
     */
    abstract fun getSecondaryTextFont(): Font

    //// Misc ////

    /**
     * The width of the scroll bars.
     */
    abstract fun getScrollBarWidth(): Int

    /**
     * The rounded radius of the scroll bars.
     */
    abstract fun getScrollBarCornerRadius(): Int

    /**
     * The radius of circular progress views.
     */
    abstract fun getCircularProgressRadius(): Float

    /**
     * The stroke width of circular progress views.
     */
    abstract fun getCircularProgressStrokeWidth(): Float

    /**
     * The width of the progress bar.
     */
    abstract fun getProgressBarWidth(): Float
}

/**
 * The default theme that can be used with any app. A basic blue/yellow light material theme.
 */
object LightMaterialTheme : Theme() {

    //// Colors ////
    private val primaryColor = Color.decode("#2196F3")!!
    private val darkPrimaryColor = Color.decode("#1976D2")!!
    private val accentColor = Color.decode("#FFC107")!!
    private val titleColor = Color.decode("#FFFFFF")!!
    private val subtitleColor = Color.decode("#BBDEFB")!!
    private val primaryTextColor = Color.decode("#212121")!!
    private val secondaryTextColor = Color.decode("#757575")!!
    private val backgroundColor = Color.decode("#EEEEEE")!!
    private val dividerColor = Color.decode("#BDBDBD")!!
    private val rippleColor = Color.decode("#444444")!!

    //// Fonts ////
    private lateinit var titleFont: Font
    private lateinit var subtitleFont: Font
    private lateinit var primaryTextFont: Font
    private lateinit var secondaryTextFont: Font

    override fun init() {
        // load and set the fonts
        val roboto = Font.createFont(Font.TRUETYPE_FONT, File("assets/fonts/Roboto/Roboto-Regular.ttf"))
        titleFont = Font.createFont(Font.TRUETYPE_FONT, File("assets/fonts/Roboto/Roboto-Medium.ttf")).deriveFont(16f)
        subtitleFont = roboto.deriveFont(16f)
        primaryTextFont = roboto.deriveFont(15f)
        secondaryTextFont = roboto.deriveFont(14f)
    }

    override fun getPrimaryColor() = primaryColor

    override fun getDarkPrimaryColor() = darkPrimaryColor

    override fun getAccentColor() = accentColor

    override fun getTitleColor() = titleColor

    override fun getSubtitleColor() = subtitleColor

    override fun getPrimaryTextColor() = primaryTextColor

    override fun getSecondaryTextColor() = secondaryTextColor

    override fun getDividerColor() = dividerColor

    override fun getBackgroundColor() = backgroundColor

    override fun getRippleColor() = rippleColor

    //// Fonts ////

    override fun getTitleFont() = titleFont

    override fun getSubtitleFont() = subtitleFont

    override fun getPrimaryTextFont() = primaryTextFont

    override fun getSecondaryTextFont() = secondaryTextFont

    //// Misc ////

    override fun getScrollBarWidth() = 10

    override fun getScrollBarCornerRadius() = 5

    override fun getCircularProgressRadius() = 30f

    override fun getCircularProgressStrokeWidth() = 5f

    override fun getProgressBarWidth() = 5f

    override fun toString(): String {
        return "LightMaterialTheme"
    }

}

/**
 * The dark theme that can be used with any app. A basic dark material theme.
 */
object DarkMaterialTheme : Theme() {

    //// Colors ////
    private val primaryColor = Color.decode("#212121")!!
    private val darkPrimaryColor = Color.decode("#1E1E1E")!! // could be #000000 for a true dark feel
    private val accentColor = Color.decode("#B71C1C")!! // dark color for a dark atmosphere/environment
    private val titleColor = Color.decode("#FFFFFF")!!
    private val subtitleColor = Color.decode("#EEEEEE")!!
    private val primaryTextColor = Color.decode("#FFFFFF")!!
    private val secondaryTextColor = Color.decode("#EEEEEE")!!
    private val backgroundColor = Color.decode("#303030")!!
    private val dividerColor = Color.decode("#444444")!!
    private val rippleColor = Color.decode("#EEEEEE")!!

    //// Fonts ////
    private lateinit var titleFont: Font
    private lateinit var subtitleFont: Font
    private lateinit var primaryTextFont: Font
    private lateinit var secondaryTextFont: Font

    override fun init() {
        // load and set the fonts
        val roboto = Font.createFont(Font.TRUETYPE_FONT, File("assets/fonts/Roboto/Roboto-Regular.ttf"))
        titleFont = Font.createFont(Font.TRUETYPE_FONT, File("assets/fonts/Roboto/Roboto-Medium.ttf")).deriveFont(16f)
        subtitleFont = roboto.deriveFont(16f)
        primaryTextFont = roboto.deriveFont(15f)
        secondaryTextFont = roboto.deriveFont(14f)
    }

    override fun getPrimaryColor() = primaryColor

    override fun getDarkPrimaryColor() = darkPrimaryColor

    override fun getAccentColor() = accentColor

    override fun getTitleColor() = titleColor

    override fun getSubtitleColor() = subtitleColor

    override fun getPrimaryTextColor() = primaryTextColor

    override fun getSecondaryTextColor() = secondaryTextColor

    override fun getDividerColor() = dividerColor

    override fun getBackgroundColor() = backgroundColor

    override fun getRippleColor() = rippleColor

    //// Fonts ////

    override fun getTitleFont() = titleFont

    override fun getSubtitleFont() = subtitleFont

    override fun getPrimaryTextFont() = primaryTextFont

    override fun getSecondaryTextFont() = secondaryTextFont

    //// Misc ////

    override fun getScrollBarWidth() = 10

    override fun getScrollBarCornerRadius() = 5

    override fun getCircularProgressRadius() = 30f

    override fun getCircularProgressStrokeWidth() = 5f

    override fun getProgressBarWidth() = 5f

    override fun toString(): String {
        return "DarkMaterialTheme"
    }

}