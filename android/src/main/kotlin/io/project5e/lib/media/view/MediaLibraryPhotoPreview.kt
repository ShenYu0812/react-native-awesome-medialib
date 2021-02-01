package io.project5e.lib.media.view

import android.view.Choreographer
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ThemedReactContext
import io.project5e.lib.media.R
import io.project5e.lib.media.model.GalleryViewModel
import io.project5e.lib.media.adapter.PhotoPreviewPagerAdapter
import io.project5e.lib.media.adapter.SelectedAdapter
import io.project5e.lib.media.react.MediaLibraryPhotoPreviewManager.Companion.ON_FINISH_SELECT
import io.project5e.lib.media.react.MediaLibraryPhotoPreviewManager.Companion.ON_SHOW_TOAST
import io.project5e.lib.media.react.MediaLibraryPhotoPreviewManager.Companion.desc
import io.project5e.lib.media.utils.ViewModelProviders
import kotlinx.android.synthetic.main.view_media_preview.view.*
import java.lang.ref.WeakReference

@Suppress("ViewConstructor", "COMPATIBILITY_WARNING")
class MediaLibraryPhotoPreview constructor(
  themedReactContext: ThemedReactContext,
  private val reactApplicationContext: ReactApplicationContext
) : ConstraintLayout(themedReactContext), LifecycleOwner, ViewPager.OnPageChangeListener {

  private var registry: LifecycleRegistry =
    LifecycleRegistry(this@MediaLibraryPhotoPreview)

  private val model: GalleryViewModel?
  private var previewAdapter: PhotoPreviewPagerAdapter = PhotoPreviewPagerAdapter(context)
  private val layoutManager = LinearLayoutManager(
    context, LinearLayoutManager.HORIZONTAL, false
  )
  private val sizeDimens = resources.getDimensionPixelSize(R.dimen.dp_64)
  private val gapDimens = resources.getDimensionPixelSize(R.dimen.dp_16)
  private var rvAdapter: SelectedAdapter = SelectedAdapter(sizeDimens, gapDimens)
  private var currentPosition: Int = 0

  init {
    setupLayoutHack()
    registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    val ownerWr = WeakReference(themedReactContext.currentActivity as? ViewModelStoreOwner)
    model = ownerWr.get()?.let { ViewModelProviders.of(it).get(GalleryViewModel::class.java) }
    View.inflate(context, R.layout.view_media_preview, this)
    vp_preview.adapter = previewAdapter
    vp_preview.addOnPageChangeListener(this@MediaLibraryPhotoPreview)

    rv_selected.layoutManager = layoutManager
    rv_selected.adapter = rvAdapter
    iv_select.setOnClickListener { onChanged() }
    tv_next_step.setOnClickListener { onNextStep() }

    model?.let { m ->
      previewAdapter.updateData(m.shouldShowList.value)
      m.onClickPotion.observe(this@MediaLibraryPhotoPreview) {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        currentPosition = if (m.shouldShowList.value?.size == null
          || it == null || it < 0 || it > m.shouldShowList.value?.size!!
        ) 0 else {
          highlightSelectedItem(it)
          markSelectedState(it)
          it
        }
        vp_preview.currentItem = currentPosition
      }

      m.selectedList.observe(this@MediaLibraryPhotoPreview) { list ->
        list?.let {
          tv_selected_num.text = context.getString(R.string.select_num, it.size)
          markSelectedState(currentPosition)
          rvAdapter.updateData(it)
        }
      }
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    if (frameCallback != null) Choreographer.getInstance().removeFrameCallback(frameCallback)
    registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  }

  override fun getLifecycle(): Lifecycle = registry

  private var frameCallback: Choreographer.FrameCallback? = null
  private fun setupLayoutHack() {
    frameCallback = object : Choreographer.FrameCallback {
      override fun doFrame(frameTimeNanos: Long) {
        manuallyLayoutChildren()
        if (!reactApplicationContext.hasActiveCatalystInstance()) return
        viewTreeObserver.dispatchOnGlobalLayout()
        Choreographer.getInstance().postFrameCallback(frameCallback)
      }
    }
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

  override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
  }

  override fun onPageSelected(position: Int) {
    currentPosition = position
    highlightSelectedItem(position)
    markSelectedState(position)
  }

  private fun highlightSelectedItem(position: Int) {
    val index = findTargetIndex(position)
    rvAdapter.setPreviewPosition(index)
    index?.let { rv_selected.smoothScrollToPosition(index) }

  }

  private fun onChanged() {
    model ?: return
    val index = findTargetIndex(currentPosition)
    val isChecked = index == null
    model.updateSelectItem(currentPosition, isChecked, true)
    val item = model.shouldShowList.value?.get(currentPosition)
    item?.let { if (!it.enable) showToast() }
  }

  private fun showToast() {
    val map = Arguments.createMap().apply { putString(desc, numberLimit()) }
    model?.emitter?.receiveEvent(id, ON_SHOW_TOAST, map)
  }

  private fun numberLimit(): String =
    context.resources.getString(R.string.number_limit, model?.getSelectLimit())

  private fun onNextStep() {
    model?.emitter?.receiveEvent(id, ON_FINISH_SELECT, null)
  }

  private fun markSelectedState(position: Int) {
    val index = findTargetIndex(position)
    index?.let {
      val item = model?.selectedList?.value?.get(index)
      iv_select.setImageResource(R.drawable.ic_state_checked)
      tv_selected_order.visibility = View.VISIBLE
      tv_selected_order.text = "${item?.order}"
    } ?: run {
      iv_select.setImageResource(R.drawable.ic_state_can_check)
      tv_selected_order.visibility = View.GONE
    }
  }

  private fun findTargetIndex(position: Int): Int? {
    return model?.selectedList?.value?.let { list ->
      val find = list.find { it._id == model.shouldShowList.value?.get(position)?._id }
      val index = list.indexOf(find)
      if (index < 0 || index >= list.size) null else index
    }
  }

  override fun onPageScrollStateChanged(state: Int) {
  }

}
