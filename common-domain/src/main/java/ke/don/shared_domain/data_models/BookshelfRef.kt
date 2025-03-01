package ke.don.shared_domain.data_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookshelfRef(
    val id: Int = 0,
    val name : String = "",
    val description : String = "",

    @SerialName("bookshelf_type")
    val bookshelfType : String = "",

    @SerialName("user_id")
    val userId : String = "",

    val books : List<BookJson> = emptyList()
)

@Serializable
data class BookJson(
    @SerialName("book_id")
    val bookId : String = ""
)

data class BookShelf(
    val supabaseBookShelf: BookshelfRef = BookshelfRef(),
    val books : List<SupabaseBook> = emptyList()
)
