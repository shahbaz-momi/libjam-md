package com.asdev.libjam.md.layout

import com.asdev.libjam.md.theme.Theme
import com.asdev.libjam.md.util.FloatPoint
import com.asdev.libjam.md.util.generateRandomId
import com.asdev.libjam.md.view.OverlayView
import com.asdev.libjam.md.view.View
import java.awt.event.KeyEvent

/**
 * Created by Asdev on 03/03/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.layout
 */

/**
 * A ViewGroup which is drawn as an overlay on a ViewGroup.
 */
abstract class OverlayViewGroup(id: String = "OverlayViewGroup:${generateRandomId()}", bindViewId: String? = null): OverlayView(id, bindViewId) {


    /**
     * Adds the specified child to this [ViewGroup]
     */
    abstract fun addChild(child: View)

    /**
     * Removes the specified child from this [ViewGroup]
     */
    abstract fun removeChild(child: View)

    /**
     * Returns the children managed by this [ViewGroup]
     */
    abstract fun getChildren(): Array<View>

    /**
     * Returns the number of children managed by this [ViewGroup]
     */
    open fun getChildCount() = getChildren().size

    override fun onPostLayout() {
        super.onPostLayout()

        for(c in getChildren())
            c.onPostLayout()
    }

    /**
     * Returns the child [View] at the specified index (i).
     */
    open fun getChildAtIndex(i: Int): View {
        if(i < 0 || i >= getChildCount())
            throw ArrayIndexOutOfBoundsException("There is no child with the specified index ($i)")

        return getChildren()[i]
    }

    /**
     * Implementation of [onThemeChange]. Calls the [View] implementation and then notifies its children of the theme change.
     */
    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)

        // theme change the children as well
        for(c in getChildren())
            c.onThemeChange(prevTheme, newTheme)
    }

    /**
     * Implementation of [loop]. Calls the [View] implementation and then loops its children.
     */
    override fun loop() {
        super.loop()

        for(i in 0 until getChildCount()) {
            getChildAtIndex(i).loop()
        }

    }

    /**
     * Calls [onKeyTyped] on all the children that are focused.
     */
    override fun onKeyTyped(e: KeyEvent) {
        super.onKeyTyped(e)

        val children = getChildren()

        for(c in children)
            if(c.state == State.STATE_FOCUSED || c.state == State.STATE_HOVER)
                c.onKeyTyped(e)
    }

    /**
     * Calls [onKeyPressed] on all the children that are focused.
     */
    override fun onKeyPressed(e: KeyEvent) {
        super.onKeyPressed(e)

        val children = getChildren()

        for(c in children)
            if(c.state == State.STATE_FOCUSED || c.state == State.STATE_HOVER)
                c.onKeyPressed(e)
    }

    /**
     * Calls [onKeyReleased] on all the children that are focused.
     */
    override fun onKeyReleased(e: KeyEvent) {
        super.onKeyReleased(e)

        val children = getChildren()

        for(c in children)
            if(c.state == State.STATE_FOCUSED || c.state == State.STATE_HOVER)
                c.onKeyReleased(e)
    }

    /**
     * Calculates the x-component of the corresponding gravity.
     * @param bX bounding box X
     * @param bW bounding box W
     * @param oW object W
     */
    protected fun calculateXComp(gravity: Int, bX: Float, bW: Float, oW: Float): Float {
        if(gravity == GRAVITY_TOP_LEFT ||
                gravity == GRAVITY_MIDDLE_LEFT ||
                gravity == GRAVITY_BOTTOM_LEFT) {
            // just return bounding box x
            return bX
        } else if(gravity == GRAVITY_TOP_RIGHT ||
                gravity == GRAVITY_MIDDLE_RIGHT ||
                gravity == GRAVITY_BOTTOM_RIGHT) {
            // align to the right
            return bX + bW - oW
        } else {
            // gravity has to be a center
            return bX + bW / 2f - oW / 2f
        }
    }

    /**
     * Calculates the y-component of the corresponding gravity.
     * @param bY bounding box Y
     * @param bH bounding box H
     * @param oH object H
     */
    protected fun calculateYComp(gravity: Int, bY: Float, bH: Float, oH: Float): Float {
        if(gravity == GRAVITY_TOP_LEFT ||
                gravity == GRAVITY_TOP_MIDDLE ||
                gravity == GRAVITY_TOP_RIGHT) {
            // just return 0f
            return bY
        } else if(gravity == GRAVITY_MIDDLE_LEFT ||
                gravity == GRAVITY_MIDDLE_MIDDLE ||
                gravity == GRAVITY_MIDDLE_RIGHT) {
            // align to the middle
            return bY + bH / 2f - oH / 2f
        } else {
            // align to bottom
            return bY + bH - oH
        }
    }

    abstract fun findChildPosition(child: View): FloatPoint?

}