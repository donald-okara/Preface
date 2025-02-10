package ke.don.shared_domain.data_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseBookshelf(
    val id: Int = 0,
    val name : String = "",
    val description : String = "",

    @SerialName("bookshelf_type")
    val bookshelfType : String = "",

    @SerialName("user_id")
    val userId : String = ""
)


