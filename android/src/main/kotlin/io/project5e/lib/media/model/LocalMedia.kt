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
    return "\n{\n\t\"_id\":$_id,\n" +
      "\t\"mediaType\":$mediaType,\n" +
      "\t\"bucketId\":$bucketId,\n " +
      "\t\"bucketName\":$bucketName,\n" +
      "\t\"srcPath\":$srcPath,\n" +
      "\t\"uri\":$uri,\n" +
      "\t\"mineType\":$mineType,\n" +
      "\t\"width\":$width,\n" +
      "\t\"height\":$height,\n" +
      "\t\"dataModify\":$dataModify,\n" +
      "\t\"size\":$size,\n" +
      "\t\"name\":$name,\n" +
      "\t\"duration\":$duration,\n" +
      "\t\"order\":$order,\n" +
      "\t\"checked\":$checked,\n" +
      "\t\"enable\":$enable\n}"
  }

  fun toMap(): WritableMap {
    return Arguments.createMap().apply {
      putString("id", _id.toString())
      putString("type", if (mediaType == MEDIA_TYPE_IMAGE) "image" else "video")
      putInt("width", width)
      putInt("height", height)
      putDouble("scale", ((width * 1.0) / (height * 1.0)))
      putString("url", uri?.toString() ?: Uri.parse(srcPath).toString())
    }
  }

}
