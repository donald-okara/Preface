package ke.don.shared_domain.data_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookshelfCatalog(
    @SerialName("bookshelf_id")
    val bookshelfId : Int = 0,

    @SerialName("book_id")
    val bookId : String = "",

)