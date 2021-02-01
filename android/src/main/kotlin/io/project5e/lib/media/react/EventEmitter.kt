package io.project5e.lib.media.react

import com.facebook.react.bridge.WritableMap

interface EventEmitter {

  fun receiveEvent(targetTag: Int, eventName: String, event: WritableMap?)

}
