package io.project5e.lib.media.react

import androidx.lifecycle.ViewModelStoreOwner
import com.facebook.react.bridge.*
import io.project5e.lib.media.model.GalleryViewModel
import io.project5e.lib.media.utils.ViewModelProviders

@Suppress("Unused", "DEPRECATION")
class MediaLibraryModule(
  reactContext: ReactApplicationContext
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
    val viewModel = getViewModel(reactApplicationContext)
    viewModel?.clearViewModel()
  }

  @ReactMethod
  fun fetchAllAssets(isVideoType: Boolean) {
    val viewModel = getViewModel(reactApplicationContext)
    viewModel?.fetchAllAssets(isVideoType)
  }

  @ReactMethod
  fun fetchAllAlbums(promise: Promise) {
    val viewModel = getViewModel(reactApplicationContext)
    viewModel ?: promise.reject("unknown error:cannot get view model!")
    viewModel?.getAllAlbum(promise)
  }

  @ReactMethod
  fun finishSelectMedia(promise: Promise) {
    val viewModel = getViewModel(reactApplicationContext)
    viewModel ?: return promise.reject("unknown error:cannot get view model!")
    val selectedItems = viewModel.getAllSelectedItem()
    val data =
      Arguments.createArray().also { selectedItems?.forEach { m -> it.pushMap(m.toMap()) } }
    return promise.resolve(data)
  }

  @ReactMethod
  fun fetchVideoURL(promise: Promise) {
    val viewModel = getViewModel(reactApplicationContext)
    viewModel ?: return promise.reject("unknown error:cannot get view model!")
    viewModel.previewVideo(promise)
  }

  @ReactMethod
  fun onSelectAlbumAtIndex(index: Int) {
    val viewModel = getViewModel(reactApplicationContext)
    viewModel ?: return
    viewModel.updateSelectedAlbum(index)
  }

  private fun getViewModel(context: ReactApplicationContext): GalleryViewModel? {
    val viewModelOwner: ViewModelStoreOwner? = context.currentActivity as? ViewModelStoreOwner
    return viewModelOwner?.let { ViewModelProviders.of(it).get(GalleryViewModel::class.java) }
  }
}
