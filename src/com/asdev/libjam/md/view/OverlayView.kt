package com.asdev.libjam.md.view

import com.asdev.libjam.md.util.*
import java.lang.ref.WeakReference

/**
 * Created by Asdev on 03/01/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.view
 */

/**
 * A view which is drawn as an overlay on a view group.
 */
open class OverlayView (
        /**
         * The id of this overlay view.
         */
        id: String = "OverlayView:${generateRandomId()}",
        /**
         * The id of the view to bind upon. Remember to call requestLayout() in order to apply the changes.
         */
        var bindViewId: String? = null): View(id) {


    /**
     * The position of this overlay view.
     */
    var position = POINT_UNSET

    /**
     * A reference view that this is bound to. Stored as a [WeakReference] to avoid potential memory leaks.
     */
    private var boundViewRef = WeakReference<View>(null)

    /**
     * Called when the position of this view has been determined.
     * @param newPos the new position of this OverlayView.
     * @param boundView a reference to the boundView as specified in bindViewId
     */
    open fun onPostLayout(newPos: FloatPoint, boundView: View?) {
        position = newPos
        boundViewRef = WeakReference<View>(boundView)
    }

    /**
     * Called when this OverlayView is attached to its parent.
     */
    open fun onAttach() {
    }

    /**
     * Called when this OverlayView is detached from its parent.
     */
    open fun onDetach() {
        // clear the reference of the bound view
        boundViewRef.clear()
    }

}