package io.project5e.lib.media.model

import androidx.lifecycle.*
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableArray
import io.project5e.lib.media.manager.*
import io.project5e.lib.media.model.UpdateType.*
import kotlinx.coroutines.*


@Suppress("DEPRECATION")
class GalleryViewModel : ViewModel() {

  private val photoSelectLimit = 9
  private val videoSelectLimit = 1
  private val viewModelJob = SupervisorJob()
  private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  private val mediaManager: LocalMediaManager = LocalMediaManager.getInstance()
  private val allMediaList: MutableLiveData<MutableList<LocalMedia>> = MutableLiveData()
  private val currentBucketId: MutableLiveData<Long> = MutableLiveData()
  val selectLimit: MutableLiveData<Int> = MutableLiveData()

  val notifyGalleryUpdate: MutableLiveData<LocalMedia?> = MutableLiveData()
  val shouldShowList: MediatorLiveData<List<LocalMedia>> = MediatorLiveData()
  val selectedList = Transformations.map(allMediaList) { list ->
    list?.filter { it.checked }?.sortedBy { it.order }
  }
  var changeAlbum: MutableLiveData<Boolean> = MutableLiveData(false)
  var previewPosition: Int? = null

  private var mediaType: Int? = null

  val sourceAdded: MutableLiveData<Boolean> = MutableLiveData(null)
  fun addResource() = uiScope.launch {
    if (sourceAdded.value == true) return@launch
    shouldShowList.addSource(allMediaList) {
      if (it == null || it.isEmpty()) return@addSource
      shouldShowList.postValue(product(it))
    }

    shouldShowList.addSource(currentBucketId) {
      val product = product(allMediaList.value)
      product ?: return@addSource
      shouldShowList.postValue(product)
    }
    sourceAdded.value = true
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

  private fun preloadResource() = uiScope.launch {
    val tb = currentBucketId.value ?: allMediaBucketId
    val deferred = async { mediaManager.applyBucket(tb).loadPage(1, pageLimit).request() }
    val list = deferred.await() ?: return@launch
    allMediaHadGot = list.size < pageLimit || list.isEmpty()
    updateLocalMedia(list, if (allMediaHadGot) UPDATE_ALL else UPDATE_PART)
  }

  fun notifyGalleryChanged(added: LocalMedia?) = uiScope.launch {
    notifyGalleryUpdate.value = added
  }

  private fun updateLocalMedia(list: MutableList<LocalMedia>, updateType: UpdateType) =
    uiScope.launch {
      currentUpdateType = updateType
      if (allMediaHadGot && updateType == UPDATE_PART) return@launch
      allMediaList.value = when (updateType) {
        UPDATE_ALL -> list
        UPDATE_PART -> allMediaList.value?.apply { if (ensureNotRepeat(list)) addAll(list) }
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

  private fun ensureNotRepeat(list: MutableList<LocalMedia>): Boolean {
    val temp = allMediaList.value
    if (temp.isNullOrEmpty()) return true
    list.iterator().forEach { temp.find { t -> t._id == it._id } ?: return true }
    return false
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
    currentUpdateType = UPDATE_ALL
    changeAlbum.value = true
    changeAlbum.value = false
    currentBucketId.value = album.albumId ?: allPhotoBucketId
  }

  fun clearViewModel() {
    allMediaList.postValue(mutableListOf())
    currentBucketId.postValue(null)
    removeResource()
    savedSelected.clear()
    fromCamera.clear()
    changeAlbum.postValue(false)
    notifyGalleryUpdate.postValue(null)
    selectLimit.postValue(null)
    allMediaHadGot = false
    previewPosition = null
    currentUpdateType = null
    mediaType = null
    albumList.clear()
  }

  private fun removeResource() = uiScope.launch {
    shouldShowList.removeSource(allMediaList)
    shouldShowList.removeSource(currentBucketId)
    sourceAdded.value = false
    sourceAdded.value = null
  }

  fun fetchAllAssets(isVideo: Boolean) = uiScope.launch {
    mediaType = if (isVideo) MEDIA_TYPE_VIDEO else MEDIA_TYPE_IMAGE
    currentBucketId.value = if (isVideo) allVideoBucketId else allPhotoBucketId
    if (allMediaHadGot) return@launch
    preloadResource()
  }

  fun isVideo() = mediaType == MEDIA_TYPE_VIDEO

  fun getAllAlbum(promise: Promise) = uiScope.launch {
    promise.resolve(fetchAlbum())
  }

  suspend fun fetchAlbum(restore: Boolean = false, added: LocalMedia? = null): WritableArray? =
    withContext(Dispatchers.Default) {
      added?.let { fromCamera.add(added) }
      if (restore) tempSaveSelectedList()
      val deferredA = async { mediaManager.applyBucket(allMediaBucketId).request() }
      var allMedia = deferredA.await() ?: return@withContext null
      if (restore) allMedia = restoreSelectedStatus(allMedia, added)
      savedSelected.clear()
      updateLocalMedia(allMedia, UPDATE_ALL)
      allMediaHadGot = true
      val alAlbum = mediaManager.getAllAlbum(allMedia, mediaType)
      albumList.clear()
      albumList.addAll(alAlbum)
      val deferredB = async { transferToArgumentsArray(albumList) }
      deferredB.await()
    }

  private val savedSelected: MutableList<LocalMedia> = mutableListOf()
  private val fromCamera: MutableList<LocalMedia> = mutableListOf()
  private fun tempSaveSelectedList() {
    savedSelected.clear()
    val allSelected = getAllSelectedItem()
    allSelected?.let { savedSelected.addAll(it) }
    val iterator = fromCamera.iterator()
    iterator.forEach { m ->
      savedSelected.find { it.name == m.name }?.let {
        iterator.remove()
      } ?: run {
        savedSelected.add(m)
      }
    }
  }

  private fun restoreSelectedStatus(
    origin: MutableList<LocalMedia>,
    added: LocalMedia?
  ): MutableList<LocalMedia> {
    added?.let { t -> origin.find { t.name == it.name } ?: origin.add(0, t) }
    savedSelected.iterator().forEach { m ->
      origin.find { it._id == m._id || it.name == m.name }?.apply { order = m.order }
        ?.apply { checked = m.checked }?.apply { enable = m.enable }
    }
    selectLimit.value?.let { s ->
      val size = savedSelected.size
      if (size >= s) origin.filter { !it.checked }.forEach { it.enable = false }
      val before = getSelectedCount()
      val order0 = if (before < s) before + 1 else null
      origin[0].apply { enable = before < s }.apply { checked = enable }
        .apply { order = order0 }
      val after = order0 ?: before
      if (after < s) return@let
      origin.filter { !it.checked }.forEach { it.enable = false }
    }
    return origin
  }

  private suspend fun transferToArgumentsArray(list: MutableList<AlbumModel>): WritableArray =
    withContext(Dispatchers.IO) {
      Arguments.createArray().also { list.iterator().forEach { m -> it.pushMap(m.toMap()) } }
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
