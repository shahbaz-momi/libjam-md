import com.asdev.libjam.md.drawable.SCALE_TYPE_COVER
import com.asdev.libjam.md.glg2d.GLG2DRootView
import com.asdev.libjam.md.layout.LinearLayout
import com.asdev.libjam.md.layout.ViewGroup
import com.asdev.libjam.md.theme.DarkMaterialTheme
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.ButtonView
import com.asdev.libjam.md.view.TextView
import com.asdev.libjam.md.xml.*
import res.R
import java.awt.Dimension

fun main(args: Array<String>) {
    THEME = DarkMaterialTheme
    THEME.init()

    // inflate and grab an xml layout
    val start = System.nanoTime()
    val v = inflateLayout(R.layout.layout_perf_testing) as ViewGroup
    println("Took " + ((System.nanoTime() - start) / 1000000.0) + "ms for XML Layout inflation.")

//    // example of finding a view from a inflated xml layout
//    val button = v.findViewById(R.id.testing_button) as? ButtonView?: return
//    // example on click event.
//    button.onClickListener = { _, _ ->
//        // create a soft clone of a texture and apply it to this as the background
//        v.background = R.drawables.texture.softClone(SCALE_TYPE_COVER)
//    }

    val childContainer = v.findViewById(R.id.child_container) as? LinearLayout?: return
    for(i in 1 until childContainer.getChildCount()) {
        val c = childContainer.getChildAtIndex(i)
        c.onClickListener = { _, _ -> childContainer.addChild(TextView("Synthetic child").apply { minSize = FloatDim(-1f, 100f) }) }
        (c as TextView).text = "Hello again $i"
        c.requestLayout()
    }

    val frame = GLG2DRootView(v, "ResParseTestKt", Dimension(500, 500), true)
    frame.showFrame()
}