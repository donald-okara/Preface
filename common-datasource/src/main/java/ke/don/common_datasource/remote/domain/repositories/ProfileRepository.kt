package ke.don.common_datasource.remote.domain.repositories

import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.states.NetworkResult
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val rawNonce: String
    val hashedNonce: String

    suspend fun signInAndInsertProfile(
        idToken: String,
        displayName: String?,
        profilePictureUri: String?
    ): NetworkResult<NoDataReturned>

    suspend fun checkSignedInStatus(): Boolean
}