package io.project5e.lib.media

import android.view.View
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ReactShadowNode
import com.facebook.react.uimanager.ViewManager
import io.project5e.lib.media.react.*

class MediaLibPackage(
  private val navigationEmitter: EventEmitter
) : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): MutableList<NativeModule> {
    return mutableListOf<NativeModule>().also {
      it.add(CameraModule(reactContext))
      it.add(MediaLibraryModule(reactContext))
    }
  }

  override fun createViewManagers(reactContext: ReactApplicationContext) =
    mutableListOf<ViewManager<out View, out ReactShadowNode<*>>>().also {
      it.add(CameraViewManager())
      it.add(MediaLibraryViewManager(reactContext, navigationEmitter))
      it.add(MediaLibraryPhotoPreviewManager(navigationEmitter))
    }
}
