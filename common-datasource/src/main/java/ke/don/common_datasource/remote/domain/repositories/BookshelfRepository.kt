package ke.don.common_datasource.remote.domain.repositories

import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.data_models.BookshelfType
import ke.don.shared_domain.data_models.SupabaseBookshelf
import ke.don.shared_domain.states.UserLibraryState
import kotlinx.coroutines.flow.StateFlow

interface BookshelfRepository {
    val addBookshelfState: StateFlow<AddBookshelfState>
    val userLibraryState: StateFlow<UserLibraryState>

    fun onNameChange(name: String)
    fun onDescriptionChange(description: String)
    fun onBookshelfTypeChange(bookshelfType: BookshelfType)

    suspend fun createBookshelf(bookshelf: SupabaseBookshelf)
    suspend fun fetchUserBookshelves(userId: String)
    suspend fun fetchBookshelfById(bookshelfId: Int): SupabaseBookshelf?

}