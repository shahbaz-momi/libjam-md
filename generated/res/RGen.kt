package res
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.generateRandomId
import java.awt.Color
import java.awt.Font
import java.io.File

const val type_int = "int"
const val type_string = "string"
const val type_drawable = "drawable"
const val type_gravity = "gravity"
const val type_font = "font"
const val type_color = "color"
const val type_float = "float"

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
	}

	object ints {
		val example_int = 18
	}

	object colors {
		val primary_dark = Color(79, 163, 201)
		val primary = Color(157, 163, 243)
		val example_hex_color = Color(255, 0, 255)
		val example_rgb_color = Color(255, 255, 255)
		val example_float_rgb_color = Color(255, 255, 255)
		val example_rgba_color = Color(255, 200, 240, 255)
		val example_float_rgba_color = Color(110, 74, 74, 255)
		val example_hsb_color = Color(255, 0, 0)
	}

	object attrs {
		object ProgressView {
			val visibility = "visibility" to type_int
			val id = "id" to type_string
			val minSize = "minSize" to type_string
			val maxSize = "maxSize" to type_string
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
			val type = "type" to type_string
			val progress = "progress" to type_float
		}

		object CircularProgressView {
			val visibility = "visibility" to type_int
			val id = "id" to type_string
			val minSize = "minSize" to type_string
			val maxSize = "maxSize" to type_string
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
			val visibility = "visibility" to type_int
			val id = "id" to type_string
			val minSize = "minSize" to type_string
			val maxSize = "maxSize" to type_string
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
			val text_size = "text-size" to type_int
			val text_color = "text-color" to type_color

		}

		object PaddingLayout {
			val visibility = "visibility" to type_int
			val id = "id" to type_string
			val minSize = "minSize" to type_string
			val maxSize = "maxSize" to type_string
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

		object RelativeLayout {
			val visibility = "visibility" to type_int
			val id = "id" to type_string
			val minSize = "minSize" to type_string
			val maxSize = "maxSize" to type_string
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
			val visibility = "visibility" to type_int
			val id = "id" to type_string
			val minSize = "minSize" to type_string
			val maxSize = "maxSize" to type_string
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
			val text_size = "text-size" to type_int
			val text_color = "text-color" to type_color
		}

		object OverlayLinearLayout {
			val visibility = "visibility" to type_int
			val id = "id" to type_string
			val minSize = "minSize" to type_string
			val maxSize = "maxSize" to type_string
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

		object View {
			val visibility = "visibility" to type_int
			val id = "id" to type_string
			val minSize = "minSize" to type_string
			val maxSize = "maxSize" to type_string
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

		object ScrollLayout {
			val visibility = "visibility" to type_int
			val id = "id" to type_string
			val minSize = "minSize" to type_string
			val maxSize = "maxSize" to type_string
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
			val visibility = "visibility" to type_int
			val id = "id" to type_string
			val minSize = "minSize" to type_string
			val maxSize = "maxSize" to type_string
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
			val visibility = "visibility" to type_int
			val id = "id" to type_string
			val minSize = "minSize" to type_string
			val maxSize = "maxSize" to type_string
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
			val visibility = "visibility" to type_int
			val id = "id" to type_string
			val minSize = "minSize" to type_string
			val maxSize = "maxSize" to type_string
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