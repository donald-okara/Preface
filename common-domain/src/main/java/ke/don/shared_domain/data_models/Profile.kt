package ke.don.shared_domain.data_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: Int = 0,
    val name : String = "",

    @SerialName("auth_id")
    val authId : String = "",
    @SerialName("avatar_url")
    val avatarUrl : String = "",
)

@Serializable
data class ProfileDetails(
    val id: Int = 0,
    val name : String = "",

    @SerialName("auth_id")
    val authId : String = "",
    @SerialName("avatar_url")
    val avatarUrl : String = "",
    @SerialName("discovered_books")
    val discoveredBooks: Int = 0,
    val email : String = ""
)
