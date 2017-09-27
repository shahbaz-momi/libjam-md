package com.asdev.libjam.md.layout

import com.asdev.libjam.md.animation.AccelerateInterpolator
import com.asdev.libjam.md.animation.DecelerateInterpolator
import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.view.BUTTON_TYPE_FLAT
import com.asdev.libjam.md.view.ButtonView
import com.asdev.libjam.md.view.View
import res.R

/**
 * Created by Asdev on 04/27/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

private const val DRAWER_SLIDE_ANIM_TIME = 350f
private const val DRAWER_ITEM_H = 50f

/**
 * A utility class that creates a drawer over a layout.
 */
class DrawerLayout private constructor(val view: OverlayViewGroup) {

    /**
     * Toggles the current drawer state, syncing to the given state.
     */
    fun toggle(shown: Boolean) {
        if(shown) {
            view.translationXAnimator.setFromValue(view.translationX).setToValue(0f).setDuration(DRAWER_SLIDE_ANIM_TIME).setInterpolator(DecelerateInterpolator).start()
        } else {
            view.translationXAnimator.setFromValue(view.translationX).setToValue(-(view.layoutSize.w + 10f)).setDuration(DRAWER_SLIDE_ANIM_TIME).setInterpolator(AccelerateInterpolator).start()
        }
    }

    companion object {
        /**
         * Creates a base drawer with the given parameters.
         */
        fun makeDrawer(header: View?, vararg items: DrawerItem): DrawerLayout {
            // as follow:
            // [  HEADER  ]
            // [          ]
            // [   LIST   ]
            // [          ]

            val parent = OverlayLinearLayout()
            parent.background = ColorDrawable(R.theme.background)
            parent.setOrientation(ORIENTATION_HORIZONTAL)

            if (header != null)
                parent.addChild(header)

            val adapter = DrawerAdapter(items)

            val list = ListLayout(adapter)
            parent.addChild(ScrollLayout(list))

            // default hidden
            parent.translationX = -250f
            // default to left
            parent.applyParameters(GenericParamList() with (LAYOUT_PARAM_GRAVITY to GRAVITY_MIDDLE_LEFT))
            parent.maxSize.w = 250f
            parent.maxSize.h = 10000000f

            return DrawerLayout(parent)
        }
    }

    /**
     * A data class which holds a label and icon for a drawer item.
     */
    data class DrawerItem(var label: String) {

        /**
         * The activated/deactivated state of this item.
         */
        var state = false

        /**
         * Binds the data of this item to the given view.
         */
        fun bindView(view: View) {
            val button = view as ButtonView

            button.text = label
        }

    }

    /**
     * A class that creates a standard list of drawer items.
     */
    class DrawerAdapter(val items: Array<out DrawerItem>): ListLayoutAdapter {

        override fun getItemCount() = items.size

        override fun bindView(parent: ListLayout, view: View, index: Int) {
            items[index].bindView(view)
            // set the on click listener to this
            view.onClickListener = { _, _ -> onSelectionChange(index) }
        }

        /**
         * Called when the selected drawer item changes.
         */
        private fun onSelectionChange(index: Int) {

        }

        override fun constructView(index: Int): View {
            val button = ButtonView("", BUTTON_TYPE_FLAT)
            button.gravity = GRAVITY_MIDDLE_LEFT
            button.paddingLeft = 20f

            button.maxSize.h = DRAWER_ITEM_H + 1
            button.minSize.h = DRAWER_ITEM_H

            return button
        }

    }
}