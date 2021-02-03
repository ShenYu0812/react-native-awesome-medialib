package io.project5e.lib.media.view

import android.view.Choreographer
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ThemedReactContext
import io.project5e.lib.media.R
import io.project5e.lib.media.adapter.GalleryAdapter
import io.project5e.lib.media.manager.CustomStaggerLayoutManager
import io.project5e.lib.media.manager.MediaLibItemDecoration
import io.project5e.lib.media.manager.PaginationScrollListener
import io.project5e.lib.media.model.GalleryViewModel
import io.project5e.lib.media.model.UpdateType.*
import io.project5e.lib.media.model.LocalMedia
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.ON_MEDIA_ITEM_SELECT
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.SELECT_MEDIA_COUNT
import io.project5e.lib.media.utils.ViewModelProviders
import kotlinx.android.synthetic.main.view_media_lib.view.*
import java.lang.ref.WeakReference

@Suppress("ViewConstructor", "COMPATIBILITY_WARNING", "Unused")
class NativeMediaLibraryView constructor(
  themedReactContext: ThemedReactContext,
  private val reactApplicationContext: ReactApplicationContext
) : ConstraintLayout(themedReactContext), LifecycleOwner, GalleryAdapter.OnSelectChangedListener,
  GalleryAdapter.OnPreviewMediaListener, LifecycleEventListener {
  private val tag = NativeMediaLibraryView::class.java.simpleName

  private var registry: LifecycleRegistry =
    LifecycleRegistry(this@NativeMediaLibraryView)

  private val layoutManager =
    CustomStaggerLayoutManager(3, StaggeredGridLayoutManager.VERTICAL).apply {
      gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
    }
  private var scrollListener: CustomOnScrollListener
  private val gapDimens = resources.getDimensionPixelSize(R.dimen.dp_6)
  private var rvAdapter: GalleryAdapter = GalleryAdapter(context = themedReactContext)
  private val model: GalleryViewModel?
  private val ownerWr: WeakReference<ViewModelStoreOwner>
  private var showList: MutableList<LocalMedia> = mutableListOf()

  init {
    themedReactContext.addLifecycleEventListener(this)
    registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    ownerWr = WeakReference(themedReactContext.currentActivity as? ViewModelStoreOwner)
    model = ownerWr.get()?.let { ViewModelProviders.of(it).get(GalleryViewModel::class.java) }
    View.inflate(context, R.layout.view_media_lib, this)
    rv_gallery.adapter = rvAdapter
    rv_gallery.layoutManager = layoutManager
    rvAdapter.selectChangedListener = this@NativeMediaLibraryView
    rvAdapter.previewListener = this@NativeMediaLibraryView
    scrollListener = CustomOnScrollListener(layoutManager)
    loadData()
    rv_gallery.addItemDecoration(MediaLibItemDecoration(gapDimens))
    rv_gallery.addOnScrollListener(scrollListener)
  }

  private fun loadData() {
    model ?: return
    rvAdapter.emitter = model.emitter
    model.shouldShowList.observe(this@NativeMediaLibraryView) { list ->
      if (list == null) return@observe
      requestLayoutAndMeasure()
      rvAdapter.haveHeader = !model.isVideo()
      showList.clear()
      showList.addAll(list)
      onItemSelected(model.getSelectedCount())
      when (model.currentUpdateType) {
        UPDATE_SPECIAL_PART -> rvAdapter.updateDataSpecial()
        UPDATE_PART -> rvAdapter.updateDataPartial(showList)
        else -> rvAdapter.updateData(showList)
      }
      rv_gallery.smoothScrollBy(0, 1)
      rvAdapter.id = id
      scrollListener.isLoading = false
      registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }
    model.changeAlbum.observe(this@NativeMediaLibraryView) {
      if (it == true) rv_gallery.smoothScrollToPosition(0)
    }
    model.notifyGalleryUpdate.observe(this@NativeMediaLibraryView) {
      if (it != true) return@observe
      model.fetchResource()
      model.updateAlbum(id)
    }
    model.selectLimit.observe(this@NativeMediaLibraryView) {
      if (it == null || it <= 0) return@observe
      rvAdapter.selectLimit = it
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
    model?.updateSelectItem(position, isChecked)
  }

  override fun onPreview(position: Int) {
    model?.let { it.previewPosition = position }
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
    model?.emitter?.receiveEvent(id, ON_MEDIA_ITEM_SELECT, dataMap)
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
    if (!reactApplicationContext.hasActiveCatalystInstance()) return
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

  private inner class CustomOnScrollListener(
    layoutManager: RecyclerView.LayoutManager
  ) : PaginationScrollListener(layoutManager) {

    override fun loadMoreItems() {
      isLoading = true
      model ?: return
      isLastPage = model.allMediaHadGot
      if (isLastPage) return
      model.fetchResource(pageNum = model.preloadPageNum++)
    }

    override fun getTotalPageCount(): Int = showList.size
  }
}
