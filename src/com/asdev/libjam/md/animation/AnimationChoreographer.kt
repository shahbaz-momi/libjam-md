package com.asdev.libjam.md.animation

import com.asdev.libjam.md.thread.MESSAGE_ACTION_CANCEL_ANIMATION
import com.asdev.libjam.md.thread.MESSAGE_ACTION_RUN_ANIMATION
import com.asdev.libjam.md.thread.MESSAGE_TYPE_ANIMATION
import com.asdev.libjam.md.thread.Message
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by Asdev on 11/02/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.animation
 */

/**
 * A class that manages and runs animations and animators. There should be one attached to each RootView. This should be
 * run on the UILooper to avoid any concurrency issues.
 */
class AnimationChoreographer {

    /**
     * The stack of animators.
     */
    private val animatorStack = ConcurrentLinkedQueue<Animator>()

    /**
     * Cancels any animation with the specified id.
     */
    fun cancel(id: String) {
        // remove if the ids match and call cancel on the animator
        animatorStack.removeIf {
            if(it.id == id) {
                // run a cancel on the animator
                it.cancel()
                return@removeIf true
            } else {
                return@removeIf false
            }
        }
    }

    /**
     * Runs the provided [Animator] by pushing it to the stack. They will be automatically removed at the end of the animation.
     * If an animation with the same id is already running, this animation will not be run.
     */
    fun run(anim: Animator) {
        if(!animatorStack.contains(anim))
            animatorStack.add(anim)
    }

    /**
     * Loops all of the animators within the stack.
     */
    fun loop() {
        for(anim in animatorStack) {
            anim.loop()

            if(anim.hasEnded()) {
                animatorStack.remove()
                continue
            }
        }
    }

    fun requestFrame(): Boolean {
        for(anim in animatorStack)
            if(anim.isRunning())
                return true
        return false
    }

    fun handleMessage(msg: Message) {
        if(msg.type != MESSAGE_TYPE_ANIMATION)
            return

        if(msg.action == MESSAGE_ACTION_RUN_ANIMATION && msg.data0 is Animator) {
            run(msg.data0 as Animator)
        } else if(msg.action == MESSAGE_ACTION_CANCEL_ANIMATION && msg.data0 is String) {
            // remove the animation if the ids are the same
            cancel(msg.data0 as String)
        }
    }

    /**
     * Returns whether or not an Animator with the given id is running or not.
     */
    fun isAnimationRunning(animationId: String) = animatorStack.find { it.id == animationId } != null

}