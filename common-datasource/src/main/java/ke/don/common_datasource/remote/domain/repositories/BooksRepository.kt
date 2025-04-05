package ke.don.common_datasource.remote.domain.repositories

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.BookItem
import ke.don.shared_domain.states.NetworkResult
import kotlinx.coroutines.flow.Flow

interface BooksRepository {

    suspend fun searchBooks(query: String): NetworkResult<List<BookItem>>

    suspend fun getBookDetails(bookId: String): NetworkResult<BookDetailsResponse>

    suspend fun fetchBookshelves() : List<BookshelfEntity>

    suspend fun pushEditedBookshelfBooks(bookId: String, bookshelfIds: List<Int>, addBookshelves: List<AddBookToBookshelf>) : NetworkResult<NoDataReturned>
}

