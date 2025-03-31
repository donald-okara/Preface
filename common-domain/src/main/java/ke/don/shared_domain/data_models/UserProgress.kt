package ke.don.shared_domain.data_models

import kotlinx.serialization.SerialName

data class CreateUserProgressDTO(
    @SerialName("book_id")
    val bookId: String,
    @SerialName("current_page")
    val currentPage: Int,
    @SerialName("total_pages")
    val totalPages: Int
)

data class UserProgressResponse(
    @SerialName("user_id")
    val userId: String,
    @SerialName("book_id")
    val bookId: String,
    @SerialName("current_page")
    val currentPage: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("last_updated")
    val lastUpdated: String
)
