package com.asdev.libjam.md.base

import java.awt.Frame
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import javax.swing.JFrame

/**
 * Created by Asdev on 10/28/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.base
 */

/**
 * Manages the state of the attached windows.
 */
class WindowStateManager(val frame: JFrame) {

    /**
     * The current state of the frame.
     */
    private var state = State.STATE_NORMAL

    /**
     * Calls this function when the state is changed
     */
    var windowStateListener: ((State, State) -> Unit)? = null

    /**
     * Called when the frame is scheduled to exit.
     */
    var disposeListener: ((Unit) -> Unit)? = null

    /**
     * Maximizes the window.
     */
    fun maximize() {
        setState(State.STATE_MAXIMIZED)
    }

    /**
     * Minimizes the window.
     */
    fun minimize() {
        // retain the state and just minify the frame
        frame.state = Frame.ICONIFIED
    }

    /**
     * Exits the application.
     */
    fun exit() {
        frame.dispose()
        disposeListener?.invoke(Unit)
        System.exit(0)
    }

    /**
     * Sets the state of the window.
     */
    fun setState(newState: State) {
        val prevBounds = frame.bounds

        if(newState == State.STATE_NORMAL) {
            // if the previous state was maximized and the new state is minimize, then set the location and size to the previous one.
            if(state == State.STATE_MAXIMIZED) {
                frame.bounds = state.bounds
            }
        } else if(newState == State.STATE_MAXIMIZED) {
            // check if it is already maximized
            if(state == newState) {
                setState(State.STATE_NORMAL)
                return
            }

            // find the maximum size and set it to that
            val bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
            frame.bounds = bounds
        }

        windowStateListener?.invoke(state, newState)

        state = newState
        state.bounds = prevBounds
    }

    /**
     * Returns the current state of the window.
     */
    fun getState() = state

    /**
     * State enumerations that can be used to set the state.
     */
    enum class State {

        /**
         * The normal state of a window.
         */
        STATE_NORMAL,

        /**
         * The state of a window where the window is maximized (fullscreen).
         */
        STATE_MAXIMIZED;

        /**
         * The bounds of the frame on the previous state.
         */
        var bounds = Rectangle()
    }

}