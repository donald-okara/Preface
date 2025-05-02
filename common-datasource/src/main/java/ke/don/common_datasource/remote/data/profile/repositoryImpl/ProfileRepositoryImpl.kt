package ke.don.common_datasource.remote.data.profile.repositoryImpl

import android.content.Context
import android.util.Log
import android.widget.Toast
import ke.don.common_datasource.local.datastore.profile.ProfileDataStoreManager
import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.remote.data.profile.network.ProfileNetworkClass
import ke.don.common_datasource.remote.domain.error_handler.CompositeErrorHandler
import ke.don.common_datasource.remote.domain.error_handler.Koffee
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.data_models.ProfileDetails
import ke.don.shared_domain.states.NetworkResult
import java.security.MessageDigest
import java.util.UUID

class ProfileRepositoryImpl(
    private val profileNetworkClass: ProfileNetworkClass,
    private val context: Context,
    private val bookshelfDao: BookshelfDao,
    private val profileDataStoreManager: ProfileDataStoreManager
): ProfileRepository {
    private val errorHandler = CompositeErrorHandler()

    private var _rawNonce: String? = null
    override val rawNonce: String
        get() = _rawNonce ?: throw IllegalStateException("Nonce not yet generated")

    override fun generateNonce(): Pair<String, String> {
        val raw = UUID.randomUUID().toString() // Generate raw nonce
        val bytes = raw.toByteArray() // Use raw directly, not rawNonce
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }
        _rawNonce = raw // Store raw nonce for later access
        return raw to hashedNonce
    }


    override suspend fun checkSignedInStatus(): Boolean = profileNetworkClass.checkSignedInStatus()

    override suspend fun signInAndInsertProfile(
        idToken: String,
        displayName: String,
        profilePictureUri: String
    ): NetworkResult<NoDataReturned> {
      return try {
            val signInResult = profileNetworkClass.signIn(idToken, rawNonce)

            if (signInResult is NetworkResult.Error) {
                Log.e(TAG, "Sign-in failed: ${signInResult.message}")
                return NetworkResult.Error(
                    message = signInResult.message
                )
            }

            val userId = (signInResult as NetworkResult.Success).data.id

            val profile = Profile(
                name = displayName,
                avatarUrl = profilePictureUri,
                authId = signInResult.data.id,
                email = signInResult.data.email ?: "",
            )

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


          profileDataStoreManager.setProfileInDatastore(profile)
        } catch (e: Exception) {
            Log.e(TAG, "Exception in signInAndInsertProfile", e)
            errorHandler.handleException(e)
        }
    }

    /**
     * Fetches the user profile and stores it in Datastore.
     */
    override suspend fun syncUserProfile(userId: String): NetworkResult<NoDataReturned> {
        val profileResult = profileNetworkClass.fetchUserProfile(userId)

        return if (profileResult is NetworkResult.Success) {
            profileDataStoreManager.setProfileInDatastore(profileResult.data)
        } else {
            Log.e(TAG, "Error fetching profile: ${(profileResult as NetworkResult.Error).message}")
            NetworkResult.Error(
                message = (profileResult).message
            )
        }
    }

    override suspend fun fetchProfileFromDataStore(): Profile = profileDataStoreManager.getProfileFromDatastore()

    override suspend fun fetchProfileDetails(userId: String): NetworkResult<ProfileDetails?> {
        return profileNetworkClass.fetchProfileDetails(userId).also { result ->
            if (result is NetworkResult.Error) {
                Koffee.toast(context, result)
            }
        }

    }

    override suspend fun signOut(): NetworkResult<NoDataReturned> {
        return profileNetworkClass.signOut().also { result ->
            if (result is NetworkResult.Error){
                Koffee.toast(context, result)
            }else{
                profileDataStoreManager.clearProfileDataStore()
                bookshelfDao.deleteAllBookshelves()
            }
        }
    }

    override suspend fun deleteUser(userId: String): NetworkResult<NoDataReturned> {
        return profileNetworkClass.deleteProfile(userId).also {
            if(it is NetworkResult.Error){
                Koffee.toast(context, it)
            }else{
                Toast.makeText(context, "Request successful. This may take a while",Toast.LENGTH_SHORT).show()

                profileDataStoreManager.clearProfileDataStore()
                bookshelfDao.deleteAllBookshelves()
            }
        }
    }

    companion object {
        private const val TAG = "ProfileRepositoryImpl"
    }

}