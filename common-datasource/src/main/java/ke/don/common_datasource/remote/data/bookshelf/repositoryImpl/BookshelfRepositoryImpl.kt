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
        return when (val result= bookshelfNetworkClass.createBookshelf(bookshelf)) {
            is NetworkResult.Error -> {
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT).show()
                NetworkResult.Error(
                    message = result.message,
                    code = result.code,
                    details = result.details,
                    hint = result.hint
                )
            }

            is NetworkResult.Success -> {
                userProfile?.authId?.let {
                    fetchUserBookShelves()
                }
                 NetworkResult.Success(NoDataReturned())
            }
        }
    }

    suspend fun syncLocalBookshelvesDb(): NetworkResult<NoDataReturned>{
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
                val remoteIds = remoteBookshelves.result.map { it.id }.toSet() // Convert to set for fast lookup
                bookshelfDao.deleteBookshelvesNotIn(remoteIds)

                // Insert/update fetched bookshelves
                bookshelfDao.insertAll(remoteBookshelves.result)

                NetworkResult.Success(NoDataReturned())
            }
        }

    }

    override suspend fun fetchUserBookShelves(): NetworkResult<Flow<List<BookShelf>>> {
        return when (val remoteBookshelves = syncLocalBookshelvesDb()){
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
                NetworkResult.Success(bookshelfDao.getAllBookshelvesFlow()
                    .map { list -> list.map { it.toBookshelf() } })
            }
        }
    }


    override suspend fun fetchBookshelfRef(bookshelfId: Int): NetworkResult<BookshelfRef> {
        return when(val result = bookshelfNetworkClass.fetchBookshelfRef(bookshelfId)){
            is NetworkResult.Error -> {
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT).show()
                NetworkResult.Error(
                    message = result.message,
                    code = result.code,
                    details = result.details,
                    hint = result.hint)
            }
            is NetworkResult.Success -> {
                NetworkResult.Success(result.result)
            }
        }
    }

    override suspend fun editBookshelf(bookshelfId: Int, bookshelf: BookshelfRef): NetworkResult<NoDataReturned> {
      return when(val result = bookshelfNetworkClass.updateBookshelf(bookshelfId, bookshelf)){
         is NetworkResult.Error -> {
             Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT).show()

             NetworkResult.Error(
                 message = result.message,
                 code = result.code,
                 details = result.details,
                 hint = result.hint
             )
         }

          is NetworkResult.Success -> {
              bookshelfDao.update(bookshelfEntity = bookshelf.toEntity())
              NetworkResult.Success(NoDataReturned())
          }

      }
//
//        return try {
//            if(bookshelfNetworkClass.updateBookshelf(bookshelfId =  bookshelfId, bookshelf = bookshelf)== ResultState.Success){
//                bookshelfDao.update(bookshelfEntity = bookshelf.toEntity())
//            }
//           ResultState.Success

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

    override suspend fun addBookToBookshelf(addBookToBookshelf: AddBookToBookshelf):ResultState {
        return try {
            Log.d(TAG, "Attempting to add book")
            bookshelfNetworkClass.addBookToBookshelf(addBookToBookshelf)
            ResultState.Success
        }catch (e: Exception){
            e.printStackTrace()
            ResultState.Error(e.message.toString())
        }
    }

    override suspend fun removeBookFromBookshelf(bookId: String, bookshelfId: Int):ResultState {
        return try {
            bookshelfNetworkClass.removeBookFromBookshelf(bookId, bookshelfId)
            ResultState.Success
        }catch (e: Exception){
            e.printStackTrace()
            ResultState.Error(e.message.toString())
        }
    }

    override suspend fun deleteBookshelf(bookshelfId: Int): NetworkResult<NoDataReturned> {
        return when(val result = bookshelfNetworkClass.deleteBookshelf(bookshelfId)){
            is NetworkResult.Error -> {
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT)
                    .show()
                NetworkResult.Error(message = result.message, hint = result.hint, details = result.details)
            }
            is NetworkResult.Success -> {
                bookshelfDao.deleteBookshelfById(bookshelfId)
                NetworkResult.Success(NoDataReturned())
            }
        }
//        try {
//            if(bookshelfNetworkClass.deleteBookshelf(bookshelfId) == ResultState.Success){
//                bookshelfDao.deleteBookshelfById(bookshelfId)
//                return ResultState.Success
//            }else{
//                return ResultState.Error("Something went wrong")
//            }
//        }catch (e: Exception) {
//            e.printStackTrace()
//            return ResultState.Error(e.message.toString())
//        }
    }


    companion object {
        const val TAG = "BookshelfRepositoryImpl"
    }
}