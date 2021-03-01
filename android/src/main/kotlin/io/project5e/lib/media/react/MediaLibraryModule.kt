package io.project5e.lib.media.react

import com.facebook.react.bridge.*
import io.project5e.lib.media.model.GalleryViewModel
import io.project5e.lib.media.utils.ViewModelProviders.getViewModel

@Suppress("Unused", "DEPRECATION")
class MediaLibraryModule(
  private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String = "MediaLibraryModule"

  // iOS only
  @ReactMethod
  fun libraryAuthorized() {
  }

  // iOS only
  @ReactMethod
  fun requestLibraryAuthorization() {
  }

  // iOS only
  @ReactMethod
  fun cameraAuthorized() {
  }

  // iOS only
  @ReactMethod
  fun requestCameraAuthorization() {
  }

  @ReactMethod
  fun startCameraPreview() {
  }

  @ReactMethod
  fun stopCameraPreview() {
  }

  @ReactMethod
  fun clear() {
    val model = getViewModel(reactContext, GalleryViewModel::class.java)
    model?.clearViewModel()
  }

  @ReactMethod
  fun fetchAllAssets(isVideoType: Boolean) {
    val model = getViewModel(reactContext, GalleryViewModel::class.java)
    model?.fetchAllAssets(isVideoType)
  }

  @ReactMethod
  fun fetchAllAlbums(promise: Promise) {
    val model = getViewModel(reactContext, GalleryViewModel::class.java)
    model ?: promise.reject("unknown error:cannot get view model!")
    model?.getAllAlbum(promise)
  }

  @ReactMethod
  fun finishSelectMedia(promise: Promise) {
    val model = getViewModel(reactContext, GalleryViewModel::class.java)
    model ?: return promise.reject("unknown error:cannot get view model!")
    val selectedItems = model.getAllSelectedItem()
    val data =
      Arguments.createArray().also { selectedItems?.forEach { m -> it.pushMap(m.toMap()) } }
    return promise.resolve(data)
  }

  @ReactMethod
  fun fetchVideoURL(promise: Promise) {
    val model = getViewModel(reactContext, GalleryViewModel::class.java)
    model ?: return promise.reject("unknown error:cannot get view model!")
    model.previewVideo(promise)
  }

  @ReactMethod
  fun onSelectAlbumAtIndex(index: Int) {
    val model = getViewModel(reactContext, GalleryViewModel::class.java)
    model?.updateSelectedAlbum(index)
  }

}
