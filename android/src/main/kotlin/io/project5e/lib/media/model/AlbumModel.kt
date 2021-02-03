package io.project5e.lib.media.model

import android.os.Parcelable
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AlbumModel(
  var index: Int = 0,
  var albumId: Long? = null,
  var name: String? = null,
  var count: Int = 0,
  var cover: String? = null,
  val indexOffset: Int = 2
) : Parcelable {

  override fun toString(): String {
    return "{\"index\":$index, " +
      "\"albumId\":$albumId, " +
      "\"name\":\"$name\", " +
      "\"count\":$count, " +
      "\"cover\":\"$cover\"}"
  }

  fun toMap(): WritableMap {
    return Arguments.createMap().apply {
      putInt("index", index)
      putInt("albumId", albumId?.toInt() ?: -1)
      putString("name", name)
      putInt("count", count)
      putString("cover", cover)
    }
  }
}
