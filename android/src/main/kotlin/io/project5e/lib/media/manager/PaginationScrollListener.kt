package io.project5e.lib.media.manager

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class PaginationScrollListener(
  var layoutManager: RecyclerView.LayoutManager
) : RecyclerView.OnScrollListener() {

  var isLastPage: Boolean = false

  var isLoading: Boolean = true

  var dataLoadComplete = false

  var firstItemPosition: Int = 0

  abstract fun loadMoreItems()

  abstract fun getTotalPageCount(): Int

  override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    super.onScrolled(recyclerView, dx, dy)
    if (isLoading || isLastPage || dataLoadComplete) return
    val visibleItemCount = layoutManager.childCount
    val totalItemCount = layoutManager.itemCount
    when (layoutManager) {
      is LinearLayoutManager ->
        firstItemPosition =
          (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
      is StaggeredGridLayoutManager ->
        firstItemPosition = (layoutManager as StaggeredGridLayoutManager)
          .findFirstCompletelyVisibleItemPositions(null)?.get(0) ?: 0
      else -> {
        Log.e("error", "not support this layoutManage:$layoutManager")
      }
    }
    if (visibleItemCount + firstItemPosition >= totalItemCount && firstItemPosition >= 0) {
      loadMoreItems()
    }
  }

}
