package ke.don.common_datasource.remote.domain.repositories

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.NetworkResult
import kotlinx.coroutines.flow.Flow

interface BookshelfRepository {

    suspend fun createBookshelf(bookshelf: BookshelfRef): NetworkResult<NoDataReturned>
    suspend fun fetchBookshelfById(bookshelfId: Int): Flow<BookshelfEntity>
    suspend fun fetchUserBookShelves():Flow<List<BookShelf>>
    suspend fun editBookshelf(bookshelfId: Int, bookshelf: BookshelfRef): NetworkResult<NoDataReturned>
    suspend fun fetchBookshelfRef(bookshelfId: Int): BookshelfRef?
    suspend fun addBookToBookshelf(addBookToBookshelf: AddBookToBookshelf):ResultState
    suspend fun removeBookFromBookshelf(bookId: String, bookshelfId: Int): ResultState
    suspend fun deleteBookshelf(bookshelfId: Int): ResultState

}