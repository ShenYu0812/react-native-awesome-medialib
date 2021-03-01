package io.project5e.lib.media.react

import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap

interface EventEmitter {

  fun sendEvent(eventName: String, data: Any?)

  fun receiveEvent(targetTag: Int, eventName: String, event: WritableMap?)

  fun receiveTouches(eventName: String, touches: WritableArray, changedIndices: WritableArray)

  fun sendNavigationEvent(eventType: String, screenId: String?, data: Any? = null)

}
