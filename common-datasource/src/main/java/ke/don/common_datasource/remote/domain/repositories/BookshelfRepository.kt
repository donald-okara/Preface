package ke.don.common_datasource.remote.domain.repositories

import ke.don.common_datasource.remote.domain.states.BookshelfUiState
import ke.don.common_datasource.remote.domain.states.UserLibraryState
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.BookshelfType
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.ResultState
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
    suspend fun addBookToBookshelf(addBookToBookshelf: AddBookToBookshelf):ResultState
    suspend fun removeBookFromBookshelf(bookId: String, bookshelfId: Int): ResultState
    suspend fun deleteBookshelf(bookshelfId: Int): ResultState

}