package io.project5e.lib.media.react

import android.net.Uri
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import io.project5e.lib.media.model.*
import io.project5e.lib.media.utils.ViewModelProviders.getViewModel

@Suppress("Unused")
class CameraModule(
  private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

  companion object {
    const val keySquare = "key_square"
    const val keyUri = "key_uri"
    const val keyPromise = "key_promise"
  }

  override fun getName() = "CameraModule"

  @ReactMethod
  fun takePhoto(isSquare: Boolean, promise: Promise) {
    val model = getViewModel(reactContext, CameraViewModel::class.java)
    val data = HashMap<String, Any>()
    data[keySquare] = isSquare
    data[keyPromise] = promise
    model?.updateCameraState(TransferCamera(STATE_TAKING_PHOTO, data))
  }

  @ReactMethod
  fun switchCamera() {
    val model = getViewModel(reactContext, CameraViewModel::class.java)
    model?.updateCameraState(TransferCamera(STATE_SWITCH_CAMERA))
  }

  @ReactMethod
  fun deletePhoto(photoPath: String) {
    val model = getViewModel(reactContext, CameraViewModel::class.java)
    model?.updateCameraState(TransferCamera(STATE_PHOTO_DEPRECATE, photoPath))
  }

  @ReactMethod
  fun stopRunning() {
    val model = getViewModel(reactContext, CameraViewModel::class.java)
    model?.updateCameraState(TransferCamera(STATE_STOP))
  }

  @ReactMethod
  fun startRunning() {
    val model = getViewModel(reactContext, CameraViewModel::class.java)
    model?.updateCameraState(TransferCamera(STATE_START))
  }

  @ReactMethod
  fun saveImage(url: String, promise: Promise) {
    val model = getViewModel(reactContext, CameraViewModel::class.java)
    val data = HashMap<String, Any>()
    data[keyUri] = Uri.parse(url)
    data[keyPromise] = promise
    model?.updateCameraState(TransferCamera(STATE_PHOTO_SAVE, data))
  }
}
