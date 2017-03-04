package com.asdev.libjam.md.thread

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.thread
 */

/**
 * A simple POJO that defines a message. Messages must have a $type and a $action. Data fields are optional.
 */
data class Message (val type: String, val action: String) {

    var data0: Any? = null
    var data1: Any? = null
    var data2: Any? = null
    var data3: Any? = null

    override fun toString(): String {
        return "Message[type=$type,action=$action,data0=$data0,data1=$data1,data2=$data2,data3=$data3]"
    }

}

/** Common [Message] types */
const val MESSAGE_TYPE_VIEW = "View:Msg"
const val MESSAGE_TYPE_ROOT_VIEW = "RootView:Msg"
const val MESSAGE_TYPE_ANIMATION = "Animation:Msg"

/** Common [Message] actions */
const val MESSAGE_ACTION_REPAINT = "View:Repaint"
const val MESSAGE_ACTION_REQUEST_LAYOUT = "View:Layout"
const val MESSAGE_ACTION_RESIZE = "RootView:Resize"
const val MESSAGE_ACTION_THEME_CHANGED = "RootView:ThemeChanged"
const val MESSAGE_ACTION_SET_CURSOR = "RootView:SetCursor"
const val MESSAGE_ACTION_RUN_ANIMATION = "Animation:RunAttached"
const val MESSAGE_ACTION_CANCEL_ANIMATION = "Animation:CancelAttached"
const val MESSAGE_ACTION_IS_ANIMATION_RUNNING = "Animation:IsAttachedRunning"

/** Common [Message]s */
val MESSAGE_REQUEST_LAYOUT = Message(MESSAGE_TYPE_VIEW, MESSAGE_ACTION_REQUEST_LAYOUT)
val MESSAGE_REQUEST_REPAINT = Message(MESSAGE_TYPE_VIEW, MESSAGE_ACTION_REPAINT)