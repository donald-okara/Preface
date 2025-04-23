package ke.don.common_datasource.local.worker.classes

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ke.don.common_datasource.local.datastore.profile.ProfileDataStoreManager
import ke.don.common_datasource.remote.data.profile.network.ProfileNetworkClass
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.states.NetworkResult

@HiltWorker
class SyncProfileWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userProfile: Profile?,
    private val profileDataStoreManager: ProfileDataStoreManager,
    private val profileNetworkClass: ProfileNetworkClass,
) : CoroutineWorker(appContext, workerParams){
    override suspend fun doWork(): Result {
        if (userProfile == null) return Result.failure()

        return try {
            when(val profileResult = profileNetworkClass.fetchUserProfile(userProfile.authId)){
                is NetworkResult.Error -> Result.retry()
                is NetworkResult.Success -> {
                    profileDataStoreManager.setProfileInDatastore(profileResult.data)
                    Result.success()
                }
            }
        }catch (e: Exception) {
            Result.retry()
        }
    }

}