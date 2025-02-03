package ke.don.common_datasource.domain.repositories

import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val rawNonce: String
    val hashedNonce: String

    suspend fun signInAndCheckProfile(
        idToken: String,
        displayName: String?,
        profilePictureUri: String?
    )

}