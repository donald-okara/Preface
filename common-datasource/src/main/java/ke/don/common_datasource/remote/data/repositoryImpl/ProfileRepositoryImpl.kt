package ke.don.common_datasource.remote.data.repositoryImpl

import android.util.Log
import ke.don.common_datasource.remote.data.network.ProfileNetworkClass
import ke.don.common_datasource.remote.domain.getters.generateNonce
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.shared_domain.data_models.Profile

class ProfileRepositoryImpl(
    private val profileNetworkClass: ProfileNetworkClass
): ProfileRepository {
    override val rawNonce: String
    override val hashedNonce: String

    init {
        val (raw, hashed) = generateNonce()
        rawNonce = raw
        hashedNonce = hashed
    }

    override suspend fun signInAndUpsertProfile(
        idToken: String,
        displayName: String?,
        profilePictureUri: String?
    ) {
        val profile = Profile(
            name = displayName.orEmpty(),
            avatarUrl = profilePictureUri.orEmpty()
        )

        try {
            if (profileNetworkClass.signIn(idToken, rawNonce)) {
                val isProfilePresent = profileNetworkClass.checkIfProfileIsPresent()
                Log.d("ProfileRepositoryImpl", "Is profile present: $isProfilePresent")

                if (!isProfilePresent) {
                    Log.d("ProfileRepositoryImpl", "Profile is not present. Inserting profile.")
                    profileNetworkClass.insertUserProfile(profile)
                } else {
                    Log.d("ProfileRepositoryImpl", "Profile is present.")
                }
            }
        } catch (e: Exception) {
            Log.e("ProfileRepositoryImpl", "Error during sign-in or profile operation", e)
        }
    }


}