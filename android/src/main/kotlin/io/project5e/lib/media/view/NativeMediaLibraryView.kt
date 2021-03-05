package io.project5e.lib.media.view

import android.util.Log
import android.view.Choreographer
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.*
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ThemedReactContext
import io.project5e.lib.media.R
import io.project5e.lib.media.adapter.GalleryAdapter
import io.project5e.lib.media.manager.CustomStaggerLayoutManager
import io.project5e.lib.media.manager.MediaLibItemDecoration
import io.project5e.lib.media.model.GalleryViewModel
import io.project5e.lib.media.model.UpdateType.*
import io.project5e.lib.media.model.LocalMedia
import io.project5e.lib.media.utils.ViewModelProviders.getViewModel
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.ON_ALBUM_UPDATE
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.ON_MEDIA_ITEM_SELECT
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.ON_PUSH_CAMERA
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.ON_PUSH_PREVIEW
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.ON_SHOW_TOAST
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.SELECT_MEDIA_COUNT
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.desc
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.newAlbums
import io.project5e.lib.media.react.EventEmitter
import kotlinx.android.synthetic.main.view_media_lib.view.*
import kotlinx.coroutines.*

@Suppress("ViewConstructor", "COMPATIBILITY_WARNING", "Unused")
class NativeMediaLibraryView constructor(
  themedReactContext: ThemedReactContext,
  private val reactAppContext: ReactApplicationContext,
) : ConstraintLayout(themedReactContext), LifecycleOwner, GalleryAdapter.OnSelectChangedListener,
  GalleryAdapter.OnPreviewMediaListener, LifecycleEventListener {
  private val tag = NativeMediaLibraryView::class.java.simpleName

  private val toastNumLimit = 0
  private val toastFormatNotSupport = 1
  private val toastDurationInvalidate = 2

  private val uiScope = CoroutineScope(Dispatchers.Main)
  private var registry: LifecycleRegistry =
    LifecycleRegistry(this@NativeMediaLibraryView)

  private val layoutManager =
    CustomStaggerLayoutManager(3, StaggeredGridLayoutManager.VERTICAL).apply {
      gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
    }
  private val gapDimens = resources.getDimensionPixelSize(R.dimen.dp_6)
  private var rvAdapter: GalleryAdapter = GalleryAdapter()
  private val model: GalleryViewModel?
  private var showList: MutableList<LocalMedia> = mutableListOf()

  init {
    themedReactContext.addLifecycleEventListener(this)
    registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    model = getViewModel(themedReactContext, GalleryViewModel::class.java)
    model?.addResource()
    inflate(context, R.layout.view_media_lib, this)
    rv_gallery.adapter = rvAdapter
    rv_gallery.layoutManager = layoutManager
    rvAdapter.selectChangedListener = this@NativeMediaLibraryView
    rvAdapter.previewListener = this@NativeMediaLibraryView
    loadData()
    rv_gallery.addItemDecoration(MediaLibItemDecoration(gapDimens))
  }

  private fun loadData() {
    model ?: return
    requestLayoutAndMeasure()
    model.shouldShowList.observe(this@NativeMediaLibraryView) { list ->
      if (list == null) return@observe
      rvAdapter.haveHeader = !model.isVideo()
      showList.clear()
      showList.addAll(list)
      onItemSelected(model.getSelectedImageCount())
      when (model.currentUpdateType) {
        UPDATE_SPECIAL_PART -> rvAdapter.updateDataSpecial()
        UPDATE_PART -> rvAdapter.updateDataPartial(showList)
        else -> rvAdapter.updateData(showList)
      }
      rv_gallery.smoothScrollBy(0, 1)
      rvAdapter.id = id
      registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }
    model.changeAlbum.observe(this@NativeMediaLibraryView) {
      if (it == true) rv_gallery.scrollToPosition(0)
    }
    model.notifyGalleryUpdate.observe(this@NativeMediaLibraryView) { add ->
      if (add == null) return@observe
      uiScope.launch {
        val bundle = Arguments.createMap()
          .also { it.putArray(newAlbums, model.fetchAlbum(true, add)) }
        EventEmitter.receiveEvent(id, ON_ALBUM_UPDATE, bundle)
      }
      if (model.getSelectedImageCount() >= model.selectLimit.value ?: 9) showToast(toastNumLimit)
    }
    model.sourceAdded.observe(this@NativeMediaLibraryView) {
      if (it != false) return@observe
      registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    if (frameCallback != null) Choreographer.getInstance().removeFrameCallback(frameCallback)
    registry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  }

  override fun onChanged(position: Int, isChecked: Boolean) {
    model ?: return
    model.updateSelectItem(position, isChecked)
    if (model.shouldShowList.value?.get(position)?.enable == false) showToast(toastNumLimit)
  }

  override fun onPreview(position: Int, haveHeader: Boolean) {
    model ?: return
    if (position == -1 && haveHeader) {
      EventEmitter.receiveEvent(id, ON_PUSH_CAMERA, null)
      return
    }
    val duration = model.shouldShowList.value?.get(position)?.duration
    if (duration != null && duration < 5000) showToast(toastDurationInvalidate)
    model.previewPosition = position
    EventEmitter.receiveEvent(id, ON_PUSH_PREVIEW, null)
  }

  override fun onHostResume() {}

  override fun onHostPause() {}

  override fun onHostDestroy() {
    registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  }

  override fun getLifecycle(): Lifecycle = registry

  private fun onItemSelected(selectedCount: Int) {
    val dataMap = Arguments.createMap()
    dataMap.putInt(SELECT_MEDIA_COUNT, selectedCount)
    EventEmitter.receiveEvent(id, ON_MEDIA_ITEM_SELECT, dataMap)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    for (i in 0..childCount) {
      getChildAt(i)?.measure(widthMeasureSpec, heightMeasureSpec)
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    for (i in 0..childCount) {
      getChildAt(i)?.layout(0, 0, measuredWidth, measuredHeight)
    }
  }

  private var frameCallback: Choreographer.FrameCallback? = Choreographer.FrameCallback {
    operateViewRoot()
    operateViewTreeAllNode()
    layoutEnqueued = model?.allMediaHadGot == true
    requestLayoutAndMeasure()
  }
  private var layoutEnqueued: Boolean = false
  private fun requestLayoutAndMeasure() {
    if (layoutEnqueued || frameCallback == null) return
    layoutEnqueued = true
    Choreographer.getInstance().postFrameCallback(frameCallback)
  }

  private fun manuallyLayoutChildren() {
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      child ?: continue
      child.measure(
        MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
      )
      child.layout(0, 0, child.measuredWidth, child.measuredHeight)
    }
  }

  private fun operateViewTreeAllNode() {
    if (model?.currentUpdateType != UPDATE_ALL) return
    manuallyLayoutChildren()
    if (!reactAppContext.hasActiveCatalystInstance()) return
    viewTreeObserver.dispatchOnGlobalLayout()
  }

  private fun operateViewRoot() {
    if (model?.currentUpdateType == UPDATE_ALL) return
    measure(
      MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
    )
    layout(left, top, right, bottom)
  }

  private fun showToast(type: Int) {
    model ?: return
    val message = when (type) {
      toastNumLimit -> numberLimit()
      toastDurationInvalidate -> durationInvalidate
      else -> formatInvalidate
    }
    val map = Arguments.createMap().apply { putString(desc, message) }
    EventEmitter.receiveEvent(id, ON_SHOW_TOAST, map)
  }

  private fun numberLimit(): String = context.resources
    .getString(R.string.number_limit, model?.selectLimit?.value ?: 9)

  private val durationInvalidate = context.resources.getString(R.string.duration_invalidate)
  private val formatInvalidate = context.resources.getString(R.string.format_invalidate)

}
