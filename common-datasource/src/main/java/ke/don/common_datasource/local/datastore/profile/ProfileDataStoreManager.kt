package ke.don.common_datasource.local.datastore.profile

import android.content.Context
import ke.don.shared_domain.data_models.Profile
import kotlinx.coroutines.flow.first

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

    suspend fun getProfileFromDatastore(): Profile{
        return context.profileDataStore.data.first()
    }
}