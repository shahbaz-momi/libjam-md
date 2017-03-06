package res

import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.generateRandomId
import java.awt.Color
import java.awt.Font
import java.io.File

/**
 * Example auto-generated res.R object.
 */
object ExampleR {

    /**
     * Sub-object containing layout codes.
     */
    object layout {

        val main_layout = generateRandomId()

    }

    /**
     * Sub-object containing color.
     */
    object colors {

        val primary_dark = Color.decode("#4FA3C9")

    }

    /**
     * Sub-object containing strings.
     */
    object strings {

        val example_string = "Hello, World"

    }

    /**
     * Sub-object containing ints.
     */
    object ints {

        val example_int = 18

    }

    /**
     * Sub-object with ports over values from THEME to XML compatible elements.
     * Usage: ${res.R.theme.title_text_color}
     */
    object theme {
        /**
         * The base props for later usage
         */

        var title_text_color = THEME.getTitleColor()
        var title_font = THEME.getTitleFont()
    }

    /**
     * Sub-object containing themes.
     */
    object themes {

        /**
         * Example theme as defined in the theme based off of the LightMaterialTheme
         */
        object example_theme: Theme() {

            //// Colors ////
            private val primaryColor = Color.decode("#2196F3")!!
            private val darkPrimaryColor = colors.primary_dark // as defined in the theme prop
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

        }

    }


    /**
     * Sub-object containing attributes of CustomViews.
     */
    object attrs {

        /**
         * The types of attributes.
         */
        const val type_int = "int"
        const val type_string = "string"
        const val type_drawable = "drawable"
        const val type_gravity = "gravity"
        const val type_font = "font"
        const val type_color = "color"
        const val type_float = "float"

        /**
         * Example custom class.
         */
        object com_asdev_libjam_md_tests_CustomView {

            val className = "com.asdev.libjam.md.tests.CustomView"

            val customProperty = "customProperty" to type_int
            val customStringProperty = "customStringProperty" to type_string


        }

    }

    /**
     * Sub-object containing ids of Views as defined in XML layout files. The id will will always be the same
     * as the ones defined in the id tag, this is for mere convience.
     */
    object id {

        val ProgressView = "ProgressView"

    }

    /**
     * Sub-object containing valid gravities. Gravities may be expressed as [top|middle|bottom]-[left|middle|right] or
     * ${res.R.gravity.xxx}
     */
    object gravity {

        val top_left = GRAVITY_TOP_LEFT
        val top_middle = GRAVITY_TOP_MIDDLE
        val top_right = GRAVITY_TOP_RIGHT

        val middle_left = GRAVITY_MIDDLE_LEFT
        val middle_middle = GRAVITY_MIDDLE_MIDDLE
        val middle_right = GRAVITY_MIDDLE_RIGHT

        val bottom_left = GRAVITY_BOTTOM_LEFT
        val bottom_middle = GRAVITY_BOTTOM_MIDDLE
        val bottom_right = GRAVITY_BOTTOM_RIGHT

    }

}