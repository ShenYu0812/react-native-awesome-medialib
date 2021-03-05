package io.project5e.lib.media.react

import com.facebook.react.ReactInstanceManager
import com.facebook.react.bridge.*
import com.facebook.react.uimanager.events.RCTEventEmitter

object EventEmitter {

  var reactInstanceManager: ReactInstanceManager? = null

  fun receiveEvent(targetTag: Int, eventName: String, event: WritableMap?) {
    reactInstanceManager?.currentReactContext
      ?.getJSModule(RCTEventEmitter::class.java)
      ?.receiveEvent(targetTag, eventName, event)
  }

}
