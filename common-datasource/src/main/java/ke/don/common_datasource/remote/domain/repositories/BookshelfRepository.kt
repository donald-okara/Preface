package ke.don.common_datasource.remote.domain.repositories

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.states.NetworkResult
import kotlinx.coroutines.flow.Flow

interface BookshelfRepository {
    suspend fun createBookshelf(bookshelf: BookshelfRef): NetworkResult<BookshelfRef>
    suspend fun fetchBookshelfById(bookshelfId: Int): NetworkResult<BookshelfEntity>
    suspend fun fetchUserBookShelves():NetworkResult<List<BookShelf>>
    suspend fun syncLocalBookshelvesDb():NetworkResult<NoDataReturned>
    suspend fun editBookshelf(bookshelfId: Int, bookshelf: BookshelfRef): NetworkResult<NoDataReturned>
    suspend fun fetchBookshelfRef(bookshelfId: Int): NetworkResult<BookshelfRef>
    suspend fun addBookToBookshelf(bookshelfId: Int, bookId: String):NetworkResult<NoDataReturned>
    suspend fun removeBookFromBookshelf(bookId: String, bookshelfId: Int): NetworkResult<NoDataReturned>
    suspend fun deleteBookshelf(bookshelfId: Int): NetworkResult<NoDataReturned>

}