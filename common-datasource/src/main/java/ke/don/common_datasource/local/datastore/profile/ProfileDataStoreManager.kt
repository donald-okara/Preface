package ke.don.common_datasource.local.datastore.profile

import android.content.Context
import ke.don.shared_domain.data_models.Profile
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last

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

    suspend fun getProfileFromDatastore(): Profile{
        return context.profileDataStore.data
            .filter { it.authId.isNotEmpty() } // Wait until the profile has a valid ID
            .first()
    }
}