package ke.don.common_datasource.local.datastore.profile

import android.content.Context
import android.util.Log
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.states.NetworkResult
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

class ProfileDataStoreManager(private val context : Context) {
    suspend fun setProfileInDatastore(profile : Profile): NetworkResult<NoDataReturned>{
        return try {
            Log.d(TAG, "Inserting Profile}")

            context.profileDataStore.updateData {
                it.copy(
                    name = profile.name,
                    authId = profile.authId,
                    avatarUrl = profile.avatarUrl
                )

            }
            Log.d(TAG, "Inserting Profile successful}")

            NetworkResult.Success(NoDataReturned())
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(
                message = e.message.toString()
            )
        }

    }

    suspend fun clearProfileDataStore(){
        context.profileDataStore.updateData { Profile() }
    }

    suspend fun getProfileFromDatastore(): Profile {
        return withTimeoutOrNull(5_000) { // 5-second timeout
            context.profileDataStore.data
                .filter { it.authId.isNotEmpty() }
                .first()
        } ?: Profile(authId = "", /* other default fields */) // or throw an exception
    }

    companion object {
        const val TAG = "ProfileDatastoreManager"
    }
}