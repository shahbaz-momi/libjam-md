package com.asdev.libjam.md.menu

import com.asdev.libjam.md.animation.FactorableDecelerateInterpolator
import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.drawable.ColorDrawable
import com.asdev.libjam.md.drawable.CompoundDrawable
import com.asdev.libjam.md.drawable.ShadowDrawable
import com.asdev.libjam.md.layout.OverlayLayoutParams
import com.asdev.libjam.md.layout.OverlayLinearLayout
import com.asdev.libjam.md.layout.newOverlayLayoutParams
import com.asdev.libjam.md.theme.COLOR_DIVIDER
import com.asdev.libjam.md.theme.COLOR_PRIMARY_TEXT
import com.asdev.libjam.md.theme.THEME
import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.FloatDim
import com.asdev.libjam.md.util.FloatPoint
import com.asdev.libjam.md.view.*
import res.R
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.MouseEvent

/**
 * Created by Asdev on 03/17/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.menu
 */

/**
 * A class which is used to manage and display a context menu on a root view.
 */
class ContextMenu {

    /**
     * A list of the contained items within this ContextMenu.
     */
    private lateinit var items: List<ContextMenuItem>

    /**
     * The layout containing the items.
     */
    private val layout = OverlayLinearLayout()

    /**
     * The background of this context menu.
     */
    private var background = CompoundDrawable(ShadowDrawable(), ColorDrawable(THEME.getBackgroundColor()))

    private val animator = FloatValueAnimator(550f, FactorableDecelerateInterpolator(2f), 0f, 0.3f, 1f)

    init {
        // initialize the layout
        layout.onAttach()
        layout.visibility = VISIBILITY_INVISIBLE
    }

    fun onThemeChange(oldTheme: Theme, newTheme: Theme) {
        layout.onThemeChange(oldTheme, newTheme)

        background = CompoundDrawable(ShadowDrawable(), ColorDrawable(THEME.getBackgroundColor()))
    }

    /**
     * Sets the context menu items of this menu.
     */
    fun setItems(vararg items: ContextMenuItem) {
        this.items = items.asList()

        notifyChangedItems()
    }

    /**
     * Sets the context menu items of this menu.
     */
    fun setItems(items: List<ContextMenuItem>) {
       this.items = items

        notifyChangedItems()
    }

    /**
     * Returns the context menu items of this menu.
     */
    fun getItems() = items

    /**
     * Called when the items within this list are changed.
     */
    private fun notifyChangedItems() {
        // remove all items from the layout
        layout.removeAllChildren()
        layout.minSize = FloatDim(-1f, -1f)
        layout.maxSize = FloatDim(-1f, -1f)
        
        // add the children back from the list
        for(it in items) {
            val v = it.constructView()
            it.bindToView(v, this)
            layout.addChild(v)
        }

        // measure the new view.
        val params = layout.onMeasure(newOverlayLayoutParams()) as? OverlayLayoutParams?: throw IllegalArgumentException("OverlayView must return OverlayLayoutParams after onMeasure()")
        val size = params.minSize

        if(size.w <= 0f) {
            size.w = THEME.getContextMenuWidth()
        }

        if(size.h <= 0f) {
            size.h = THEME.getContextMenuHeight()
        }

        layout.onLayout(size)
    }

    /**
     * Returns the position of this context menu.
     */
    fun getPosition() = layout.position

    /**
     * Returns the size of this context menu.
     */
    fun getSize() = layout.layoutSize

    /**
     * Shows the context menu at the given location.
     */
    fun show(x: Float, y: Float) {
        layout.onPostLayout(FloatPoint(x, y), null)
        layout.visibility = VISIBILITY_VISIBLE

        if(animator.isRunning())
            animator.cancel()

        animator.start()
    }

    /**
     * Hides the context menu.
     */
    fun hide() {
        layout.visibility = VISIBILITY_INVISIBLE
        layout.onStateChanged(layout.state, View.State.STATE_NORMAL)

        animator.cancel()
    }

    fun loop() {
        layout.loop()

        animator.loop()

        if(animator.isRunning()) {
            layout.requestRepaint()
        }
    }

    fun onDraw(g: Graphics2D) {
        if(layout.visibility != VISIBILITY_VISIBLE)
            return

        g.translate(layout.position.x.toInt() + layout.translationX.toInt(), layout.position.y.toInt() + layout.translationY.toInt())
        val animW = layout.layoutSize.w * animator.getValue()
        val animH = layout.layoutSize.h * animator.getValue()

        background.draw(g, 0f, 0f, animW, animH)

        val clip = g.clip
        g.clipRect(0, 0, animW.toInt(), animH.toInt())
        layout.onDraw(g)
        g.clip = clip

        g.translate(-layout.position.x.toInt() - layout.translationX.toInt(), -layout.position.y.toInt() - layout.translationY.toInt())
    }

    fun onPostDraw(g: Graphics2D) {
        if(layout.visibility != VISIBILITY_VISIBLE)
            return

        g.translate(layout.position.x.toInt() + layout.translationX.toInt(), layout.position.y.toInt() + layout.translationY.toInt())
        layout.onPostDraw(g)
        g.translate(-layout.position.x.toInt() - layout.translationX.toInt(), -layout.position.y.toInt() - layout.translationY.toInt())
    }

    /////// Mouse Events ////////

    /**
     * Called when the mouse is pressed on the context menu.
     */
    fun onMousePress(e: MouseEvent, pos: FloatPoint) {
        layout.onMousePress(e, Point(pos.x.toInt(), pos.y.toInt()))
    }

    /**
     * Called when the mouse is released on the context menu.
     */
    fun onMouseRelease(e: MouseEvent, pos: FloatPoint) {
        layout.onMouseRelease(e, Point(pos.x.toInt(), pos.y.toInt()))
    }

    /**
     * Called when the mouse is dragged on the context menu.
     */
    fun onMouseDragged(e: MouseEvent, pos: FloatPoint) {
        layout.onMouseDragged(e, Point(pos.x.toInt(), pos.y.toInt()))
    }

    /**
     * Called when the mouse is moved on the context menu.
     */
    fun onMouseMoved(e: MouseEvent, pos: FloatPoint) {
        layout.onMouseMoved(e, Point(pos.x.toInt(), pos.y.toInt()))
    }
}

/**
 * A class which is used to represent a context menu item.
 */
abstract class ContextMenuItem {

    /**
     * Called during initialization to construct a View which should visually represent this item.
     */
    abstract fun constructView(): View

    /**
     * Called to bind the data of this item to the given view.
     */
    abstract fun bindToView(v: View, root: ContextMenu)

}

/**
 * A class which is a textual context menu item.
 */
open class ContextMenuText(val text: String): ContextMenuItem() {

    override fun constructView(): View {
        val v = TextView(text)
        v.gravity = R.gravity.middle_left
        v.minSize = FloatDim(-1f, 46f)
        v.maxSize = FloatDim(-1f, 50f)
        v.paddingLeft = 15f

        return v
    }

    override fun bindToView(v: View, root: ContextMenu) {
        if(v is TextView)
            v.text = text
    }

}

/**
 * A class which is a actionable context menu item.
 * The action must return whether or not to hide the menu after performing the action.
 */
open class ContextMenuAction(text: String, val action: (Unit) -> Boolean): ContextMenuText(text) {

    override fun constructView(): View {
        val v = ButtonView(text, BUTTON_TYPE_FLAT)
        v.gravity = R.gravity.middle_left
        v.minSize = FloatDim(-1f, 46f)
        v.maxSize = FloatDim(-1f, 50f)
        v.setThemeColor(COLOR_PRIMARY_TEXT)
        v.paddingLeft = 15f

        return v
    }

    override fun bindToView(v: View, root: ContextMenu) {
        if(v is ButtonView) {
            v.text = text
            v.onClickListener = {_, _ ->
                if(action.invoke(Unit))
                    root.hide()
            }
        }
    }

}

/**
 * A class which is a separator in a context menu.
 */
object ContextMenuSeparator: ContextMenuItem() {

    override fun constructView(): View {
        val v = View()
        v.background = ColorDrawable(THEME.getColor(COLOR_DIVIDER))
        v.minSize = FloatDim(-1f, 1f)
        v.maxSize = FloatDim(-1f, 1f)
        return v
    }

    override fun bindToView(v: View, root: ContextMenu) {}

}