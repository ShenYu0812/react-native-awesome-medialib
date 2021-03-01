package io.project5e.lib.media.react

import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import io.project5e.lib.media.view.NativeCameraView

class CameraViewManager : SimpleViewManager<NativeCameraView>() {
  companion object {
    const val CAMERA_VIEW = "CameraView"
  }

  override fun createViewInstance(reactContext: ThemedReactContext) =
    NativeCameraView(reactContext)

  override fun getName(): String = CAMERA_VIEW
}
