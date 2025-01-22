package ke.don.feature_book_details.domain.repositories

import ke.don.feature_book_details.data.model.BookListItemResponse
import ke.don.feature_book_details.domain.states.BookUiState
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response

interface BooksRepository {
    val bookUiState : StateFlow<BookUiState>

    suspend fun searchBooks(query: String): Response<BookListItemResponse>

    suspend fun getBookDetails(bookId: String)

    fun updateBookState(newState: BookUiState)

}

