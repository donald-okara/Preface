package ke.don.shared_domain.data_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val name : String = "",

    @SerialName("auth_id")
    val authId : String,
    @SerialName("avatar_url")
    val avatarUrl : String = ""
)
