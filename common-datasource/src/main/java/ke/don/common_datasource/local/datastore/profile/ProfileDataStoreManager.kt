package ke.don.common_datasource.local.datastore.profile

import android.content.Context
import ke.don.shared_domain.data_models.Profile
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.time.withTimeoutOrNull
import kotlinx.coroutines.withTimeoutOrNull

class ProfileDataStoreManager(private val context : Context) {
    suspend fun setProfileInDatastore(profile : Profile){
        context.profileDataStore.updateData {
            it.copy(
                id = profile.id,
                name = profile.name,
                authId = profile.authId,
                avatarUrl = profile.avatarUrl
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
}