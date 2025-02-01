package ke.don.shared_domain.data_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val name : String = "",

    @SerialName("avatar_url")
    val avatarUrl : String = ""
)
