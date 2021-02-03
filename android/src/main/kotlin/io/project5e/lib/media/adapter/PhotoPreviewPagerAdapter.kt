package io.project5e.lib.media.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.project5e.lib.media.model.LocalMedia
import io.project5e.lib.media.R

class PhotoPreviewPagerAdapter(
  private val context: Context,
  private val localMediaList: MutableList<LocalMedia?> = mutableListOf()
) : PagerAdapter() {

  override fun getCount(): Int = localMediaList.size

  override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    val deviceWidth = ScreenUtils.getScreenWidth()
    val deviceHeight = ScreenUtils.getScreenHeight()
    val itemData = localMediaList[position]
    val imageWidth = itemData?.width ?: 0
    val imageHeight = itemData?.height ?: 0
    val targetHeight: Int =
      if (imageHeight != 0 && imageWidth != 0)
        (1.0f * imageHeight / imageWidth * deviceWidth).toInt()
      else deviceHeight
    val view = LayoutInflater.from(context)
      .inflate(R.layout.item_photo_preview, container, false)
    view.layoutParams = view.layoutParams.apply {
      width = deviceWidth
      height = targetHeight
    }
    val ivPreview: ImageView = view.findViewById(R.id.iv_item_preview)
    ivPreview.layoutParams = ivPreview.layoutParams.apply {
      width = deviceWidth
      height = targetHeight
    }
    itemData?.let {
      Glide.with(context)
        .asBitmap()
        .skipMemoryCache(false)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.pic_preview_placeholder)
        .listener(SimpleRequestListener(context, itemData.uri))
        .error(R.drawable.pic_preview_placeholder)
        .override(deviceWidth, targetHeight)
        .load(itemData.srcPath)
        .into(ivPreview)
    }
    container.addView(view)
    return view
  }

  override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
    container.removeView(obj as View)
  }

  fun updateData(list: List<LocalMedia>?) {
    localMediaList.clear()
    if (list != null) localMediaList.addAll(list)
    notifyDataSetChanged()
  }
}
