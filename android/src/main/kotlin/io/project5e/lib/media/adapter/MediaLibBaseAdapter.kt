package io.project5e.lib.media.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import io.project5e.lib.media.model.LocalMedia
import io.project5e.lib.media.R
import io.project5e.lib.media.adapter.ItemType.ITEM_NORMAL
import io.project5e.lib.media.adapter.ItemType.ITEM_HEADER

abstract class MediaLibBaseAdapter<VH : MediaLibBaseAdapter.BaseViewHolder>(
  var id: Int = -1,
  var haveHeader: Boolean = false,
  protected val dataList: MutableList<LocalMedia> = mutableListOf()
) : RecyclerView.Adapter<VH>() {

  val deviceWidth = ScreenUtils.getScreenWidth()
  val checkRes: Int = R.drawable.ic_state_checked
  val uncheckRes: Int = R.drawable.ic_state_uncheck

  override fun onBindViewHolder(vh: VH, position: Int, payloads: MutableList<Any>) {
    when (payloads.isEmpty()) {
      true -> onBindViewHolder(vh, position)
      else -> onBindViewHolderPayload(vh, position, payloads)
    }
  }

  override fun onBindViewHolder(vh: VH, position: Int) {
    when (getItemViewType(position)) {
      ITEM_NORMAL.type -> bindNormal(vh, position - if (haveHeader) 1 else 0)
      ITEM_HEADER.type -> bindHeader(vh)
    }
  }

  private fun onBindViewHolderPayload(vh: VH, position: Int, pl: MutableList<Any>) {
    when (getItemViewType(position)) {
      ITEM_NORMAL.type -> bindNormalPl(vh, position - if (haveHeader) 1 else 0, pl)
      ITEM_HEADER.type -> onBindViewHolder(vh, position)
    }
  }

  open fun bindHeader(vh: VH) {}

  abstract fun bindNormal(vh: VH, position: Int)

  open fun bindNormalPl(vh: VH, position: Int, pl: MutableList<Any>) {}

  override fun getItemId(position: Int): Long = (position - if (haveHeader) 1 else 0).toLong()

  override fun getItemCount(): Int = dataList.size + if (haveHeader) 1 else 0

  override fun getItemViewType(position: Int): Int =
    if (position == 0 && haveHeader) ITEM_HEADER.type else ITEM_NORMAL.type

  fun updateData(list: List<LocalMedia>) {
    dataList.clear()
    dataList.addAll(list)
    notifyDataSetChanged()
  }

  fun updateDataSpecial() {
    dataList.filter { it.order != null }.forEach {
      val index = dataList.indexOf(it)
      notifyItemChanged(index + 1, it)
    }
  }

  fun updateDataPartial(newList: List<LocalMedia>) {
    val beforeLastPosition = itemCount
    dataList.clear()
    dataList.addAll(newList)
    val insertCount = itemCount - beforeLastPosition
    notifyItemRangeInserted(beforeLastPosition, insertCount)
  }

  open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

enum class ItemType(val type: Int) {
  ITEM_HEADER(0), ITEM_NORMAL(1)
}
