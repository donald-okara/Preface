package ke.don.common_datasource.remote.data.bookshelf.repositoryImpl

import android.content.Context
import android.util.Log
import android.widget.Toast
import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.local.roomdb.entities.toBookshelf
import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.common_datasource.remote.domain.states.toEntity
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    override suspend fun syncLocalBookshelvesDb(): NetworkResult<NoDataReturned>{
        return when (val remoteBookshelves = bookshelfNetworkClass.fetchUserBookshelves(userProfile?.authId!!)){
            is NetworkResult.Error -> {
                Toast.makeText(context, "${remoteBookshelves.message} ${remoteBookshelves.hint}", Toast.LENGTH_SHORT).show()
                NetworkResult.Error(
                    message = remoteBookshelves.message,
                    code = remoteBookshelves.code,
                    details = remoteBookshelves.details,
                    hint = remoteBookshelves.hint
                )
            }
            is NetworkResult.Success ->{
                val remoteIds = remoteBookshelves.data.map { it.id }.toSet() // Convert to set for fast lookup
                bookshelfDao.deleteBookshelvesNotIn(remoteIds)

                // Insert/update fetched bookshelves
                bookshelfDao.insertAll(remoteBookshelves.data)

                NetworkResult.Success(NoDataReturned())
            }
        }

    }

    override suspend fun fetchUserBookShelves(): NetworkResult<Flow<List<BookShelf>>> {
        syncLocalBookshelvesDb()
        return NetworkResult.Success(bookshelfDao.getAllBookshelvesFlow()
            .map { list -> list.map { it.toBookshelf() } })
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
                bookshelfDao.updateBookshelf(bookshelf.toEntity())
            }
        }


    }

    override suspend fun fetchBookshelfById(bookshelfId: Int): NetworkResult<Flow<BookshelfEntity>> {
        return when(val result = syncLocalBookshelvesDb()) {
            is NetworkResult.Error -> {
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT)
                    .show()
                NetworkResult.Error(
                    message = result.message,
                    code = result.code,
                    details = result.details,
                    hint = result.hint
                )
            }

            is NetworkResult.Success -> {
                NetworkResult.Success(bookshelfDao.getBookshelfById(bookshelfId))
            }
        }
    }

    override suspend fun addBookToBookshelf(addBookToBookshelf: AddBookToBookshelf): NetworkResult<NoDataReturned> {
        return bookshelfNetworkClass.addBookToBookshelf(addBookToBookshelf).also { result ->
            if (result is NetworkResult.Error) {
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT).show()
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
            }
        }

    }


    companion object {
        const val TAG = "BookshelfRepositoryImpl"
    }
}