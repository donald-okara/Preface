package ke.don.shared_domain.states

import ke.don.shared_domain.data_models.BookItem

sealed interface SearchState{
    data class Success(val data: List<BookItem>): SearchState
    data class Error(val message : String = "An error occurred"): SearchState
    data object Loading: SearchState
    data object Empty: SearchState
}
