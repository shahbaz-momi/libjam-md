package com.asdev.libjam.md.tests

import com.asdev.libjam.md.drawable.NinePatchDrawable
import org.junit.Test
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by Asdev on 10/13/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */
class NinePatchDrawableTest {

    @Test
    fun testNinePatchInit() {
        val img = ImageIO.read(File("assets/shadow.9.png"))
        NinePatchDrawable(img)
    }

}