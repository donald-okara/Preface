package ke.don.shared_domain.data_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddBookToBookshelf(
    val id : Int = 0,
    @SerialName("bookshelf_id")
    val bookshelfId : Int = 0,
    @SerialName("book_id")
    val bookId : String = "",
    val title : String = "",
    val description : String = "",
    @SerialName("highest_image_url")
    val highestImageUrl : String? = null,
    @SerialName("lowest_image_url")
    val lowestImageUrl : String? = null,
    val source : BookSource = BookSource.Google,
)