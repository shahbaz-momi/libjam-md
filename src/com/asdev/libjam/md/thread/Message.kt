package com.asdev.libjam.md.thread

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.thread
 *
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
val MESSAGE_TYPE_VIEW = "View:Msg"
val MESSAGE_TYPE_ROOT_VIEW = "RootView:Msg"
/** Common [Message] actions */
val MESSAGE_ACTION_REPAINT = "View:Repaint"
val MESSAGE_ACTION_REQUEST_LAYOUT = "View:Layout"
val MESSAGE_ACTION_RESIZE = "RootView:Resize"
val MESSAGE_ACTION_THEME_CHANGED = "RootView:ThemeChanged"
val MESSAGE_ACTION_SET_CURSOR = "RootView:SetCursor"

/** Common [Message]s */
val MESSAGE_REQUEST_LAYOUT = Message(MESSAGE_TYPE_VIEW, MESSAGE_ACTION_REQUEST_LAYOUT)
val MESSAGE_REQUEST_REPAINT = Message(MESSAGE_TYPE_VIEW, MESSAGE_ACTION_REPAINT)