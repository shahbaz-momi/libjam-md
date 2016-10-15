package com.asdev.libjam.md.layout

import com.asdev.libjam.md.drawable.ShadowDrawable
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.View
import java.awt.Graphics2D

/**
 * Created by Asdev on 10/14/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */
class ElevatedLayout(val child: View, val radius: Float = 30f, opacity: Float = 0.25f, shadowYOffset: Float = 3f): View() {

    private val shadow: ShadowDrawable

    init {
        shadow = ShadowDrawable(radius, opacity, shadowYOffset, false, false)
    }

    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)
        child.onThemeChange(prevTheme, newTheme)
    }

    override fun onMeasure(result: LayoutParams): LayoutParams {
        val lp = child.onMeasure(result)

        if(lp.minSize.w > 0f && lp.minSize.w > minSize.w) {
            minSize = FloatDim(lp.minSize.w + radius * 2f, minSize.h)
        }

        if(lp.minSize.h > 0f && lp.minSize.h > minSize.h) {
            minSize = FloatDim(minSize.w, lp.minSize.h + radius * 2f)
        }

        if(lp.maxSize.w > 0f && maxSize.w > 0f && lp.maxSize.w > maxSize.w) {
            maxSize = FloatDim(lp.maxSize.w + radius * 2f, maxSize.h)
        }

        if(lp.maxSize.h > 0f && maxSize.h > 0f && lp.maxSize.h > maxSize.h) {
            maxSize = FloatDim(maxSize.w, lp.maxSize.h + radius * 2f)
        }

        if(maxSize.w < minSize.w && maxSize.w > 0f)
            maxSize = FloatDim(minSize.w, maxSize.h)

        if(maxSize.h < minSize.h && maxSize.h > 0f)
            maxSize = FloatDim(maxSize.w, minSize.h)

        return super.onMeasure(result)
    }

    override fun onLayout(newSize: FloatDim) {
        super.onLayout(newSize)
        child.onLayout(FloatDim(newSize.w - radius * 2f, newSize.h - radius * 2f))

    }

    override fun onDraw(g: Graphics2D) {
        if(layoutSize.w < 0f || layoutSize.h < 0f)
            return

        shadow.draw(g, radius, radius, layoutSize.w - radius * 2f, layoutSize.h - radius * 2f)
        // draw the background behind the view
        // background?.draw(g, radius, radius, layoutSize.w - radius * 2f, layoutSize.h - radius * 2f)
        // translate the canvas
        g.translate(radius.toInt(), radius.toInt())
        // draw the child
        child.onDraw(g)
        g.translate(-radius.toInt(), -radius.toInt())
    }
}