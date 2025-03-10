package ke.don.common_datasource.remote.data.profile.repositoryImpl

import android.content.Context
import android.util.Log
import ke.don.common_datasource.local.datastore.profile.ProfileDataStoreManager
import ke.don.common_datasource.local.datastore.profile.profileDataStore
import ke.don.common_datasource.remote.data.profile.network.ProfileNetworkClass
import ke.don.common_datasource.remote.domain.getters.generateNonce
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.shared_domain.data_models.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileRepositoryImpl(
    private val profileNetworkClass: ProfileNetworkClass,
    private val context: Context,
    private val profile: Profile?,
    private val profileDataStoreManager: ProfileDataStoreManager
): ProfileRepository {
    override val rawNonce: String
    override val hashedNonce: String

    private val _userProfile = MutableStateFlow(Profile())
    override val userProfile: StateFlow<Profile> = _userProfile

    private val _userId = MutableStateFlow<String?>(null)
    override val userId: StateFlow<String?> = _userId

    init {
        CoroutineScope(Dispatchers.IO).launch{
            val profile = context.profileDataStore.data.first()
            _userId.value = profile.authId

        }
        val (raw, hashed) = generateNonce()
        rawNonce = raw
        hashedNonce = hashed
    }

    override suspend fun checkSignedInStatus(): Boolean = profileNetworkClass.checkSignedInStatus()

    override suspend fun signInAndInsertProfile(
        idToken: String,
        displayName: String?,
        profilePictureUri: String?
    ): Boolean {
        val profile = Profile(
            name = displayName.orEmpty(),
            avatarUrl = profilePictureUri.orEmpty()
        )

        return try {
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

                Log.d(TAG, "Profile fetched from network class:: ${userProfile.value}")
                _userProfile.value.let {
                    profileDataStoreManager.setProfileInDatastore(
                        it
                    )
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        private const val TAG = "ProfileRepositoryImpl"
    }

}