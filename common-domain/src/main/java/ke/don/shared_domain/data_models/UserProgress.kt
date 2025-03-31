package ke.don.shared_domain.data_models

data class CreateUserProgressDTO(
    val userId: String,
    val bookId: String,
    val currentPage: Int,
    val totalPages: Int,
)

data class UserProgressResponse(
    val userId: String,
    val bookId: String,
    val currentPage: Int,
    val totalPages: Int,
    val lastUpdated: String
)
