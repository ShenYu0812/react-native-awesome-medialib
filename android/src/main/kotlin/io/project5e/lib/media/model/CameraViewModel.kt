package io.project5e.lib.media.model

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_DCIM
import android.os.Environment.MEDIA_MOUNTED
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Images.Media.*
import android.util.Log
import android.util.Rational
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactContext
import io.project5e.lib.media.view.NativeCameraView.Companion.fileFormat
import io.project5e.lib.media.view.MediaScannerConnection
import io.project5e.lib.media.utils.VersionUtils
import io.project5e.lib.media.utils.BitmapUtils.Companion.getBitmapSize
import io.project5e.lib.media.utils.ViewModelProviders.getViewModel
import io.project5e.lib.media.utils.copyFile
import io.project5e.lib.media.utils.bufferCopy
import io.project5e.lib.media.utils.getPath
import io.project5e.lib.media.utils.close
import kotlinx.coroutines.*
import okio.BufferedSource
import okio.buffer
import okio.source
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraViewModel : ViewModel() {

  private val viewModelJob = SupervisorJob()
  private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  val cameraState: MutableLiveData<TransferCamera?> = MutableLiveData()

  fun updateCameraState(t: TransferCamera) = uiScope.launch {
    cameraState.value = t
  }

  fun clearViewModel() = uiScope.launch {
    cameraState.value = null
  }

  fun deleteCapturePhoto(photoPath: String) = uiScope.launch {
    withContext(Dispatchers.IO) { File(photoPath).delete() }
  }

  fun saveCapturePhoto(ctx: ReactContext, origin: Uri, promise: Promise?) = uiScope.launch {
    if (VersionUtils.isAboveAndroidQ)
      @Suppress("NewApi") savePhotoAboveAndroidQ(ctx, origin, promise)
    else savePhoto(ctx, origin, promise)
  }

  @Suppress("DEPRECATION")
  @RequiresApi(Build.VERSION_CODES.Q)
  private fun savePhotoAboveAndroidQ(ctx: ReactContext, origin: Uri, promise: Promise?) =
    uiScope.launch {
      val contentValues = ContentValues()
      val file = origin.toFile()
      val extensions = if (file.extension.isEmpty()) "*" else file.extension
      val dateToken = SimpleDateFormat(fileFormat, Locale.getDefault())
        .parse(file.nameWithoutExtension)?.time ?: System.currentTimeMillis()
      contentValues.put(DISPLAY_NAME, file.name)
      contentValues.put(DATE_TAKEN, dateToken.toInt())
      contentValues.put(MIME_TYPE, "image/$extensions")
      contentValues.put(RELATIVE_PATH, "$DIRECTORY_DCIM/camera")
      val target = ctx.contentResolver.insert(EXTERNAL_CONTENT_URI, contentValues)
      target ?: promise?.reject("uri is null")
      target ?: return@launch
      withContext(Dispatchers.IO) { write(ctx, origin, target, promise) }
    }

  @Suppress("DEPRECATION")
  private fun write(ctx: ReactContext, origin: Uri, target: Uri, promise: Promise?) {
    var buffer: BufferedSource? = null
    try {
      val originFile = origin.toFile()
      val dateToken = SimpleDateFormat(fileFormat, Locale.getDefault())
        .parse(originFile.nameWithoutExtension)?.time ?: System.currentTimeMillis()
      val extensions = if (originFile.extension.isEmpty()) "*" else originFile.extension
      buffer = ctx.contentResolver.openInputStream(origin)?.source()?.buffer()
        ?: throw NullPointerException()
      val outputStream = ctx.contentResolver.openOutputStream(target)
      val bufferCopy = bufferCopy(buffer, outputStream)
      if (!bufferCopy) promise?.reject("buffer copy false")
      if (!bufferCopy) return
      val path = getPath(ctx, target) ?: ""
      ctx.contentResolver.notifyChange(target, null)
      val model = getViewModel(ctx, GalleryViewModel::class.java)
      model?.notifyGalleryChanged(LocalMedia().apply {
        this.uri = target
        this.name = originFile.name
        this.dataModify = dateToken.toInt()
        this.mineType = "image/$extensions"
        this.srcPath = path
        this.mediaType = MEDIA_TYPE_IMAGE
        this.width = getBitmapSize(target).first
        this.height = getBitmapSize(target).second
      }) ?: run {
        promise?.reject("model is null")
        return
      }
      deleteCapturePhoto(originFile.path)
      promise?.resolve(path)
    } catch (e: Exception) {
      Log.e("error", "save image fail: ${e.message}")
      deleteCapturePhoto(origin.toString())
      promise?.reject(e.message)
    } finally {
      if (buffer != null && buffer.isOpen) close(buffer)
    }
  }

  @Suppress("DEPRECATION")
  private suspend fun savePhoto(ctx: ReactContext, origin: Uri, promise: Promise?) =
    withContext(Dispatchers.Default) {
      if (Environment.getExternalStorageState() != MEDIA_MOUNTED) {
        promise?.resolve("don't need copy")
        return@withContext
      }
      val dcim = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).path
      val targetDir = File("$dcim${File.separator}Camera")
      if (!targetDir.exists()) targetDir.mkdirs()
      val oFile = origin.toFile()
      val tFile = File(targetDir, oFile.name)
      val d = async(Dispatchers.IO) {
        copyFile(oFile.path, tFile.absolutePath)
        tFile.exists()
      }
      if (!d.await()) promise?.reject("save photo below android q is fail!")
      insertImage(ctx.contentResolver, tFile.absolutePath, tFile.name, null)
      MediaScannerConnection(ctx, tFile.absolutePath)
      val model = getViewModel(ctx, GalleryViewModel::class.java)
      val extensions = if (tFile.extension.isEmpty()) "*" else tFile.extension
      val dateToken = SimpleDateFormat(fileFormat, Locale.getDefault())
        .parse(tFile.nameWithoutExtension)?.time ?: System.currentTimeMillis()
      val tUri = Uri.fromFile(tFile)
      model?.notifyGalleryChanged(LocalMedia().apply {
        this.uri = tUri
        this.name = tFile.name
        this.mineType = "image/${extensions}"
        this.dataModify = dateToken.toInt()
        this.srcPath = tFile.absolutePath
        this.mediaType = MEDIA_TYPE_IMAGE
        this.width = getBitmapSize(tUri).first
        this.height = getBitmapSize(tUri).second
      })
      deleteCapturePhoto(tFile.absolutePath)
      promise?.resolve(tFile.absolutePath)
    }

  fun getRatio(isSquare: Boolean) =
    if (isSquare) Rational(1, 1)
    else Rational(3, 4)
}

class TransferCamera(
  @CameraState val state: Int = STATE_IDLE,
  val data: Any? = null
) {
  override fun toString(): String {
    return "TransferCamera:{\"state\": $state, " +
      "\"data\":$data}"
  }
}

const val STATE_IDLE = 0
const val STATE_START = 1
const val STATE_SWITCH_CAMERA = 2
const val STATE_TAKING_PHOTO = 3
const val STATE_PHOTO_DEPRECATE = 4
const val STATE_PHOTO_SAVE = 5
const val STATE_STOP = 6

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
@IntDef(
  STATE_START,
  STATE_SWITCH_CAMERA,
  STATE_TAKING_PHOTO,
  STATE_PHOTO_DEPRECATE,
  STATE_PHOTO_SAVE,
  STATE_STOP,
  STATE_IDLE
)
annotation class CameraState
