package com.don.preface.data.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class BookListItemResponse(
    val kind: String,
    val totalItems: Int,
    val items: List<BookItem> = emptyList()
)

@Stable
@Parcelize
@Serializable
data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo
): Parcelable

@Parcelize
@Serializable
data class VolumeInfo(
    val title: String,
    val authors: List<String> = emptyList(),
    val publisher: String = "",
    val publishedDate: String = "",
    val description: String = "",
    val imageLinks: ImageLinks?,
    val previewLink: String = ""
): Parcelable

@Parcelize
@Serializable
data class ImageLinks(
    val smallThumbnail: String = "",
    val thumbnail: String = "",
    val small: String = "",
    val medium: String = "",
    val large: String = "",
    val extraLarge: String = ""
): Parcelable
