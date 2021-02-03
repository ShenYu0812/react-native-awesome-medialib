package io.project5e.lib.media.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class SimpleRequestListener(
	private val context: Context,
	private val uri: Uri?
) : RequestListener<Bitmap> {

	override fun onLoadFailed(
		e: GlideException?,
		model: Any?,
		target: Target<Bitmap>?,
		isFirstResource: Boolean
	): Boolean {
		try {
			uri ?: return false
			val pfd = context.contentResolver.openFileDescriptor(uri, "r")
			pfd ?: return false
			val bitmap = BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
			(target as? ImageView)?.setImageBitmap(bitmap) ?: return false
			return true
		} catch (e: Exception) {
			return false
		}
	}

	override fun onResourceReady(
		resource: Bitmap?,
		model: Any?,
		target: Target<Bitmap>?,
		dataSource: DataSource?,
		isFirstResource: Boolean
	): Boolean {
		return false
	}
}
