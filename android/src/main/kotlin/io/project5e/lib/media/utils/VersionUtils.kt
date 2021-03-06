package io.project5e.lib.media.utils

import android.os.Build

@Suppress("All")
object VersionUtils {

  val isAboveAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

  val isAboveAndroidKitkat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
  
  val isAboveLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
}
