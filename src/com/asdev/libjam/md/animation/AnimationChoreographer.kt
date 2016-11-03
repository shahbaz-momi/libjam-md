package com.asdev.libjam.md.animation

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
     * Runs the provided [Animator] by pushing it to the stack. They will be automatically removed at the end of the animation.
     */
    fun run(anim: Animator) = animatorStack.add(anim)

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
            if(!anim.hasEnded() && System.currentTimeMillis() >= anim.getStartDelay())
                return true
        return false
    }

}