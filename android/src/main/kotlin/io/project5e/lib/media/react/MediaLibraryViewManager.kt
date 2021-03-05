package io.project5e.lib.media.react

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import io.project5e.lib.media.model.GalleryViewModel
import io.project5e.lib.media.utils.ViewModelProviders.getViewModel
import io.project5e.lib.media.view.NativeMediaLibraryView

@Suppress("Unused", "UNUSED_PARAMETER")
class MediaLibraryViewManager(
  private val applicationContext: ReactApplicationContext,
) : SimpleViewManager<NativeMediaLibraryView>() {

  companion object {
    const val MEDIA_LIBRARY_VIEW = "MediaLibraryView"
    const val key = "phasedRegistrationNames"
    const val bubbled = "bubbled"
    const val newAlbums = "newAlbums"
    const val desc = "desc"
    const val ON_MEDIA_ITEM_SELECT = "onMediaItemSelect"
    const val ON_PUSH_CAMERA = "onPushCameraPage"
    const val ON_PUSH_PREVIEW = "onPushPreviewPage"
    const val SELECT_MEDIA_COUNT = "selectedMediaCount"
    const val ON_ALBUM_UPDATE: String = "onAlbumUpdate"
    const val ON_SHOW_TOAST = "onShowToast"
  }

  override fun getName(): String = MEDIA_LIBRARY_VIEW

  override fun createViewInstance(reactContext: ThemedReactContext): NativeMediaLibraryView =
    NativeMediaLibraryView(reactContext, applicationContext)

  @ReactProp(name = "maxSelectedMediaCount")
  fun setMaxSelectedCount(v: NativeMediaLibraryView, count: Int) {
    val model = getViewModel(applicationContext, GalleryViewModel::class.java)
    model?.updateSelectLimit(count)
  }

  override fun getExportedCustomBubblingEventTypeConstants(): MutableMap<String, *>? {
    return MapBuilder.builder<String, Any>()
      .put(
        ON_MEDIA_ITEM_SELECT,
        MapBuilder.of(key, MapBuilder.of(bubbled, ON_MEDIA_ITEM_SELECT))
      )
      .put(
        ON_PUSH_CAMERA,
        MapBuilder.of(key, MapBuilder.of(bubbled, ON_PUSH_CAMERA))
      )
      .put(
        ON_PUSH_PREVIEW,
        MapBuilder.of(key, MapBuilder.of(bubbled, ON_PUSH_PREVIEW))
      )
      .put(
        ON_ALBUM_UPDATE,
        MapBuilder.of(key, MapBuilder.of(bubbled, ON_ALBUM_UPDATE))
      )
      .put(
        ON_SHOW_TOAST,
        MapBuilder.of(key, MapBuilder.of(bubbled, ON_SHOW_TOAST))
      )
      .build()
  }
}
