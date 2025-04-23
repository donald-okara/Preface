package ke.don.common_datasource.local.worker.classes

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.states.NetworkResult

@HiltWorker
class SyncBookshelvesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookshelfNetworkClass: BookshelfNetworkClass,
    private val bookshelfDao: BookshelfDao,
    private val userProfile: Profile?
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG,"Work manager running")
            if (userProfile == null) return Result.failure()
            Result.success()


            when (val result = bookshelfNetworkClass.fetchUserBookshelves(userProfile.authId)) {
                is NetworkResult.Error -> Result.retry()
                is NetworkResult.Success -> {
                    val remoteIds = result.data.map { it.id }.toSet()
                    bookshelfDao.deleteBookshelvesNotIn(remoteIds)
                    bookshelfDao.insertAll(result.data)
                    Result.success()
                }
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val TAG = "SyncBookshelvesWorker"
    }
}
