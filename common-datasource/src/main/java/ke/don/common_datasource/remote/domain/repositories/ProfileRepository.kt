package ke.don.common_datasource.remote.domain.repositories

import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.data_models.ProfileDetails
import ke.don.shared_domain.states.NetworkResult
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val rawNonce: String

    fun generateNonce(): Pair<String, String> // Returns raw and hashed nonce

    suspend fun signInAndInsertProfile(
        idToken: String,
        displayName: String?,
        profilePictureUri: String?
    ): NetworkResult<NoDataReturned>

    suspend fun syncUserProfile(userId: String): NetworkResult<NoDataReturned>

    suspend fun checkSignedInStatus(): Boolean

    suspend fun fetchProfileFromDataStore(): Profile

    suspend fun fetchProfileDetails(userId: String): NetworkResult<ProfileDetails?>

    suspend fun signOut(): NetworkResult<NoDataReturned>

    suspend fun deleteUser(userId: String): NetworkResult<NoDataReturned>

}
