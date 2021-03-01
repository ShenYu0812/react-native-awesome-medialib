package com.project5e.example.pictureselector

import com.facebook.react.bridge.*
import com.project5e.react.navigation.NavigationEmitter
import io.project5e.lib.media.react.EventEmitter

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
object CustomNavigationEmitter: EventEmitter {

  override fun sendEvent(eventName: String, data: Any?) {
    NavigationEmitter.sendEvent(eventName, data)
  }

  override fun receiveEvent(targetTag: Int, eventName: String, event: WritableMap?) {
    NavigationEmitter.receiveEvent(targetTag, eventName, event)
  }

  override fun receiveTouches(eventName: String, touches: WritableArray, changedIndices: WritableArray) {
    NavigationEmitter.receiveTouches(eventName, touches, changedIndices)
  }

  override fun sendNavigationEvent(eventType: String, screenId: String?, data: Any?) {
    NavigationEmitter.sendNavigationEvent(eventType, screenId, data)
  }

}
