package io.project5e.lib.media.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.facebook.react.bridge.Arguments
import io.project5e.lib.media.model.LocalMedia
import io.project5e.lib.media.R
import io.project5e.lib.media.react.EventEmitter
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.ON_PUSH_CAMERA
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.ON_PUSH_PREVIEW
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.ON_SHOW_TOAST
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.desc
import java.text.SimpleDateFormat

private const val toastNumLimit = 0

@Suppress("unused")
private const val toastFormatNotSupport = 1
private const val toastDurationInvalidate = 2

@Suppress("SimpleDateFormat")
class GalleryAdapter(
  private val context: Context,
  private val gapDimension: Int = 0
) : MediaLibBaseAdapter<GalleryAdapter.GalleryViewHolder>() {
  var selectLimit = 9
  private val durationInvalidate = context.resources.getString(R.string.duration_invalidate)
  private val formatInvalidate = context.resources.getString(R.string.format_invalidate)

  var emitter: EventEmitter? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_gallery, parent, false)
    return GalleryViewHolder(view)
  }

  override fun bindNormal(vh: GalleryViewHolder, position: Int) {
    localImage(vh, position)
    modifyState(vh, position)
    vh.cbSelect.setOnClickListener {
      val newState = !dataList[position].checked
      selectChangedListener?.onChanged(position, newState)
      if (!newState) modifyState(vh, position)
      if (!dataList[position].enable) showToast(toastNumLimit)
    }
    vh.container.setOnClickListener {
      dataList[position].duration?.let {
        if (it >= 5000) return@let
        showToast(toastDurationInvalidate)
        return@setOnClickListener
      }
      previewListener?.onPreview(position)
      emitter?.receiveEvent(id, ON_PUSH_PREVIEW, null)
    }
  }

  override fun bindNormalPl(vh: GalleryViewHolder, position: Int, pl: MutableList<Any>) {
    val itemData = pl[0] as? LocalMedia
    itemData ?: return
    modifyState(vh, position)
  }

  override fun bindHeader(vh: GalleryViewHolder) {
    vh.ivCamera.visibility = if (haveHeader) View.VISIBLE else View.GONE
    vh.ivPhoto.visibility = if (!haveHeader) View.VISIBLE else View.GONE
    vh.cbSelect.visibility = View.GONE
    vh.container.setOnClickListener { emitter?.receiveEvent(id, ON_PUSH_CAMERA, null) }
    layoutItem(vh)
  }

  private fun localImage(vh: GalleryViewHolder, position: Int) {
    layoutItem(vh)
    val t = dataList[position].duration
    val timestamp = if (t != null) SimpleDateFormat("mm:ss").format(t) else null
    vh.tvDuration.visibility = if (timestamp != null) View.VISIBLE else View.GONE
    vh.tvDuration.text = timestamp

    val overrideSize = (deviceWidth - gapDimension * 4) / 3
    Glide.with(vh.ivPhoto.context)
      .asBitmap()
      .skipMemoryCache(false)
      .diskCacheStrategy(DiskCacheStrategy.ALL)
      .placeholder(R.drawable.picture_media_item_placeholder)
      .error(R.drawable.picture_media_item_placeholder)
      .load(dataList[position].srcPath)
      .override(overrideSize, overrideSize)
      .into(vh.ivPhoto)
  }

  private fun layoutItem(vh: GalleryViewHolder) {
    val side = deviceWidth / 3
    vh.container.layoutParams = ViewGroup.LayoutParams(side, side)
  }

  private fun modifyState(vh: GalleryViewHolder, position: Int) {
    val itemData = dataList[position]
    vh.cbSelect.visibility = if (itemData.duration != null) View.GONE else View.VISIBLE
    vh.cbSelect.setImageResource(if (itemData.checked) checkRes else uncheckRes)
    vh.vMask.visibility = if (itemData.enable) View.GONE else View.VISIBLE
    vh.tvSelectedNum.text = itemData.order?.toString()
    vh.tvSelectedNum.visibility = if (itemData.checked) View.VISIBLE else View.GONE
  }

  private fun showToast(type: Int) {
    val message = when (type) {
      toastNumLimit -> numberLimit()
      toastDurationInvalidate -> durationInvalidate
      else -> formatInvalidate
    }
    val map = Arguments.createMap().apply { putString(desc, message) }
    emitter?.receiveEvent(id, ON_SHOW_TOAST, map)
  }

  private fun numberLimit(): String =
    context.resources.getString(R.string.number_limit, selectLimit)

  var selectChangedListener: OnSelectChangedListener? = null

  interface OnSelectChangedListener {
    fun onChanged(position: Int, isChecked: Boolean)
  }

  var previewListener: OnPreviewMediaListener? = null

  interface OnPreviewMediaListener {
    fun onPreview(position: Int)
  }

  class GalleryViewHolder(
    itemView: View
  ) : BaseViewHolder(itemView) {

    val container: ViewGroup = itemView.findViewById(R.id.photo_container)
    val ivCamera: ImageView = itemView.findViewById(R.id.iv_camera)
    val ivPhoto: ImageView = itemView.findViewById(R.id.iv_item)
    val cbSelect: ImageButton = itemView.findViewById(R.id.iv_select)
    val tvSelectedNum: TextView = itemView.findViewById(R.id.tv_selected_num)
    val tvDuration: TextView = itemView.findViewById(R.id.tv_duration_video_only)
    val vMask: View = itemView.findViewById(R.id.v_mask)

    init {
      ivCamera.visibility = View.GONE
    }
  }

}
