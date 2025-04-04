package ke.don.shared_domain.data_models

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
    val smallThumbnail: String? = null,
    val thumbnail: String? = null,
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null,
    val extraLarge: String? = null
): Parcelable {
    fun getHighestQualityUrl(): String? = this.let {
        extraLarge ?: large ?: medium ?: small ?: thumbnail ?: smallThumbnail
    }

}
