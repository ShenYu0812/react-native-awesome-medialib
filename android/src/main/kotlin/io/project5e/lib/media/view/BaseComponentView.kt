package io.project5e.lib.media.view

import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.react.uimanager.ThemedReactContext

@Suppress("ViewConstructor")
open class BaseComponentView constructor(themedReactContext: ThemedReactContext) :
  ConstraintLayout(themedReactContext) {

  private val measureAndLayout = Runnable {
    measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
    layout(left, top, right, bottom)
  }

  override fun requestLayout() {
    super.requestLayout()
    post(measureAndLayout)
  }
}
