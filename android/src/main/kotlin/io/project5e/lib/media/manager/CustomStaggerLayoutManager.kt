package io.project5e.lib.media.manager

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class CustomStaggerLayoutManager constructor(spanCount: Int, orientation: Int) :
  StaggeredGridLayoutManager(spanCount, orientation) {
  companion object {
    const val TAG = "StaggerLayoutManager"
  }

  override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
    try {
      super.onLayoutChildren(recycler, state)
    } catch (e: Exception) {
      Log.e(TAG, "$e.message")
    }
  }
}
