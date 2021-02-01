package com.project5e.example.pictureselector

import android.app.Application
import android.content.Context
import com.facebook.react.*
import com.facebook.soloader.SoLoader
import io.project5e.lib.media.MediaLibPackage

import java.lang.reflect.InvocationTargetException


class MainApplication : Application(), ReactApplication {
  private val mReactNativeHost: ReactNativeHost = object : ReactNativeHost(this) {
    override fun getUseDeveloperSupport(): Boolean {
      return BuildConfig.DEBUG
    }

    override fun getPackages(): List<ReactPackage> {
      val packages: MutableList<ReactPackage> = PackageList(this).packages
      packages.add(MediaLibPackage())
      return packages
    }

    override fun getJSMainModuleName(): String {
      return "index"
    }
  }

  override fun getReactNativeHost(): ReactNativeHost {
    return mReactNativeHost
  }

  override fun onCreate() {
    super.onCreate()
    SoLoader.init(this,  /* native exopackage */false)
     initializeFlipper(this, reactNativeHost.reactInstanceManager) // Remove this line if you don't want Flipper enabled
  }

  companion object {
    /**
     * Loads Flipper in React Native templates.
     *
     * @param context
     */
    private fun initializeFlipper(context: Context, reactInstanceManager: ReactInstanceManager) {
      if (BuildConfig.DEBUG) {
        try {
          /*
         We use reflection here to pick up the class that initializes Flipper,
        since Flipper library is not available in release mode
        */
          val aClass = Class.forName("com.project5e.example.pictureselector.ReactNativeFlipper")
          aClass
            .getMethod("initializeFlipper", Context::class.java, ReactInstanceManager::class.java)
            .invoke(null, context, reactInstanceManager)
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }
  }
}
