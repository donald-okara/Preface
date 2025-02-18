package ke.don.common_datasource.remote.domain.repositories

import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.BookshelfType
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.UserLibraryState
import kotlinx.coroutines.flow.StateFlow

interface BookshelfRepository {
    val addBookshelfState: StateFlow<AddBookshelfState>
    val userLibraryState: StateFlow<UserLibraryState>

    fun onNameChange(name: String)
    fun onDescriptionChange(description: String)
    fun onBookshelfTypeChange(bookshelfType: BookshelfType)

    suspend fun createBookshelf(bookshelf: BookshelfRef)
    suspend fun fetchBookshelfById(bookshelfId: Int): BookshelfRef?
    suspend fun fetchUserBookShelves()
    suspend fun addBookToBookshelf(addBookToBookshelf: AddBookToBookshelf)
    suspend fun removeBookFromBookshelf(bookId: String, bookshelfId: Int)
}