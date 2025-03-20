package ke.don.common_datasource.remote.domain.repositories

import ke.don.common_datasource.remote.domain.states.BookUiState
import ke.don.shared_domain.data_models.BookItem
import ke.don.shared_domain.states.NetworkResult
import kotlinx.coroutines.flow.StateFlow

interface BooksRepository {
    val bookUiState : StateFlow<BookUiState>

    suspend fun searchBooks(query: String): NetworkResult<List<BookItem>>

    suspend fun getBookDetails(bookId: String)

    fun updateBookState(newState: BookUiState)

    fun onBookshelfSelected(bookshelfId: Int)

    suspend fun pushEditedBookshelfBooks() : Boolean
}

