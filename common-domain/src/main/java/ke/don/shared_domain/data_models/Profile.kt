package ke.don.shared_domain.data_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val name : String = "",
    val email: String = "",
    @SerialName("auth_id")
    val authId : String = "",
    @SerialName("avatar_url")
    val avatarUrl : String = "",
    @SerialName("delete_account_request")
    val deleteAccountRequest: Boolean = false
)

@Serializable
data class ProfileDetails(
    val name : String = "",

    @SerialName("auth_id")
    val authId : String = "",
    @SerialName("avatar_url")
    val avatarUrl : String = "",
    @SerialName("discovered_books")
    val discoveredBooks: Int = 0,
    val email : String = ""
)
