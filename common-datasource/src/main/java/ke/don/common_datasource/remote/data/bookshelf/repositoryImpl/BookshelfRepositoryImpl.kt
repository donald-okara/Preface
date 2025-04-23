package ke.don.common_datasource.remote.data.bookshelf.repositoryImpl

import android.content.Context
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.local.roomdb.entities.toBookshelf
import ke.don.common_datasource.local.worker.classes.SyncBookshelvesWorker
import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.common_datasource.remote.domain.states.toEntity
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.states.NetworkResult
import kotlinx.coroutines.flow.Flow

class BookshelfRepositoryImpl(
    private val bookshelfNetworkClass: BookshelfNetworkClass,
    private val userProfile : Profile?,
    private val bookshelfDao: BookshelfDao,
    private val context : Context
): BookshelfRepository {

    override suspend fun createBookshelf(bookshelf: BookshelfRef): NetworkResult<NoDataReturned> {
        return bookshelfNetworkClass.createBookshelf(bookshelf).also { result->
            if (result is NetworkResult.Error){
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override suspend fun syncLocalBookshelvesDb(): NetworkResult<NoDataReturned> {
        return try {
            val request = OneTimeWorkRequestBuilder<SyncBookshelvesWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "bookshelf_sync",
                    ExistingWorkPolicy.REPLACE,
                    request
                )
            NetworkResult.Success(NoDataReturned())
        } catch (e: Exception) {
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
        }
    }

    override suspend fun fetchUserBookShelves(): NetworkResult<List<BookShelf>> {
        syncLocalBookshelvesDb()
        return NetworkResult.Success(bookshelfDao.getAllBookshelves().map {
            it.toBookshelf()
            }
        )
    }


    override suspend fun fetchBookshelfRef(bookshelfId: Int): NetworkResult<BookshelfRef> {
       return bookshelfNetworkClass.fetchBookshelfRef(bookshelfId).also { result ->
           if (result is NetworkResult.Error) {
               Toast.makeText(context, "${'$'}{result.message} ${'$'}{result.hint}", Toast.LENGTH_SHORT).show()
           }
       }

    }

    override suspend fun editBookshelf(bookshelfId: Int, bookshelf: BookshelfRef): NetworkResult<NoDataReturned> {
        return bookshelfNetworkClass.updateBookshelf(bookshelfId, bookshelf).also { result ->
            if (result is NetworkResult.Error) {
                Toast.makeText(context, "${'$'}{result.message} ${'$'}{result.hint}", Toast.LENGTH_SHORT).show()
            }else{
                syncLocalBookshelvesDb()
            }
        }


    }

    override suspend fun fetchBookshelfById(bookshelfId: Int): NetworkResult<Flow<BookshelfEntity>> {
        return NetworkResult.Success(bookshelfDao.getBookshelfById(bookshelfId))
    }

    override suspend fun addBookToBookshelf(bookshelfId: Int, bookId: String): NetworkResult<NoDataReturned> {
        return bookshelfNetworkClass.addBookToBookshelf(bookshelfId, bookId).also { result ->
            if (result is NetworkResult.Error) {
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT).show()
            }else{
                syncLocalBookshelvesDb()
            }
        }
    }


    override suspend fun removeBookFromBookshelf(bookId: String, bookshelfId: Int):NetworkResult<NoDataReturned>  {
        return bookshelfNetworkClass.removeBookFromBookshelf(bookId, bookshelfId). also {result ->
            if (result is NetworkResult.Error){
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override suspend fun deleteBookshelf(bookshelfId: Int): NetworkResult<NoDataReturned> {
        return bookshelfNetworkClass.deleteBookshelf(bookshelfId).also { result ->
            if (result is NetworkResult.Error) {
                Toast.makeText(context, "${'$'}{result.message} ${'$'}{result.hint}", Toast.LENGTH_SHORT).show()
            }else{
                syncLocalBookshelvesDb()
            }
        }

    }


    companion object {
        const val TAG = "BookshelfRepositoryImpl"
    }
}