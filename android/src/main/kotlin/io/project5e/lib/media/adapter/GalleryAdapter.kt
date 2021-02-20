package io.project5e.lib.media.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.project5e.lib.media.model.LocalMedia
import io.project5e.lib.media.R
import java.text.SimpleDateFormat

@Suppress("SimpleDateFormat")
class GalleryAdapter : MediaLibBaseAdapter<GalleryAdapter.GalleryViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_gallery, parent, false)
    return GalleryViewHolder(view, deviceWidth)
  }

  override fun bindNormal(vh: GalleryViewHolder, position: Int) {
    localImage(vh, position)
    modifyState(vh, position)
    vh.cbSelect.setOnClickListener {
      val newState = !dataList[position].checked
      selectChangedListener?.onChanged(position, newState)
      if (!newState) modifyState(vh, position)
    }
    vh.container.setOnClickListener {
      previewListener?.onPreview(position, haveHeader)
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
    vh.container.setOnClickListener { previewListener?.onPreview(-1, haveHeader) }
  }

  private fun localImage(vh: GalleryViewHolder, position: Int) {
    val t = dataList[position].duration
    val timestamp = if (t != null) SimpleDateFormat("mm:ss").format(t) else null
    vh.tvDuration.visibility = if (timestamp != null) View.VISIBLE else View.GONE
    vh.tvDuration.text = timestamp

    Glide.with(vh.ivPhoto.context)
      .asBitmap()
      .skipMemoryCache(false)
      .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
      .placeholder(R.drawable.pic_gallery_placeholder)
      .listener(SimpleRequestListener(vh.ivPhoto.context, dataList[position].uri))
      .error(R.drawable.pic_gallery_placeholder)
      .load(dataList[position].srcPath)
      .into(vh.ivPhoto)
  }

  private fun modifyState(vh: GalleryViewHolder, position: Int) {
    val itemData = dataList[position]
    vh.cbSelect.visibility = if (itemData.duration != null) View.GONE else View.VISIBLE
    vh.cbSelect.setImageResource(if (itemData.checked) checkRes else uncheckRes)
    vh.vMask.visibility = if (itemData.enable) View.GONE else View.VISIBLE
    vh.tvSelectedNum.text = itemData.order?.toString()
    vh.tvSelectedNum.visibility = if (itemData.checked) View.VISIBLE else View.GONE
  }

  var selectChangedListener: OnSelectChangedListener? = null

  interface OnSelectChangedListener {
    fun onChanged(position: Int, isChecked: Boolean)
  }

  var previewListener: OnPreviewMediaListener? = null

  interface OnPreviewMediaListener {
    fun onPreview(position: Int, haveHeader: Boolean)
  }

  class GalleryViewHolder(
    itemView: View,
    private val deviceWidth: Int
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
      layoutItem()
    }

    private fun layoutItem() {
      val side = deviceWidth / 3
      container.layoutParams = ViewGroup.LayoutParams(side, side)
    }
  }

}
