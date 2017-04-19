package res
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.generateRandomId
import com.asdev.libjam.md.drawable.newImageDrawable
import com.asdev.libjam.md.util.loadFontFile
import java.awt.Color
import java.awt.Font
import java.io.File
import com.asdev.libjam.md.util.FloatDim

const val type_int = "int"
const val type_string = "string"
const val type_drawable = "drawable"
const val type_gravity = "gravity"
const val type_font = "font"
const val type_color = "color"
const val type_float = "float"
const val type_dim = "dim"

object R {
	object strings {
		val example_string4 = "Another one!"
		val example_string5 = "happy"
		val example_string6 = "Example string 6"
		val example_string = "Example string!"
		val example_string2 = "sad"
		val example_string3 = "Example string 3!"
		val searching = "Searching..."
		val snackbar_test = "Super long snackbar string/text can go here..."
		val dismiss_buttom = "DISMISS"
		val button_text = "Change background"
	}

	object ints {
		val example_int = 18
		val example_int2 = 19
	}

	object colors {
		val light_material_primary = Color(33, 150, 243, 255)
		val light_material_primary_dark = Color(25, 118, 210, 255)
		val light_material_accent = Color(255, 193, 7, 255)
		val light_material_title = Color(255, 255, 255, 255)
		val light_material_subtitle = Color(187, 222, 251, 255)
		val light_material_primary_text = Color(33, 33, 33, 255)
		val light_material_secondary_text = Color(117, 117, 117, 255)
		val light_material_background = Color(238, 238, 238, 255)
		val light_material_divider = Color(189, 189, 189, 255)
		val light_material_ripple = Color(68, 68, 68, 255)
		val dark_material_primary = Color(33, 33, 33, 255)
		val dark_material_primary_dark = Color(30, 30, 30, 255)
		val dark_material_accent = Color(183, 28, 28, 255)
		val dark_material_title = Color(255, 255, 255, 255)
		val dark_material_subtitle = Color(238, 238, 238, 255)
		val dark_material_primary_text = Color(255, 255, 255, 255)
		val dark_material_secondary_text = Color(238, 238, 238, 255)
		val dark_material_background = Color(48, 48, 48, 255)
		val dark_material_divider = Color(68, 68, 68, 255)
		val dark_material_ripple = Color(238, 238, 238, 255)
		val primary_dark = Color(79, 163, 201, 255)
		val primary = Color(157, 163, 243, 255)
		val example_hex_color = Color(255, 0, 255, 255)
		val example_rgb_color = Color(255, 255, 255, 255)
		val example_float_rgb_color = Color(255, 255, 255, 255)
		val example_rgba_color = Color(255, 200, 240, 255)
		val example_float_rgba_color = Color(110, 74, 74, 255)
		val example_hsb_color = Color(255, 0, 0, 255)
	}

	object attrs {
		object ProgressView {
			val visibility = "visibility" to type_string
			val id = "id" to type_string
			val minSize = "minSize" to type_dim
			val maxSize = "maxSize" to type_dim
			val background = "background" to type_drawable
			val z_index = "z-index" to type_int
			val translation_x = "translation-x" to type_int
			val translation_y = "translation-y" to type_int
			val overclip_left = "overclip-left" to type_int
			val overclip_right = "overclip-right" to type_int
			val overclip_top = "overclip-top" to type_int
			val overclip_bottom = "overclip-bottom" to type_int
			val gravity = "gravity" to type_gravity

			val padding_left = "padding-left" to type_int
			val padding_right = "padding-right" to type_int
			val type = "type" to type_string
			val progress = "progress" to type_float
		}

		object CircularProgressView {
			val visibility = "visibility" to type_string
			val id = "id" to type_string
			val minSize = "minSize" to type_dim
			val maxSize = "maxSize" to type_dim
			val background = "background" to type_drawable
			val z_index = "z-index" to type_int
			val translation_x = "translation-x" to type_int
			val translation_y = "translation-y" to type_int
			val overclip_left = "overclip-left" to type_int
			val overclip_right = "overclip-right" to type_int
			val overclip_top = "overclip-top" to type_int
			val overclip_bottom = "overclip-bottom" to type_int
			val gravity = "gravity" to type_gravity

			val padding_left = "padding-left" to type_int
			val padding_right = "padding-right" to type_int
			val padding_top = "padding-top" to type_int
			val padding_bottom = "padding-bottom" to type_int
			val circle_radius = "circle-radius" to type_int
			val circle_stroke_width = "circle-stroke-width" to type_int
			val circle_color = "circle-color" to type_color
			val circle_gravity = "circle-gravity" to type_gravity
		}

		object ButtonView {
			val visibility = "visibility" to type_string
			val id = "id" to type_string
			val minSize = "minSize" to type_dim
			val maxSize = "maxSize" to type_dim
			val background = "background" to type_drawable
			val z_index = "z-index" to type_int
			val translation_x = "translation-x" to type_int
			val translation_y = "translation-y" to type_int
			val overclip_left = "overclip-left" to type_int
			val overclip_right = "overclip-right" to type_int
			val overclip_top = "overclip-top" to type_int
			val overclip_bottom = "overclip-bottom" to type_int
			val gravity = "gravity" to type_gravity

			val padding_left = "padding-left" to type_int
			val padding_right = "padding-right" to type_int
			val padding_top = "padding-top" to type_int
			val padding_bottom = "padding-bottom" to type_int
			val text_gravity = "text-gravity" to type_gravity
			val text = "text" to type_string
			val text_font = "text-font" to type_font
			val text_color = "text-color" to type_color

			val type = "type" to type_string
		}

		object PaddingLayout {
			val visibility = "visibility" to type_string
			val id = "id" to type_string
			val minSize = "minSize" to type_dim
			val maxSize = "maxSize" to type_dim
			val background = "background" to type_drawable
			val z_index = "z-index" to type_int
			val translation_x = "translation-x" to type_int
			val translation_y = "translation-y" to type_int
			val overclip_left = "overclip-left" to type_int
			val overclip_right = "overclip-right" to type_int
			val overclip_top = "overclip-top" to type_int
			val overclip_bottom = "overclip-bottom" to type_int
			val gravity = "gravity" to type_gravity

			val padding_left = "padding-left" to type_int
			val padding_right = "padding-right" to type_int
			val padding_top = "padding-top" to type_int
			val padding_bottom = "padding-bottom" to type_int
		}

		object View {
			val visibility = "visibility" to type_string
			val id = "id" to type_string
			val minSize = "minSize" to type_dim
			val maxSize = "maxSize" to type_dim
			val background = "background" to type_drawable
			val z_index = "z-index" to type_int
			val translation_x = "translation-x" to type_int
			val translation_y = "translation-y" to type_int
			val overclip_left = "overclip-left" to type_int
			val overclip_right = "overclip-right" to type_int
			val overclip_top = "overclip-top" to type_int
			val overclip_bottom = "overclip-bottom" to type_int
			val gravity = "gravity" to type_gravity
		}

		object com_asdev_libjam_md_tests_CustomViewTest {
			val visibility = "visibility" to type_string
			val id = "id" to type_string
			val minSize = "minSize" to type_dim
			val maxSize = "maxSize" to type_dim
			val background = "background" to type_drawable
			val z_index = "z-index" to type_int
			val translation_x = "translation-x" to type_int
			val translation_y = "translation-y" to type_int
			val overclip_left = "overclip-left" to type_int
			val overclip_right = "overclip-right" to type_int
			val overclip_top = "overclip-top" to type_int
			val overclip_bottom = "overclip-bottom" to type_int
			val gravity = "gravity" to type_gravity

		}

		object RelativeLayout {
			val visibility = "visibility" to type_string
			val id = "id" to type_string
			val minSize = "minSize" to type_dim
			val maxSize = "maxSize" to type_dim
			val background = "background" to type_drawable
			val z_index = "z-index" to type_int
			val translation_x = "translation-x" to type_int
			val translation_y = "translation-y" to type_int
			val overclip_left = "overclip-left" to type_int
			val overclip_right = "overclip-right" to type_int
			val overclip_top = "overclip-top" to type_int
			val overclip_bottom = "overclip-bottom" to type_int
			val gravity = "gravity" to type_gravity

		}

		object TextView {
			val visibility = "visibility" to type_string
			val id = "id" to type_string
			val minSize = "minSize" to type_dim
			val maxSize = "maxSize" to type_dim
			val background = "background" to type_drawable
			val z_index = "z-index" to type_int
			val translation_x = "translation-x" to type_int
			val translation_y = "translation-y" to type_int
			val overclip_left = "overclip-left" to type_int
			val overclip_right = "overclip-right" to type_int
			val overclip_top = "overclip-top" to type_int
			val overclip_bottom = "overclip-bottom" to type_int
			val gravity = "gravity" to type_gravity

			val padding_left = "padding-left" to type_int
			val padding_right = "padding-right" to type_int
			val padding_top = "padding-top" to type_int
			val padding_bottom = "padding-bottom" to type_int
			val text_gravity = "text-gravity" to type_gravity
			val text = "text" to type_string
			val text_font = "text-font" to type_font
			val text_color = "text-color" to type_color
		}

		object OverlayLinearLayout {
			val visibility = "visibility" to type_string
			val id = "id" to type_string
			val minSize = "minSize" to type_dim
			val maxSize = "maxSize" to type_dim
			val background = "background" to type_drawable
			val z_index = "z-index" to type_int
			val translation_x = "translation-x" to type_int
			val translation_y = "translation-y" to type_int
			val overclip_left = "overclip-left" to type_int
			val overclip_right = "overclip-right" to type_int
			val overclip_top = "overclip-top" to type_int
			val overclip_bottom = "overclip-bottom" to type_int
			val gravity = "gravity" to type_gravity

			val orientation = "orientation" to type_string
		}

		object ScrollLayout {
			val visibility = "visibility" to type_string
			val id = "id" to type_string
			val minSize = "minSize" to type_dim
			val maxSize = "maxSize" to type_dim
			val background = "background" to type_drawable
			val z_index = "z-index" to type_int
			val translation_x = "translation-x" to type_int
			val translation_y = "translation-y" to type_int
			val overclip_left = "overclip-left" to type_int
			val overclip_right = "overclip-right" to type_int
			val overclip_top = "overclip-top" to type_int
			val overclip_bottom = "overclip-bottom" to type_int
			val gravity = "gravity" to type_gravity

		}

		object OverlayView {
			val visibility = "visibility" to type_string
			val id = "id" to type_string
			val minSize = "minSize" to type_dim
			val maxSize = "maxSize" to type_dim
			val background = "background" to type_drawable
			val z_index = "z-index" to type_int
			val translation_x = "translation-x" to type_int
			val translation_y = "translation-y" to type_int
			val overclip_left = "overclip-left" to type_int
			val overclip_right = "overclip-right" to type_int
			val overclip_top = "overclip-top" to type_int
			val overclip_bottom = "overclip-bottom" to type_int
			val gravity = "gravity" to type_gravity

		}

		object LinearLayout {
			val visibility = "visibility" to type_string
			val id = "id" to type_string
			val minSize = "minSize" to type_dim
			val maxSize = "maxSize" to type_dim
			val background = "background" to type_drawable
			val z_index = "z-index" to type_int
			val translation_x = "translation-x" to type_int
			val translation_y = "translation-y" to type_int
			val overclip_left = "overclip-left" to type_int
			val overclip_right = "overclip-right" to type_int
			val overclip_top = "overclip-top" to type_int
			val overclip_bottom = "overclip-bottom" to type_int
			val gravity = "gravity" to type_gravity

			val orientation = "orientation" to type_string
		}

		object ElevatedLayout {
			val visibility = "visibility" to type_string
			val id = "id" to type_string
			val minSize = "minSize" to type_dim
			val maxSize = "maxSize" to type_dim
			val background = "background" to type_drawable
			val z_index = "z-index" to type_int
			val translation_x = "translation-x" to type_int
			val translation_y = "translation-y" to type_int
			val overclip_left = "overclip-left" to type_int
			val overclip_right = "overclip-right" to type_int
			val overclip_top = "overclip-top" to type_int
			val overclip_bottom = "overclip-bottom" to type_int
			val gravity = "gravity" to type_gravity

			val shadow_radius = "shadow-radius" to type_int
			val shadow_opacity = "shadow-opacity" to type_int
			val shadow_y_offset = "shadow-y-offset" to type_int
			val rounded_corner_radius = "rounded-corner-radius" to type_int
		}

	}

	object layout {
		val layout_main = "C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\generated\\layout_main.xml"
		val layout_on_boarding = "C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\generated\\layout_on_boarding.xml"
		val layout_testing = "C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\generated\\layout_testing.xml"
		val layout_perf_testing = "C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\generated\\layout_perf_testing.xml"
	}

	object id {
		val root_linear_layout = "root-linear-layout"
		val greeting_text = "greeting-text"
		val progress_view = "progress-view"
		val testing_button = "testing-button"
		val child_container = "child-container"
	}

	object drawables {
		val texture = newImageDrawable("C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\xml\\04drawables\\texture.png")
		val welcome_card = newImageDrawable("C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\xml\\04drawables\\welcome_card.jpg")
	}

	object fonts {
		val OpenSans_Bold = loadFontFile("C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\xml\\05fonts\\OpenSans\\OpenSans-Bold.ttf")
		val OpenSans_BoldItalic = loadFontFile("C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\xml\\05fonts\\OpenSans\\OpenSans-BoldItalic.ttf")
		val OpenSans_ExtraBold = loadFontFile("C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\xml\\05fonts\\OpenSans\\OpenSans-ExtraBold.ttf")
		val OpenSans_ExtraBoldItalic = loadFontFile("C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\xml\\05fonts\\OpenSans\\OpenSans-ExtraBoldItalic.ttf")
		val OpenSans_Italic = loadFontFile("C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\xml\\05fonts\\OpenSans\\OpenSans-Italic.ttf")
		val OpenSans_Light = loadFontFile("C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\xml\\05fonts\\OpenSans\\OpenSans-Light.ttf")
		val OpenSans_LightItalic = loadFontFile("C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\xml\\05fonts\\OpenSans\\OpenSans-LightItalic.ttf")
		val OpenSans_Regular = loadFontFile("C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\xml\\05fonts\\OpenSans\\OpenSans-Regular.ttf")
		val OpenSans_Semibold = loadFontFile("C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\xml\\05fonts\\OpenSans\\OpenSans-Semibold.ttf")
		val OpenSans_SemiboldItalic = loadFontFile("C:\\Users\\Shahbaz Momi\\IdeaProjects\\libjam-md\\xml\\05fonts\\OpenSans\\OpenSans-SemiboldItalic.ttf")
	}

	object dims {
		val example_dim = FloatDim(100.5f, 100.5f)
	}

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

		val font_primary = THEME.getPrimaryTextFont()
		val font_secondary = THEME.getSecondaryTextFont()
		val font_title = THEME.getTitleFont()
		val font_subtitle = THEME.getSubtitleFont()
	}
}