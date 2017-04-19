package res

import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.generateRandomId
import com.asdev.libjam.md.drawable.newImageDrawable
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

        val main_layout = "main_layout" to "\${R.theme.example_theme}"

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

        val primary = THEME.getPrimaryColor()
        val primary_dark = THEME.getDarkPrimaryColor()
        val accent = THEME.getAccentColor()
        val title = THEME.getTitleColor()
        val subtitle = THEME.getSubtitleColor()
        val primary_text = THEME.getPrimaryTextColor()
        val secondary_text = THEME.getSecondaryTextColor()
        val background = THEME.getBackgroundColor()
        val divider = THEME.getDividerColor()
        val ripple = THEME.getRippleColor()

    }

    /**
     * Sub-object containing themes.
     */
    object themes {

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