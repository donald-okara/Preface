package ke.don.common_datasource.remote.domain.repositories

import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.states.UserLibraryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val rawNonce: String
    val hashedNonce: String
    val userProfile : StateFlow<Profile?>
    val userLibraryState: StateFlow<UserLibraryState>
    val userId : StateFlow<String?>

    suspend fun signInAndInsertProfile(
        idToken: String,
        displayName: String?,
        profilePictureUri: String?
    )

    suspend fun fetchUserBookshelves()

    suspend fun checkSignedInStatus(): Boolean
}