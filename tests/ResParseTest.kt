import com.asdev.libjam.md.drawable.ImageDrawable
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.theme.COLOR_PRIMARY
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.xml.*
import res.R
import java.awt.Dimension

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

    println(R.dims.example_dim)
    println(parseDimReference("\${R.dims.example_dim}"))
    println(parseDimReference("100.5fx100.5f"))
    println(parseDimReference("\${R.ints.example_int}x\${R.ints.example_int}"))

    val v = inflateLayout(R.layout.layout_testing)
    val frame = GLG2DRootView(v, "Testing", Dimension(500, 500), true)
    frame.showFrame()
}