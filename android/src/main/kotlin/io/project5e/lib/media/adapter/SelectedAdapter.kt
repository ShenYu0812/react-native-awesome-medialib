package io.project5e.lib.media.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.project5e.lib.media.R

class SelectedAdapter(
  private val sizeDimensions: Int,
  private val gapDimensions: Int
) : MediaLibBaseAdapter<SelectedAdapter.SelectedViewHolder>(haveHeader = false) {

  private var previewPosition: Int? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_gallery_selected, parent, false)
    return SelectedViewHolder(view)
  }

  override fun bindNormal(vh: SelectedViewHolder, position: Int) {
    val width = sizeDimensions + gapDimensions
    val height = sizeDimensions + gapDimensions * 2
    vh.container.layoutParams = ViewGroup.LayoutParams(width, height)
    vh.vMask.visibility = if (previewPosition == position) View.VISIBLE else View.GONE
    Glide.with(vh.ivPhoto.context)
      .asBitmap()
      .skipMemoryCache(false)
      .diskCacheStrategy(DiskCacheStrategy.ALL)
      .placeholder(R.drawable.pic_gallery_placeholder)
      .listener(SimpleRequestListener(vh.ivPhoto.context, dataList[position].uri))
      .error(R.drawable.pic_gallery_placeholder)
      .load(dataList[position].srcPath)
      .override(sizeDimensions, sizeDimensions)
      .into(vh.ivPhoto)
    vh.container.setOnClickListener { itemClickListener?.onClick(position) }
  }

  fun setPreviewPosition(pos: Int?) {
    previewPosition = pos
    notifyDataSetChanged()
  }

  class SelectedViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val container: ViewGroup = itemView.findViewById(R.id.photo_container)
    val ivPhoto: ImageView = itemView.findViewById(R.id.iv_item)
    val vMask: View = itemView.findViewById(R.id.v_mask)
  }

  var itemClickListener: OnItemClickListener? = null

  interface OnItemClickListener {
    fun onClick(position: Int)
  }

}
