package ke.don.shared_domain.states

import ke.don.shared_domain.data_models.BookShelf

data class BookshelfUiState(
    val bookShelf: BookShelf = BookShelf(),
    val resultState: ResultState = ResultState.Empty,
)