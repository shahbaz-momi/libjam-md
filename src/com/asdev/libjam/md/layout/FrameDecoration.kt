package com.asdev.libjam.md.layout

import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.CompoundDrawable
import com.asdev.libjam.md.drawable.ShadowDrawable
import com.asdev.libjam.md.theme.COLOR_TITLE
import com.asdev.libjam.md.theme.FONT_TITLE
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.view.TextView

/**
 * Created by Asdev on 10/14/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

/**
 * A custom material design frame decoration.
 */
class FrameDecoration(title: String): LinearLayout() {

    private val navBar: RelativeLayout
    private val toolbar: RelativeLayout
    private val titleText: TextView

    init {
        navBar = RelativeLayout()
        toolbar = RelativeLayout()
        titleText = TextView(title)

        // set fonts and colors of title text
        titleText.setThemeColor(COLOR_TITLE)
        titleText.setThemeFont(FONT_TITLE)
        titleText.gravity = GRAVITY_MIDDLE_LEFT
        titleText.setPadding(20f)
        // set the gravity of title text to middle left
        titleText.applyLayoutParameters(
                GenericLayoutParamList() with ("gravity" to GRAVITY_MIDDLE_LEFT)
        )

        // set the background colors for the navbar and toolbars
        onThemeChange(THEME, THEME)

        // set min and max sizes
        navBar.maxSize = FloatDim(-2f, 35f)
        navBar.minSize = navBar.maxSize
        toolbar.maxSize = FloatDim(-2f, 50f)
        toolbar.minSize = toolbar.maxSize
        maxSize = FloatDim(-2f, 85f)
        minSize = maxSize

        // add the title text to the toolbar
        toolbar.addChild(titleText)

        addChild(navBar)
        addChild(toolbar)
    }

    fun setDrawAboveAll(b: Boolean) = if(b) zIndex = 99 else zIndex = 0

    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        // on theme change the children
        super.onThemeChange(prevTheme, newTheme)

        // container is just background
        background = ColorDrawable(newTheme.getBackgroundColor())
        // set the navbar as dark primary
        navBar.background = ColorDrawable(newTheme.getDarkPrimaryColor())
        // the toolbar is just primary
        toolbar.background = CompoundDrawable(
                // draw the shadow
                ShadowDrawable(radius = 20f, opacity = 0.5f, clipLeft = true, clipRight = true),
                // then the color
                ColorDrawable(newTheme.getPrimaryColor())
        )
    }

}