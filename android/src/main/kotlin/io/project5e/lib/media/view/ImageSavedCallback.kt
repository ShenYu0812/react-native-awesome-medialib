package io.project5e.lib.media.view

import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import java.io.File

class ImageSavedCallback(
  private val photoFile: File,
  private val promise: Promise
) : ImageCapture.OnImageSavedCallback {
  companion object {
    const val tag = "ImageSavedCallback"
  }

  override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
    val savedUri = Uri.fromFile(photoFile)
    Log.d("find_bugs", "photo:$photoFile, uri=$savedUri")
    val dataMap = Arguments.createMap()
    dataMap.putString("url", savedUri.toString())
    promise.resolve(dataMap)
  }

  override fun onError(e: ImageCaptureException) {
    promise.reject(e)
    Log.e(tag, "${e.message}")
  }
}
