package ke.don.shared_domain.data_models

import kotlinx.serialization.Serializable

@Serializable
data class BookListItemResponse(
    val kind: String,
    val totalItems: Int,
    val items: List<BookItem> = emptyList()
)

@Serializable
data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo
)

@Serializable
data class VolumeInfo(
    val title: String,
    val authors: List<String> = emptyList(),
    val publisher: String = "",
    val publishedDate: String = "",
    val description: String = "",
    val imageLinks: ImageLinks?,
    val previewLink: String = ""
)

@Serializable
data class ImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null,
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null,
    val extraLarge: String? = null
) {
    fun getHighestQualityUrl(): String? = this.let {
        extraLarge ?: large ?: medium ?: small ?: thumbnail ?: smallThumbnail
    }

    fun getLowestQualityUrl(): String? = this.let {
        smallThumbnail ?: thumbnail ?: small ?: medium ?: large ?: extraLarge
    }

}
