package io.project5e.lib.media.react

import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import io.project5e.lib.media.view.MediaLibraryPhotoPreview

class MediaLibraryPhotoPreviewManager(
  private val navigationEmitter: EventEmitter
) : SimpleViewManager<MediaLibraryPhotoPreview>() {

  companion object {
    const val MEDIA_LIB_PHOTO_PREVIEW = "MediaLibraryPhotoPreview"
    const val key = "phasedRegistrationNames"
    const val bubbled = "bubbled"
    const val desc = "desc"
    const val ON_FINISH_SELECT = "onFinishSelect"
    const val ON_SHOW_TOAST = "onShowToast"
  }

  override fun getName(): String = MEDIA_LIB_PHOTO_PREVIEW

  override fun createViewInstance(reactContext: ThemedReactContext): MediaLibraryPhotoPreview =
    MediaLibraryPhotoPreview(reactContext, navigationEmitter)

  override fun getExportedCustomBubblingEventTypeConstants(): MutableMap<String, *>? {
    return MapBuilder.builder<String, Any>()
      .put(
        ON_FINISH_SELECT,
        MapBuilder.of(
          key, MapBuilder.of(
          bubbled,
          ON_FINISH_SELECT
        )
        )
      )
      .put(
        ON_SHOW_TOAST,
        MapBuilder.of(
          key, MapBuilder.of(
          bubbled,
          ON_SHOW_TOAST
        )
        )
      )
      .build()
  }
}
