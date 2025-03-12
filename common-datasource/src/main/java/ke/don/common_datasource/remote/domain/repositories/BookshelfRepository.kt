package ke.don.common_datasource.remote.domain.repositories

import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.BookshelfType
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.BookshelfUiState
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.UserLibraryState
import kotlinx.coroutines.flow.StateFlow

interface BookshelfRepository {
    val addBookshelfState: StateFlow<AddBookshelfState>
    val userLibraryState: StateFlow<UserLibraryState>
    val bookshelfUiState: StateFlow<BookshelfUiState>

    fun onNameChange(name: String)
    fun onDescriptionChange(description: String)
    fun onBookshelfTypeChange(bookshelfType: BookshelfType)

    suspend fun createBookshelf(bookshelf: BookshelfRef)
    suspend fun fetchBookshelfById(bookshelfId: Int)
    suspend fun fetchUserBookShelves()
    suspend fun editBookshelf(bookshelfId: Int, bookshelf: BookshelfRef)
    suspend fun fetchBookshelfRef(bookshelfId: Int)
    suspend fun addBookToBookshelf(addBookToBookshelf: AddBookToBookshelf)
    suspend fun removeBookFromBookshelf(bookId: String, bookshelfId: Int)
    suspend fun deleteBookshelf(bookshelfId: Int): ResultState

}