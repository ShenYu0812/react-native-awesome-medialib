package io.project5e.lib.media.manager

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestOptions(
  var bucket: Long? = null,
  var pageLoadParams: PageLoadParams? = null
) : Parcelable {

  fun isPageLoad(): Boolean = pageLoadParams?.isNotEmpty() ?: false

  override fun equals(other: Any?): Boolean {
    if (other == null) return false
    if (this === other) return true
    if (javaClass != other.javaClass) return false
    other as RequestOptions
    if (bucket != other.bucket) return false
    if (pageLoadParams == null) return false
    if (pageLoadParams != other.pageLoadParams) return false
    return true
  }

  override fun hashCode(): Int {
    var result = bucket.hashCode()
    result = 31 * result + (pageLoadParams?.hashCode() ?: 0)
    return result
  }

  @Parcelize
  data class PageLoadParams(
    var page: Int? = null,
    var pageLimit: Int? = null
  ) : Parcelable {

    fun isNotEmpty(): Boolean =
      (page != null && pageLimit != null && page!! > 0 || pageLimit!! > 0)

    override fun equals(other: Any?): Boolean {
      if (other == null) return false
      if (this === other) return true
      if (javaClass != other.javaClass) return false
      other as PageLoadParams
      if (page == null || pageLimit == null) return false
      if (page != other.page) return false
      if (pageLimit != other.pageLimit) return false
      return true
    }

    override fun hashCode(): Int {
      var result = page ?: 0
      result = 31 * result + (pageLimit ?: 0)
      return result
    }

    override fun toString(): String {
      return "PageLoadParams(page=$page, pageLimit=$pageLimit)"
    }

  }
}
