package io.project5e.lib.media.utils

import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.blankj.utilcode.util.Utils
import com.facebook.react.bridge.ReactContext

object ViewModelProviders {

  fun of(@NonNull owner: ViewModelStoreOwner): ViewModelProvider {
    return ViewModelProvider(owner, ViewModelProvider.AndroidViewModelFactory.getInstance(Utils.getApp()))
  }

  fun <T : ViewModel> getViewModel(context: ReactContext, clazz: Class<T>): T? {
    val owner: ViewModelStoreOwner? = context.currentActivity as? ViewModelStoreOwner
    return owner?.let { of(it).get(clazz) }
  }
}
