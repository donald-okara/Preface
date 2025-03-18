package ke.don.common_datasource.remote.domain.repositories

import ke.don.common_datasource.remote.domain.states.BookshelfUiState
import ke.don.common_datasource.remote.domain.states.UserLibraryState
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.EmptyResultState
import kotlinx.coroutines.flow.StateFlow

interface BookshelfRepository {
    val addBookshelfState: StateFlow<AddBookshelfState>
    val userLibraryState: StateFlow<UserLibraryState>
    val bookshelfUiState: StateFlow<BookshelfUiState>

    suspend fun createBookshelf(bookshelf: BookshelfRef): EmptyResultState
    suspend fun fetchBookshelfById(bookshelfId: Int)
    suspend fun fetchUserBookShelves()
    suspend fun editBookshelf(bookshelfId: Int, bookshelf: BookshelfRef): EmptyResultState
    suspend fun fetchBookshelfRef(bookshelfId: Int): BookshelfRef?
    suspend fun addBookToBookshelf(addBookToBookshelf: AddBookToBookshelf):EmptyResultState
    suspend fun removeBookFromBookshelf(bookId: String, bookshelfId: Int): EmptyResultState
    suspend fun deleteBookshelf(bookshelfId: Int): EmptyResultState

}