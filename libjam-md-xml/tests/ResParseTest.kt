import com.asdev.libjam.md.drawable.ImageDrawable
import com.asdev.libjam.md.theme.COLOR_PRIMARY
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.xml.*
import res.R

fun main(args: Array<String>) {
    THEME.init()

    println(R.strings.example_string)
    println(parseStringReference("Example string!"))
    println(parseStringReference("\${R.strings.example_string}"))

    // testing some ints
    println(R.ints.example_int)
    println(parseIntReference("18"))
    println(parseIntReference("\${R.ints.example_int}"))

    println(R.colors.light_material_primary)
    println(parseColorReference("#2196F3"))
    println(parseColorReference("\${R.colors.light_material_primary}"))

    println(R.theme.primary)
    println(THEME.getColor(COLOR_PRIMARY))
    println(parseColorReference("\${R.theme.primary}"))

    println(R.drawables.welcome_card.img.width)
    println((parseDrawableReference("\${R.drawables.welcome_card}") as ImageDrawable).img.width)

    println(R.fonts.OpenSans_Regular.size)
    println(parseFontReference("\${R.fonts.OpenSans_Regular:\${R.ints.example_int}}").size)
    println(parseFontReference("\${R.theme.font_primary:\${R.ints.example_int}}").size)
}