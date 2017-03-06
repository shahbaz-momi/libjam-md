package res
import com.asdev.libjam.md.layout.*
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.generateRandomId
import java.awt.Color
import java.awt.Font
import java.io.File

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