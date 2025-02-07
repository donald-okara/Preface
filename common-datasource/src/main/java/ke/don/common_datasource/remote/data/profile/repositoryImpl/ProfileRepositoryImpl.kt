package ke.don.common_datasource.remote.data.profile.repositoryImpl

import android.util.Log
import ke.don.common_datasource.local.datastore.profile.ProfileDataStoreManager
import ke.don.common_datasource.remote.data.profile.network.ProfileNetworkClass
import ke.don.common_datasource.remote.domain.getters.generateNonce
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.shared_domain.data_models.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ProfileRepositoryImpl(
    private val profileNetworkClass: ProfileNetworkClass,
    private val profileDataStoreManager: ProfileDataStoreManager
): ProfileRepository {
    override val rawNonce: String
    override val hashedNonce: String

    private val _userProfile = MutableStateFlow(Profile())
    override val userProfile: StateFlow<Profile> = _userProfile

    init {
        val (raw, hashed) = generateNonce()
        rawNonce = raw
        hashedNonce = hashed
    }
    override suspend fun checkSignedInStatus(): Boolean = profileNetworkClass.checkSignedInStatus()

    override suspend fun signInAndInsertProfile(
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

                if (!isProfilePresent) {
                    Log.d(TAG, "Profile is not present. Inserting profile.")
                    profileNetworkClass.insertUserProfile(profile)
                } else {
                    Log.d(TAG, "Profile is present.")
                }

                _userProfile.update {
                    profileNetworkClass.fetchUserProfile()!!
                }

                _userProfile.value.let {
                    profileDataStoreManager.setProfileInDatastore(
                        it
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "ProfileRepositoryImpl"
    }

}