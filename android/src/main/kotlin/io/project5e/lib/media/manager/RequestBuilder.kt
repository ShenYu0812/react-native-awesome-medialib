package io.project5e.lib.media.manager

class RequestBuilder {

  var isRequesting: Boolean = false
  var timestamp: Long = System.currentTimeMillis()
  var requestOptions: RequestOptions = RequestOptions()

  fun applyBucket(bucketId: Long): RequestBuilder {
    requestOptions.bucket = bucketId
    return this
  }

  fun loadPage(page: Int? = null, pageLimit: Int? = null): RequestBuilder {
    requestOptions.pageLoadParams =
      RequestOptions.PageLoadParams(page, pageLimit)
    return this
  }

  suspend fun request() = LocalMediaManager.getInstance().request(this)
}
