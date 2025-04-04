package ke.don.shared_domain.data_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserProgressDTO(
    @SerialName("book_id")
    val bookId: String,
    @SerialName("current_page")
    val currentPage: Int,
    @SerialName("total_pages")
    val totalPages: Int
)

@Serializable
data class UserProgressResponse(
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("book_id")
    val bookId: String = "",
    @SerialName("current_page")
    val currentPage: Int = 0,
    @SerialName("total_pages")
    val totalPages: Int = 0,
    @SerialName("last_updated")
    val lastUpdated: String = ""
)
