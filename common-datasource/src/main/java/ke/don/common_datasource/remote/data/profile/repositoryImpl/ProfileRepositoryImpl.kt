package ke.don.common_datasource.remote.data.profile.repositoryImpl

import android.content.Context
import android.util.Log
import ke.don.common_datasource.local.datastore.profile.ProfileDataStoreManager
import ke.don.common_datasource.local.datastore.profile.profileDataStore
import ke.don.common_datasource.remote.data.profile.network.ProfileNetworkClass
import ke.don.common_datasource.remote.domain.getters.generateNonce
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.states.NetworkResult
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
    private val profileDataStoreManager: ProfileDataStoreManager
): ProfileRepository {
    override val rawNonce: String
    override val hashedNonce: String

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
    ): NetworkResult<NoDataReturned> {
        val profile = Profile(
            name = displayName.orEmpty(),
            avatarUrl = profilePictureUri.orEmpty()
        )

        return try {
            val signInResult = profileNetworkClass.signIn(idToken, rawNonce)

            if (signInResult is NetworkResult.Error) {
                Log.e(TAG, "Sign-in failed: ${signInResult.message}")
                return NetworkResult.Error(
                    message = signInResult.message
                )
            }

            val userId = (signInResult as NetworkResult.Success).data.id
            val profileIsPresentResult = profileNetworkClass.checkIfProfileIsPresent(userId)

            if (profileIsPresentResult is NetworkResult.Error) {
                Log.e(TAG, "Error checking profile presence: ${profileIsPresentResult.message}")
                return NetworkResult.Error(
                    message = profileIsPresentResult.message
                )
            }

            if (!(profileIsPresentResult as NetworkResult.Success).data) {
                val insertResult = profileNetworkClass.insertUserProfile(profile)
                if (insertResult is NetworkResult.Error) {
                    Log.e(TAG, "Error inserting profile: ${insertResult.message}")
                    return NetworkResult.Error(
                        message = insertResult.message
                    )
                }
            }

            fetchAndStoreUserProfile(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Exception in signInAndInsertProfile", e)
            NetworkResult.Error(
                message = e.message.toString()
            )
        }
    }

    /**
     * Fetches the user profile and stores it in Datastore.
     */
    private suspend fun fetchAndStoreUserProfile(userId: String): NetworkResult<NoDataReturned> {
        val profileResult = profileNetworkClass.fetchUserProfile(userId)

        return if (profileResult is NetworkResult.Success) {
            profileDataStoreManager.setProfileInDatastore(profileResult.data)
            Log.d(TAG, "Profile fetched and stored successfully")
            NetworkResult.Success(NoDataReturned())
        } else {
            Log.e(TAG, "Error fetching profile: ${(profileResult as NetworkResult.Error).message}")
            NetworkResult.Error(
                message = (profileResult as NetworkResult.Error).message
            )
        }
    }


    companion object {
        private const val TAG = "ProfileRepositoryImpl"
    }

}