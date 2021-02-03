package io.project5e.lib.media.model

import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import kotlinx.android.parcel.Parcelize


@Parcelize
data class LocalMedia(
  var _id: Long? = null,
  var mediaType: Int = -1,
  var bucketId: Long? = null,
  var bucketName: String? = null,
  var srcPath: String? = null,
  var uri: Uri? = null,
  var mineType: String? = null,
  var width: Int = 0,
  var height: Int = 0,
  var dataModify: Int = 0,
  var size: Long = 0,
  var name: String? = null,
  var duration: Long? = null,
  var order: Int? = null,
  var checked: Boolean = false,
  var enable: Boolean = true
) : Parcelable {

  override fun toString(): String {
    return "{\"_id\":$_id, " +
      "\"mediaType\":$mediaType" +
      "\"bucketId\":$bucketId, " +
      "\"bucketName\":$bucketName, " +
      "\"srcPath\":$srcPath, " +
      "\"uri\":$uri, " +
      "\"mineType\":$mineType, " +
      "\"width\":$width, " +
      "\"height\":$height, " +
      "\"dataModify\":$dataModify, " +
      "\"size\":$size, " +
      "\"name\":$name, " +
      "\"duration\":$duration, " +
      "\"order\":$order, " +
      "\"checked\":$checked, " +
      "\"enable\":$enable}"
  }

  fun toMap(): WritableMap {
    return Arguments.createMap().apply {
      putString("key", _id.toString())
      putString("type", if (mediaType == MEDIA_TYPE_IMAGE) "image" else "video")
      putInt("width", width)
      putInt("height", height)
      putString("url", srcPath)
    }
  }

  fun toMapVideoPreview(): WritableMap {
    return Arguments.createMap().apply {
      putString("key", _id.toString())
      putInt("scale", ((width * 1.0f) / (height * 1.0f)).toInt())
      putString("url", srcPath)
    }
  }

}
