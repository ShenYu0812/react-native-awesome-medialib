package io.project5e.lib.media.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import io.project5e.lib.media.utils.VersionUtils.isAboveAndroidKitkat
import okio.*
import java.io.*
import java.io.IOException
import java.nio.channels.FileChannel
import java.util.*

const val TAG = "ImageUtils"

fun bufferCopy(inBuffer: BufferedSource?, outputStream: OutputStream?): Boolean {
  var outBuffer: BufferedSink? = null
  try {
    outBuffer = outputStream?.sink()?.buffer()
    inBuffer?.let { outBuffer?.writeAll(it) }
    outBuffer?.flush()
    return true
  } catch (e: Exception) {
    e.printStackTrace()
  } finally {
    close(inBuffer)
    close(outBuffer)
  }
  return false
}

/**
 * Copies one file into the other with the given paths.
 * In the event that the paths are the same, trying to copy one file to the other
 * will cause both files to become null.
 * Simply skipping this step if the paths are identical.
 */
@Throws(IOException::class)
fun copyFile(pathFrom: String, pathTo: String) {
  if (pathFrom.equals(pathTo, ignoreCase = true)) {
    return
  }
  var outputChannel: FileChannel? = null
  var inputChannel: FileChannel? = null
  try {
    outputChannel = FileOutputStream(File(pathTo)).channel
    inputChannel = FileInputStream(File(pathFrom)).channel
    inputChannel.transferTo(0, inputChannel.size(), outputChannel)
    inputChannel.close()
  } finally {
    inputChannel?.close()
    outputChannel?.close()
  }
}

fun close(c: Closeable?) {
  // java.lang.IncompatibleClassChangeError: interface not implemented
  if (c != null) {
    try {
      c.close()
    } catch (e: Exception) {
      Log.e(TAG, "${e.message}")
    }
  }
}

@Suppress("NewApi", "DEPRECATION")
fun getPath(ctx: Context, uri: Uri): String? {
  val context = ctx.applicationContext

  // DocumentProvider
  if (isAboveAndroidKitkat && DocumentsContract.isDocumentUri(context, uri)) {
    if (isExternalStorageDocument(uri)) {
      val docId = DocumentsContract.getDocumentId(uri)
      val split = docId.split(":".toRegex()).toTypedArray()
      val type = split[0]
      if ("primary".equals(type, ignoreCase = true)) {
        return if (VersionUtils.isAboveAndroidQ) {
          context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            .toString() + "/" + split[1]
        } else {
          Environment.getExternalStorageDirectory().toString() + "/" + split[1]
        }
      }
    } else if (isDownloadsDocument(uri)) {
      val id = DocumentsContract.getDocumentId(uri)
      val contentUri = ContentUris.withAppendedId(
        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
      )
      return getDataColumn(context, contentUri, null, null)
    } else if (isMediaDocument(uri)) {
      val docId = DocumentsContract.getDocumentId(uri)
      val split = docId.split(":".toRegex()).toTypedArray()
      val type = split[0]
      var contentUri: Uri? = null
      when (type) {
        "image" -> {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        "video" -> {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        "audio" -> {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
      }
      val selection = "_id=?"
      val selectionArgs: Array<String?> = arrayOf(split[1] ?: null)
      return getDataColumn(context, contentUri, selection, selectionArgs)
    }
  } else if ("content".equals(uri.scheme, ignoreCase = true)) {

    // Return the remote address
    return if (isGooglePhotosUri(uri)) {
      uri.lastPathSegment
    } else getDataColumn(context, uri, null, null)
  } else if ("file".equals(uri.scheme, ignoreCase = true)) {
    return uri.path
  }
  return null
}

fun getDataColumn(
  context: Context, uri: Uri?, selection: String?,
  selectionArgs: Array<String?>?
): String? {
  var cursor: Cursor? = null
  val column = "_data"
  val projection = arrayOf(
    column
  )
  try {
    cursor = context.contentResolver.query(
      uri!!, projection, selection, selectionArgs,
      null
    )
    if (cursor != null && cursor.moveToFirst()) {
      val columnIndex = cursor.getColumnIndexOrThrow(column)
      return cursor.getString(columnIndex)
    }
  } catch (ex: IllegalArgumentException) {
    Log.i(
      TAG,
      String.format(Locale.getDefault(), "getDataColumn: _data - [%s]", ex.message)
    )
  } finally {
    cursor?.close()
  }
  return null
}

fun isExternalStorageDocument(uri: Uri): Boolean {
  return "com.android.externalstorage.documents" == uri.authority
}

fun isDownloadsDocument(uri: Uri): Boolean {
  return "com.android.providers.downloads.documents" == uri.authority
}

fun isGooglePhotosUri(uri: Uri): Boolean {
  return "com.google.android.apps.photos.content" == uri.authority
}

fun isMediaDocument(uri: Uri): Boolean {
  return "com.android.providers.media.documents" == uri.authority
}
