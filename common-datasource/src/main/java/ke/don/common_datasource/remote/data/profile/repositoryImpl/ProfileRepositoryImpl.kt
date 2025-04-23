package ke.don.common_datasource.remote.data.profile.repositoryImpl

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ke.don.common_datasource.local.datastore.profile.ProfileDataStoreManager
import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.local.worker.classes.SyncProfileWorker
import ke.don.common_datasource.remote.data.profile.network.ProfileNetworkClass
import ke.don.common_datasource.remote.domain.getters.generateNonce
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.data_models.ProfileDetails
import ke.don.shared_domain.states.NetworkResult

class ProfileRepositoryImpl(
    private val profileNetworkClass: ProfileNetworkClass,
    private val context: Context,
    private val bookshelfDao: BookshelfDao,
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

            syncUserProfile(userId)
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
    override suspend fun syncUserProfile(userId: String): NetworkResult<NoDataReturned> {
        val profileResult = profileNetworkClass.fetchUserProfile(userId)

        return if (profileResult is NetworkResult.Success) {
            val request = OneTimeWorkRequestBuilder<SyncProfileWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "profile_sync",
                    ExistingWorkPolicy.REPLACE,
                    request
                )
            NetworkResult.Success(NoDataReturned())
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
                Toast.makeText(context, "${'$'}{result.message} ${'$'}{result.hint}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override suspend fun signOut(): NetworkResult<NoDataReturned> {
        return profileNetworkClass.signOut().also { result ->
            if (result is NetworkResult.Success){
                profileDataStoreManager.clearProfileDataStore()
                bookshelfDao.deleteAllBookshelves()
            }else{
                Toast.makeText(context, "Something went wrong",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override suspend fun deleteUser(userId: String): NetworkResult<NoDataReturned> {
        return profileNetworkClass.deleteProfile(userId).also {
            if(it is NetworkResult.Success){
                Toast.makeText(context, "Request successful. This may take a while",Toast.LENGTH_SHORT).show()

                profileDataStoreManager.clearProfileDataStore()
                bookshelfDao.deleteAllBookshelves()
            }else{
                Toast.makeText(context, "Something went wrong",Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "ProfileRepositoryImpl"
    }

}