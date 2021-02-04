package io.project5e.lib.media.utils

import com.facebook.react.ReactNativeHost
import com.facebook.react.bridge.*
import com.facebook.react.uimanager.events.RCTEventEmitter

/**
 * @param data Types:
 *
 * Boolean -> Bool
 * Integer -> Number
 * Double -> Number
 * Float -> Number
 * String -> String
 * Callback -> function
 * ReadableMap -> Object
 * ReadableArray -> Array
 */
object NavigationEmitter {
  private var reactNativeHost: ReactNativeHost? = null

  fun initReactNativeHost(host: ReactNativeHost) {
    reactNativeHost = host
  }

  fun receiveEvent(targetTag: Int, eventName: String, event: WritableMap?) {
    reactNativeHost?.reactInstanceManager?.currentReactContext
      ?.getJSModule(RCTEventEmitter::class.java)
      ?.receiveEvent(targetTag, eventName, event)
  }
}
