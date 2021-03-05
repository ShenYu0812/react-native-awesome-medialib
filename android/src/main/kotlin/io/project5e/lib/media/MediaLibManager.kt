package io.project5e.lib.media

import android.app.Application
import com.facebook.react.ReactInstanceManager
import io.project5e.lib.media.manager.LocalMediaManager
import io.project5e.lib.media.react.EventEmitter

object MediaLibManager {

  fun install(application: Application, reactInstanceManager: ReactInstanceManager) {
    LocalMediaManager.initialization(application)
    EventEmitter.reactInstanceManager = reactInstanceManager
  }
}
