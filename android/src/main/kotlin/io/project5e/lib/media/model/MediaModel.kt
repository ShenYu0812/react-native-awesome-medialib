package io.project5e.lib.media.model

import androidx.annotation.StringDef
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap

data class MediaModel(
  val key: String,
  @MediaType val type: String,
  val url: String,
  val width: Int,
  val height: Int,
  val objectKey: String? = null,
) {

  companion object {

    fun cast(map: ReadableMap): MediaModel {
      val key = map.optString("key") ?: ""
      val type = map.optString("type") ?: ""
      val width = map.optInt("width")
      val height = map.optInt("height")
      val url = map.optString("url") ?: ""
      val objectKey = map.optString("objectKey") ?: ""
      return MediaModel(key, type, url, width, height, objectKey)
    }

    fun toMap(model: MediaModel): WritableMap =
      Arguments.createMap().apply {
        putString("key", model.key)
        putString("type", model.type)
        putString("url", model.url)
        putInt("width", model.width)
        putInt("height", model.height)
      }
  }

}

const val TYPE_VIDEO = "video"
const val TYPE_IMAGE = "image"

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
@StringDef(TYPE_IMAGE, TYPE_VIDEO)
annotation class MediaType

fun ReadableMap.optInt(key: String): Int =
  if (!isNull(key) && hasKey(key)) getInt(key) else 0

fun ReadableMap.optString(key: String): String? =
  if (!isNull(key) && hasKey(key)) getString(key) else null
