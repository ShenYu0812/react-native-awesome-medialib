package io.project5e.lib.media.utils

import androidx.annotation.NonNull
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.blankj.utilcode.util.Utils

object ViewModelProviders {
  fun of(@NonNull owner: ViewModelStoreOwner): ViewModelProvider {
    return ViewModelProvider(owner, ViewModelProvider.AndroidViewModelFactory.getInstance(Utils.getApp()))
  }
}
