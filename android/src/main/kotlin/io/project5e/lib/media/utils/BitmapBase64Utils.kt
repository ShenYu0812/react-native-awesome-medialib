package io.project5e.lib.media.utils

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream


class BitmapBase64Utils {

  companion object {

    fun bitmapToBase64(bitmap: Bitmap): String? {
      var result: String? = null
      var out: ByteArrayOutputStream? = null
      try {
        out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out)
        out.flush()
        out.close()

        val bitmapBytes = out.toByteArray()
        result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP)
      } catch (e: Exception) {
        e.printStackTrace()
      } finally {
        out?.close()
      }
      return result
    }
  }

}
