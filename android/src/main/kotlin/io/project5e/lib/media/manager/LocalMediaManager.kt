package io.project5e.lib.media.manager

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import android.provider.MediaStore.Files.FileColumns._ID
import android.provider.MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
import android.provider.MediaStore.MediaColumns.BUCKET_ID
import android.provider.MediaStore.MediaColumns.DATA
import android.provider.MediaStore.MediaColumns.DATE_MODIFIED
import android.provider.MediaStore.MediaColumns.DISPLAY_NAME
import android.provider.MediaStore.MediaColumns.DURATION
import android.provider.MediaStore.MediaColumns.HEIGHT
import android.provider.MediaStore.MediaColumns.MIME_TYPE
import android.provider.MediaStore.MediaColumns.SIZE
import android.provider.MediaStore.MediaColumns.WIDTH
import android.util.Log
import io.project5e.lib.media.model.AlbumModel
import io.project5e.lib.media.model.LocalMedia
import io.project5e.lib.media.utils.BitmapUtils
import io.project5e.lib.media.utils.VersionUtils.isAboveAndroidQ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.abs

const val allPhotoBucketId = -1L
const val allVideoBucketId = -2L
const val allMediaBucketId = -3L
const val pageLimit = 30

class LocalMediaManager private constructor() {

  companion object {
    @Volatile
    private var instance: LocalMediaManager? = null

    fun getInstance() = instance ?: synchronized(this) {
      instance ?: LocalMediaManager().also { instance = it }
    }

    private lateinit var applicationContext: Application

    fun initialization(application: Application) {
      if (this::applicationContext.isInitialized) return
      applicationContext = application
    }

    private fun checkInit() {
      if (this::applicationContext.isInitialized) return
      throw IllegalArgumentException("LocalMediaManager is not init before use!")
    }

    var requestList: List<RequestBuilder> = emptyList()

    @Suppress("unused")
    private var tag = "LocalMediaManager"

    private const val external = "external"
    private const val bucketId = "bucket_id"
    private const val bucketDisplayName = "bucket_display_name"
    private const val duration = "duration"
  }

  fun applyBucket(bucketId: Long): RequestBuilder = RequestBuilder().applyBucket(bucketId)

  private fun shouldIntercept(req: RequestBuilder): Boolean {
    requestList.find { it.requestOptions == req.requestOptions }?.let {
      if (it.isRequesting) return true
      return abs(req.timestamp - it.timestamp) <= 500
    } ?: run {
      requestList = requestList + req
      return false
    }
  }

  suspend fun request(req: RequestBuilder): MutableList<LocalMedia>? {
    if (shouldIntercept(req)) {
      Log.w("PictureSelector", "Repeat request had been intercepted!")
      return null
    }
    req.isRequesting = true
    val result = getLocalMediaUri(req)
    req.isRequesting = false
    return result
  }

  private val queryUri: Uri = MediaStore.Files.getContentUri(external)

  @Suppress("InlinedApi", "DEPRECATION")
  private val projection = arrayOf(
    _ID,
    MEDIA_TYPE,
    if (isAboveAndroidQ) BUCKET_ID else bucketId,
    if (isAboveAndroidQ) BUCKET_DISPLAY_NAME else bucketDisplayName,
    DATA,
    MIME_TYPE,
    WIDTH,
    HEIGHT,
    SIZE,
    DISPLAY_NAME,
    if (isAboveAndroidQ) DURATION else duration,
    DATE_MODIFIED
  )

  @Synchronized
  private suspend fun getLocalMediaUri(
    req: RequestBuilder
  ): MutableList<LocalMedia> = withContext(Dispatchers.IO) {
    checkInit()
    val targetBucketId = req.requestOptions.bucket ?: allMediaBucketId
    val pageNum = req.requestOptions.pageLoadParams?.page
    val pageLimit = req.requestOptions.pageLoadParams?.pageLimit
    var cursor: Cursor? = null
    val localMediaList = mutableListOf<LocalMedia>()
    try {
      cursor = applicationContext.contentResolver?.query(
        queryUri,
        projection,
        getSelection(targetBucketId),
        getSelectionArgs(targetBucketId),
        getOrder(pageNum, pageLimit)
      )
      cursor?.use { it ->
        var index = -1
        val idColumn = it.getColumnIndex(projection[++index])
        val mediaTypeColumn = it.getColumnIndex(projection[++index])
        val bucketIdColumn = it.getColumnIndex(projection[++index])
        val bucketNameColumn = it.getColumnIndex(projection[++index])
        val dataColumn = it.getColumnIndex(projection[++index])
        val mineTypeColumn = it.getColumnIndex(projection[++index])
        val widthColumn = it.getColumnIndex(projection[++index])
        val heightColumn = it.getColumnIndex(projection[++index])
        val sizeColumn = it.getColumnIndex(projection[++index])
        val nameColumn = it.getColumnIndex(projection[++index])
        val durationColumn = it.getColumnIndex(projection[++index])
        val orderColumn = it.getColumnIndex(projection[++index])

        while (it.moveToNext()) {
          val absolutePath = it.getString(dataColumn)
          if (!File(absolutePath).exists()) continue
          localMediaList += LocalMedia().apply {
            _id = it.getLong(idColumn)
            mediaType = it.getInt(mediaTypeColumn)
            bucketId = it.getLong(bucketIdColumn)
            bucketName = it.getString(bucketNameColumn)
            uri = if (_id == null) null else ContentUris.withAppendedId(queryUri, _id!!)
            srcPath = if (!isAboveAndroidQ) absolutePath else "" + uri
            mineType = it.getString(mineTypeColumn)
            width = it.getInt(widthColumn)
            height = it.getInt(heightColumn)
            size = it.getLong(sizeColumn)
            name = it.getString(nameColumn)
            duration =
              if (mediaType == MEDIA_TYPE_VIDEO) it.getLong(durationColumn) else null
            dataModify = it.getInt(orderColumn)
            enable = !(duration != null && duration!! <= 5000)
          }
        }

      }
    } catch (e: Exception) {
      e.printStackTrace()
    } finally {
      cursor?.close()
    }
    localMediaList
  }

  private fun getSelection(bucketId: Long) =
    when (bucketId) {
      allPhotoBucketId -> "($MEDIA_TYPE=?) AND (${WIDTH}>100 AND ${HEIGHT}>100 AND ${SIZE}>0)"
      allVideoBucketId -> "($MEDIA_TYPE=?) AND (${WIDTH}>100 AND ${HEIGHT}>100 AND ${SIZE}>0)"
      allMediaBucketId -> "($MEDIA_TYPE=? OR $MEDIA_TYPE=?) " +
        "AND (${WIDTH}>100 AND ${HEIGHT}>100 AND ${SIZE}>0)"
      else -> "($MEDIA_TYPE=? OR $MEDIA_TYPE=?) AND $bucketId=?  " +
        "AND (${WIDTH}>100 AND ${HEIGHT}>100 AND ${SIZE}>0)"
    }

  private fun getSelectionArgs(bucketId: Long) =
    when (bucketId) {
      allPhotoBucketId -> arrayOf(MEDIA_TYPE_IMAGE.toString())
      allVideoBucketId -> arrayOf(MEDIA_TYPE_VIDEO.toString())
      allMediaBucketId -> arrayOf(MEDIA_TYPE_IMAGE.toString(), MEDIA_TYPE_VIDEO.toString())
      else -> arrayOf(
        MEDIA_TYPE_IMAGE.toString(),
        MEDIA_TYPE_VIDEO.toString(),
        bucketId.toString()
      )
    }

  private fun getOrder(pageNum: Int?, pageSize: Int?): String {
    val pageParam = if (pageNum == null || pageSize == null) ""
    else "limit $pageSize offset ${(pageNum - 1) * pageSize}"
    return "$DATE_MODIFIED desc $pageParam"
  }

  suspend fun getAllAlbum(
    allMedia: MutableList<LocalMedia>?,
    mediaType: Int? = MEDIA_TYPE_IMAGE
  ): MutableList<AlbumModel> =
    withContext(Dispatchers.IO) {
      val albumList: MutableList<AlbumModel> = mutableListOf()
      allMedia ?: return@withContext albumList
      val isVideo = mediaType == MEDIA_TYPE_VIDEO
      val allRes = allMedia.filter { it.mediaType == mediaType }
      if (allRes.isEmpty()) return@withContext albumList
      albumList += allRes
        .let {
          AlbumModel(
            0,
            if (isVideo) allVideoBucketId else allPhotoBucketId,
            "最近项目",
            it.size,
            if (isVideo) getVideoThumbnail(it[0]) else it[0].srcPath
          )
        }
      allRes.forEach { l ->
        if (albumList.any { it.albumId == l.bucketId }) {
          albumList.apply { filter { it.albumId == l.bucketId }[0].apply { this.count += 1 } }
        } else {
          val cover = if (isVideo) getVideoThumbnail(l) else l.srcPath
          albumList += AlbumModel(
            albumList.size, l.bucketId ?: -1L, l.bucketName, 1, cover
          )
        }
      }
      albumList
    }

  private fun getVideoThumbnail(item: LocalMedia): String {
    val targetSrc = if (isAboveAndroidQ) item.uri ?: item.srcPath else item.srcPath
    val retriever = MediaMetadataRetriever().apply {
      when (targetSrc) {
        is Uri -> setDataSource(applicationContext, targetSrc)
        is String -> setDataSource(targetSrc)
        else -> return ""
      }
    }
    val time = item.duration?.let { it / 2 } ?: -1
    val bitmap = retriever.getFrameAtTime(time)
    bitmap ?: return ""
    return "data:image/png;base64,${BitmapUtils.bitmapToBase64(bitmap)}"
  }

}
