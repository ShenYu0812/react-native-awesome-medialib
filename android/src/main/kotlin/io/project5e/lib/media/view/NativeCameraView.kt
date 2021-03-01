package io.project5e.lib.media.view

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.camera.core.CameraSelector.*
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.uimanager.ThemedReactContext
import io.project5e.lib.media.R
import io.project5e.lib.media.model.*
import io.project5e.lib.media.utils.ViewModelProviders.getViewModel
import io.project5e.lib.media.react.CameraModule.Companion.keySquare
import io.project5e.lib.media.react.CameraModule.Companion.keyPromise
import io.project5e.lib.media.react.CameraModule.Companion.keyUri
import kotlinx.android.synthetic.main.view_camera.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@Suppress("ViewConstructor", "RestrictedApi", "DEPRECATION")
class NativeCameraView constructor(
  private val themedReactContext: ThemedReactContext
) : BaseComponentView(themedReactContext), LifecycleOwner, LifecycleEventListener {
  companion object {
    private const val cTag = "NativeCameraView"
    const val fileFormat = "yyyy-MM-dd-HH-mm-ss-SSS"
    private val permissionArray = arrayOf(Manifest.permission.CAMERA)
  }

  private val registry: LifecycleRegistry = LifecycleRegistry(this)

  private val model: CameraViewModel?

  private var imageCapture: ImageCapture? = null

  init {
    themedReactContext.addLifecycleEventListener(this)
    registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    model = getViewModel(themedReactContext, CameraViewModel::class.java)
    View.inflate(context, R.layout.view_camera, this)
    init()
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
  }

  override fun getLifecycle(): Lifecycle = registry

  private fun init() {
    registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    model ?: return
    if (allPermissionsGranted()) model.updateCameraState(TransferCamera(STATE_START))
    model.cameraState.observe(this@NativeCameraView) {
      Log.d("find_bugs", "observe:$it")
      if (it == null) return@observe
      when (it.state) {
        STATE_START -> startCamera()
        STATE_SWITCH_CAMERA -> switchCamera()
        STATE_TAKING_PHOTO -> if (it.data is HashMap<*, *>) takePicture(it.data)
        STATE_PHOTO_DEPRECATE -> if (it.data is String) model.deleteCapturePhoto(it.data)
        STATE_PHOTO_SAVE -> {
          if (it.data !is HashMap<*, *>) return@observe
          val origin = it.data[keyUri] as Uri
          val promise = it.data[keyPromise] as Promise
          model.saveCapturePhoto(themedReactContext, origin, promise)
        }
        STATE_STOP -> model.updateCameraState(TransferCamera(STATE_IDLE))
        else -> {
          model.clearViewModel()
          registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }
      }
    }
  }

  private fun startCamera() {
    registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    Log.d("find_bugs", "startCamera:${model?.cameraState?.value}")
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
      try {
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(viewFinder.surfaceProvider)
        Log.d("find_bugs", "cameraProviderFuture.addListener before init:$imageCapture")
        imageCapture = ImageCapture.Builder().build()
        Log.d("find_bugs", "cameraProviderFuture.addListener after init:$imageCapture")
        cameraProvider.unbindAll()
        Log.d("find_bugs", "cameraProviderFuture.addListener invoke bindToLifecycle")
        cameraProvider.bindToLifecycle(this, selector, preview, imageCapture)
      } catch (e: Exception) {
        Log.e(cTag, "Use case binding failed", e)
      }
    }, ContextCompat.getMainExecutor(context))
  }

  private var selector = viewFinder.controller?.cameraSelector ?: DEFAULT_BACK_CAMERA
  private fun switchCamera() {
    selector =
      if (selector == DEFAULT_BACK_CAMERA) DEFAULT_FRONT_CAMERA else DEFAULT_BACK_CAMERA
    model?.updateCameraState(TransferCamera(STATE_START))
  }

  private fun takePicture(data: HashMap<*, *>) {
    model ?: return
    val promise = data[keyPromise] as Promise
    val isSquare = data[keySquare] as Boolean
    imageCapture ?: return promise.reject("image capture is null")
    // imageCapture?.camera ?: return promise.reject("camera is null")
    val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val timestamp = System.currentTimeMillis()
    val fileName = "${SimpleDateFormat(fileFormat, Locale.getDefault()).format(timestamp)}.jpg"
    val photoFile = File(outputDir, fileName)
    Log.d("find_bugs", "write to : $outputDir, file:$fileName")
    Log.e(
      "find_bugs",
      "not bound to valid camera:${imageCapture?.camera}, capture:${imageCapture}"
    )
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture?.setCropAspectRatio(model.getRatio(isSquare))
    val callback = ImageSavedCallback(photoFile, promise)
    imageCapture?.takePicture(outputOptions, ContextCompat.getMainExecutor(context), callback)
  }

  private fun allPermissionsGranted() = permissionArray.all {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
  }

  override fun onHostResume() {}

  override fun onHostPause() {}

  override fun onHostDestroy() {
    registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  }
}
