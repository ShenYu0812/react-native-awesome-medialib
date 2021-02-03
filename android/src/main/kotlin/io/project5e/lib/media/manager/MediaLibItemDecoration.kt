package io.project5e.lib.media.manager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class MediaLibItemDecoration(
  private var padding: Int,
  private val spanCount: Int = 3
) : RecyclerView.ItemDecoration() {

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    val lp = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
    if (lp.spanIndex == StaggeredGridLayoutManager.LayoutParams.INVALID_SPAN_ID) return
    val spanSpace = padding * (spanCount + 1) / spanCount
    outRect.left = padding * (lp.spanIndex + 1) - spanSpace * lp.spanIndex
    outRect.right = spanSpace * (lp.spanIndex + 1) - padding * (lp.spanIndex + 1)
    outRect.top = padding
    outRect.bottom = 0
  }
}
