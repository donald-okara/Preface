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
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.NetworkResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BookshelfRepositoryImpl(
    private val bookshelfNetworkClass: BookshelfNetworkClass,
    private val userProfile : Profile?,
    private val bookshelfDao: BookshelfDao,
    private val context : Context
): BookshelfRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch{
            Log.d(TAG, "userProfile: $userProfile")
            userProfile?.authId?.let {
                fetchUserBookShelves()
            }
        }
    }

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
    override suspend fun fetchUserBookShelves(): Flow<List<BookShelf>> {
        val remoteBookshelves = bookshelfNetworkClass.fetchUserBookshelves(userProfile?.authId!!)

        // Perform database updates in a transaction for consistency
        // Delete bookshelves not in the remote list
        val remoteIds = remoteBookshelves.map { it.id }.toSet() // Convert to set for fast lookup
        bookshelfDao.deleteBookshelvesNotIn(remoteIds)

        // Insert/update fetched bookshelves
        bookshelfDao.insertAll(remoteBookshelves)

        return bookshelfDao.getAllBookshelvesFlow()
            .map { list -> list.map { it.toBookshelf() } }
    }


    override suspend fun fetchBookshelfRef(bookshelfId: Int): BookshelfRef? {
        return bookshelfNetworkClass.fetchBookshelfRef(bookshelfId)
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

    override suspend fun fetchBookshelfById(bookshelfId: Int): Flow<BookshelfEntity> =
        bookshelfDao.getBookshelfById(bookshelfId)

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

    override suspend fun deleteBookshelf(bookshelfId: Int): ResultState {
        try {
            if(bookshelfNetworkClass.deleteBookshelf(bookshelfId) == ResultState.Success){
                bookshelfDao.deleteBookshelfById(bookshelfId)
                return ResultState.Success
            }else{
                return ResultState.Error("Something went wrong")
            }
        }catch (e: Exception) {
            e.printStackTrace()
            return ResultState.Error(e.message.toString())
        }
    }


    companion object {
        const val TAG = "BookshelfRepositoryImpl"
    }
}