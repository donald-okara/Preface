package ke.don.common_datasource.local.worker.classes

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ke.don.common_datasource.local.datastore.profile.ProfileDataStoreManager
import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
import ke.don.common_datasource.remote.data.profile.network.ProfileNetworkClass
import ke.don.shared_domain.data_models.Profile
import javax.inject.Inject
import androidx.work.ListenableWorker


class PrefaceWorkerFactory @Inject constructor(
    private val bookshelfNetworkClass: BookshelfNetworkClass,
    private val bookshelfDao: BookshelfDao,
    private val profileDataStoreManager: ProfileDataStoreManager,
    private val userProfile: Profile?,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncBookshelvesWorker::class.java.name -> SyncBookshelvesWorker(
                appContext = appContext,
                workerParams = workerParameters,
                bookshelfNetworkClass = bookshelfNetworkClass,
                profileDataStoreManager = profileDataStoreManager,
                bookshelfDao = bookshelfDao,
            )

            else -> null // Let WorkManager handle unknown workers with default factory
        }
    }
}