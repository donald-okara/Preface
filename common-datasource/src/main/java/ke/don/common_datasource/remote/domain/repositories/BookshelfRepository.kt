package ke.don.common_datasource.remote.domain.repositories

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.remote.domain.states.BookshelfUiState
import ke.don.common_datasource.remote.domain.states.UserLibraryState
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.EmptyResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BookshelfRepository {

    suspend fun createBookshelf(bookshelf: BookshelfRef): EmptyResultState
    suspend fun fetchBookshelfById(bookshelfId: Int): Flow<BookshelfEntity>
    suspend fun fetchUserBookShelves():Flow<List<BookShelf>>
    suspend fun editBookshelf(bookshelfId: Int, bookshelf: BookshelfRef): EmptyResultState
    suspend fun fetchBookshelfRef(bookshelfId: Int): BookshelfRef?
    suspend fun addBookToBookshelf(addBookToBookshelf: AddBookToBookshelf):EmptyResultState
    suspend fun removeBookFromBookshelf(bookId: String, bookshelfId: Int): EmptyResultState
    suspend fun deleteBookshelf(bookshelfId: Int): EmptyResultState

}