package ke.don.shared_domain.states

import ke.don.shared_domain.data_models.BookShelf

data class UserLibraryState(
    val userBookshelves: List<BookShelf> = emptyList(),
    val successState: SuccessState = SuccessState.IDLE,
)
