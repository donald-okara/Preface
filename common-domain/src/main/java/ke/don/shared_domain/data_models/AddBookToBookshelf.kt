package ke.don.shared_domain.data_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddBookToBookshelf(
    val id: Int = 0,
    @SerialName("bookshelf_id")
    val bookshelfId: Int = 0,
    @SerialName("book_id")
    val bookId: String = "",
    val title: String = "",
    val description: String = "",
    @SerialName("highest_image_url")
    val highestImageUrl: String? = null,
    @SerialName("lowest_image_url")
    val lowestImageUrl: String? = null,
    val source: BookSource = BookSource.Google,
    val authors: List<String> = emptyList(), // New field
    val publisher: String = "", // New field
    @SerialName("published_date")
    val publishedDate: String = "", // New field
    val categories: List<String> = emptyList(), // New field
    @SerialName("maturity_rating")
    val maturityRating: String = "", // New field
    val language: String = "", // New field
    @SerialName("preview_link")
    val previewLink: String = "" // New field
)

@Serializable
data class SupabaseBook(
    val id: Int = 0,
    @SerialName("book_id")
    val bookId: String = "",
    val title: String = "",
    val description: String = "",
    @SerialName("highest_image_url")
    val highestImageUrl: String? = null,
    @SerialName("lowest_image_url")
    val lowestImageUrl: String? = null,
    val source: BookSource = BookSource.Google,
    val authors: List<String> = emptyList(), // New field
    val publisher: String = "", // New field
    @SerialName("published_date")
    val publishedDate: String = "", // New field
    val categories: List<String> = emptyList(), // New field
    @SerialName("maturity_rating")
    val maturityRating: String = "", // New field
    val language: String = "", // New field
    @SerialName("preview_link")
    val previewLink: String = "" // New field
)
