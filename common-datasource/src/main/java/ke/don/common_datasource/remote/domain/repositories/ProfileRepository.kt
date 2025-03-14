package ke.don.common_datasource.remote.domain.repositories

import ke.don.shared_domain.data_models.Profile
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val rawNonce: String
    val hashedNonce: String
    val userProfile : StateFlow<Profile?>
    val userId : StateFlow<String?>

    suspend fun signInAndInsertProfile(
        idToken: String,
        displayName: String?,
        profilePictureUri: String?
    ):Boolean

    suspend fun checkSignedInStatus(): Boolean
}