package io.project5e.lib.media.view

import android.content.Context
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.MediaScannerConnectionClient
import android.net.Uri
import android.text.TextUtils

class MediaScannerConnection : MediaScannerConnectionClient {
  interface ScanListener {
    fun onScanFinish()
  }

  private var mMs: MediaScannerConnection
  private var mPath: String
  private var mListener: ScanListener? = null

  constructor(context: Context, path: String, l: ScanListener?) {
    mListener = l
    mPath = path
    mMs = MediaScannerConnection(context.applicationContext, this)
    mMs.connect()
  }

  constructor(context: Context, path: String) {
    mPath = path
    mMs = MediaScannerConnection(context.applicationContext, this)
    mMs.connect()
  }

  override fun onMediaScannerConnected() {
    if (!TextUtils.isEmpty(mPath)) {
      mMs.scanFile(mPath, null)
    }
  }

  override fun onScanCompleted(path: String, uri: Uri) {
    mMs.disconnect()
    if (mListener != null) {
      mListener!!.onScanFinish()
    }
  }
}
