package io.project5e.lib.media.model

import androidx.lifecycle.*
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableArray
import io.project5e.lib.media.manager.*
import io.project5e.lib.media.model.UpdateType.*
import io.project5e.lib.media.react.EventEmitter
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.ON_ALBUM_UPDATE
import io.project5e.lib.media.react.MediaLibraryViewManager.Companion.newAlbums
import io.project5e.lib.media.utils.NavigationEmitter.receiveEvent
import kotlinx.coroutines.*

private const val photoSelectLimit = 9
private const val videoSelectLimit = 1

class GalleryViewModel : ViewModel() {

  private val viewModelJob = SupervisorJob()
  private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  private var mediaManager: LocalMediaManager = LocalMediaManager.getInstance()
  private val allMediaList: MutableLiveData<MutableList<LocalMedia>> = MutableLiveData(
    mutableListOf()
  )
  private val currentBucketId: MutableLiveData<Long> = MutableLiveData()
  var selectLimit: MutableLiveData<Int> = MutableLiveData()

  var notifyGalleryUpdate: MutableLiveData<Boolean> = MutableLiveData(false)
  val shouldShowList: MediatorLiveData<List<LocalMedia>> = MediatorLiveData()
  val selectedList = Transformations.map(allMediaList) { list ->
    list?.filter { it.checked }?.sortedBy { it.order }
  }
  var changeAlbum: MutableLiveData<Boolean> = MutableLiveData(false)
  var previewPosition: Int? = null

  var preloadPageNum = 1
  private var mediaType: Int? = null

  init {
    shouldShowList.postValue(mutableListOf())
    allMediaList.postValue(mutableListOf())
    shouldShowList.addSource(allMediaList) {
      if (it == null || it.isEmpty()) return@addSource
      shouldShowList.postValue(product(it))
    }

    shouldShowList.addSource(currentBucketId) {
      val product = product(allMediaList.value)
      product ?: return@addSource
      shouldShowList.postValue(product)
    }
  }

  private fun product(list: MutableList<LocalMedia>?) = list?.filter { m ->
    when (currentBucketId.value) {
      allPhotoBucketId -> m.mediaType == MEDIA_TYPE_IMAGE
      allVideoBucketId -> m.mediaType == MEDIA_TYPE_VIDEO
      allMediaBucketId -> true
      null -> false
      else -> m.bucketId == currentBucketId.value && m.mediaType == mediaType
    }
  }

  var allMediaHadGot: Boolean = false
  private val albumList: MutableList<AlbumModel> = mutableListOf()

  var currentUpdateType: UpdateType? = null
    private set

  fun fetchResource(
    bucketId: Long? = null,
    pageNum: Int? = null,
    pageLimit: Int? = null
  ) = uiScope.launch {
    val tBucket = bucketId ?: currentBucketId.value ?: allMediaBucketId
    val pl = if (pageNum != null) pageLimit ?: defaultPageSize else null
    val deferred = async { mediaManager.applyBucket(tBucket).loadPage(pageNum, pl).request() }
    val list = deferred.await() ?: return@launch
    pl?.let { allMediaHadGot = list.size < it } ?: run { allMediaHadGot = list.isEmpty() }
    updateLocalMedia(list, if (pageNum != null) UPDATE_PART else UPDATE_ALL)
  }

  fun notifyGalleryChanged(changed: Boolean) = uiScope.launch {
    notifyGalleryUpdate.value = changed
  }

  private fun updateLocalMedia(list: MutableList<LocalMedia>, updateType: UpdateType) =
    uiScope.launch {
      currentUpdateType = updateType
      if (allMediaHadGot && updateType == UPDATE_PART) return@launch
      allMediaList.value = when (updateType) {
        UPDATE_ALL -> list
        UPDATE_PART -> allMediaList.value?.apply { addAll(list) }
        else -> replaceItem(list)
      }
    }

  private fun replaceItem(list: List<LocalMedia>) = allMediaList.value?.apply {
    list.forEach { m ->
      val index = indexOf(find { m._id == it._id })
      removeAt(index)
      add(index, m)
    }
  }

  fun updateSelectItem(position: Int, isChecked: Boolean, isOtherPage: Boolean = false) {
    val sLimit = getSelectLimit()
    val before = getSelectedCount()
    if (!isChecked && before <= 0 || isChecked && before >= sLimit) return
    shouldShowList.value?.let { list ->
      list[position].checked = isChecked
      list[position].order = if (isChecked) sLimit else null
      val allSelected = getAllSelectedItem()
      allSelected?.sortedBy { it.order }
        ?.forEach { t -> list.find { t._id == it._id }?.apply { order = t.order } }
      val after = getSelectedCount()
      val cannotSelect = after == sLimit
      val canReselect = after == sLimit - 1 && !isChecked
      if (cannotSelect) {
        list.filterNot { it.checked }.forEach { it.enable = false }
      }
      if (canReselect) {
        list.forEach { it.enable = true }
      }
      val type = if (cannotSelect || canReselect || isOtherPage) UPDATE_SPECIAL_ALL
      else UPDATE_SPECIAL_PART
      val result: MutableList<LocalMedia> = mutableListOf()
      result.addAll(list)
      updateLocalMedia(result, type)
    }
  }

  fun getSelectLimit(): Int =
    selectLimit.value ?: if (isVideo()) videoSelectLimit else photoSelectLimit

  fun getSelectedCount(): Int =
    allMediaList.let { it.value?.filter { m -> m.checked }?.size } ?: 0

  fun getAllSelectedItem(): List<LocalMedia>? =
    allMediaList.value?.asSequence()?.filter { m -> m.checked }?.onEach { j -> j.enable = true }
      ?.sortedBy { t -> t.order }?.onEachIndexed { i, k -> k.order = i + 1 }?.toList()

  fun updateSelectedAlbum(index: Int) = uiScope.launch {
    if (albumList.size <= 0) return@launch
    if (index < 0 || index >= albumList.size) return@launch
    val album = albumList[index]
    preloadPageNum = 1
    currentUpdateType = UPDATE_ALL
    changeAlbum.value = true
    changeAlbum.value = false
    currentBucketId.value = album.albumId ?: allPhotoBucketId
  }

  fun resetData() = uiScope.launch {
    allMediaList.value = null
    shouldShowList.value = null
    currentBucketId.value = null
    changeAlbum.value = null
    previewPosition = null
    notifyGalleryUpdate.value = null
    selectLimit.value = null
    currentUpdateType = null
    preloadPageNum = 1
    mediaType = null
    allMediaHadGot = false
    albumList.clear()
  }

  fun fetchAllAssets(isVideo: Boolean) = uiScope.launch {
    mediaType = if (isVideo) MEDIA_TYPE_VIDEO else MEDIA_TYPE_IMAGE
    currentBucketId.value = if (isVideo) allVideoBucketId else allPhotoBucketId
    fetchResource(pageNum = preloadPageNum++)
  }

  fun isVideo() = mediaType == MEDIA_TYPE_VIDEO

  fun getAllAlbum(promise: Promise) = uiScope.launch {
    promise.resolve(fetchAlbum())
  }

  fun updateAlbum(id: Int? = null) = uiScope.launch {
    id ?: return@launch
    val bundle = Arguments.createMap().also { it.putArray(newAlbums, fetchAlbum()) }
    receiveEvent(id, ON_ALBUM_UPDATE, bundle)
  }

  private suspend fun fetchAlbum(): WritableArray? = withContext(Dispatchers.Default) {
    val deferredA = async { mediaManager.applyBucket(allMediaBucketId).request() }
    val allMedia = deferredA.await() ?: return@withContext null
    allMediaHadGot = true
    updateLocalMedia(allMedia, UPDATE_ALL)
    val alAlbum = mediaManager.getAllAlbum(allMedia, mediaType)
    albumList.clear()
    albumList.addAll(alAlbum)
    val deferredB = async { transferToArgumentsArray(albumList) }
    deferredB.await()
  }

  private suspend fun transferToArgumentsArray(list: MutableList<AlbumModel>): WritableArray =
    withContext(Dispatchers.IO) {
      Arguments.createArray().also { list.forEach { m -> it.pushMap(m.toMap()) } }
    }

  fun previewVideo(promise: Promise) = uiScope.launch {
    previewPosition?.let { p ->
      shouldShowList.value?.let {
        if (p < 0 || p >= it.size) promise.reject("index out of bounds!")
        promise.resolve(it[p].toMapVideoPreview())
      } ?: promise.reject("haven't got show list!")
    } ?: promise.reject("haven't got click position!")
  }

  fun updateSelectLimit(limit: Int) = uiScope.launch {
    selectLimit.value = limit
  }

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

}

enum class UpdateType {
  UPDATE_ALL, UPDATE_PART, UPDATE_SPECIAL_ALL, UPDATE_SPECIAL_PART
}
