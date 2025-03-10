package ke.don.shared_domain.states

import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.utils.color_utils.model.ColorPallet

data class BookUiState(
    val bookDetails: BookDetailsResponse = BookDetailsResponse(),
    val colorPallet: ColorPallet = ColorPallet(),
    val highestImageUrl: String? = null,
    val bookshelvesState: BookshelvesState = BookshelvesState(),
    val resultState: ResultState = ResultState.Empty,
    val pushSuccess: Boolean = false
)

sealed interface ResultState {
    data object Success : ResultState
    data class Error(val message: String = "An error occurred") : ResultState
    data object Loading : ResultState
    data object Empty : ResultState
}



data class BookshelfBookDetailsState(
    val bookshelfBookDetails: BookshelfRef = BookshelfRef(),
    val isSelected: Boolean = false,
    val isBookPresent: Boolean = false,
)

fun BookshelfRef.toBookshelfBookDetails(
    bookshelf: BookshelfRef,
    isBookPresent: Boolean = false
): BookshelfBookDetailsState{
    return BookshelfBookDetailsState(
        bookshelfBookDetails = bookshelf,
        isBookPresent = isBookPresent
    )
}



fun BookShelf.toBookshelfBookDetails(
    bookshelf: BookShelf,
    isBookPresent: Boolean = false
): BookshelfBookDetailsState{
    return BookshelfBookDetailsState(
        bookshelfBookDetails = bookshelf.supabaseBookShelf,
        isBookPresent = isBookPresent
    )
}

fun BookUiState.toAddBookToBookshelf(
    bookshelfId: Int,
    bookUiState: BookUiState
): AddBookToBookshelf {
    val highestImageUrl = bookUiState.highestImageUrl
    val lowestImageUrl = bookUiState.bookDetails.volumeInfo.imageLinks.let {
        it.extraLarge ?: it.large ?: it.medium ?: it.small ?: it.thumbnail
        ?: it.smallThumbnail
    }?.replace("http", "https")

    return AddBookToBookshelf(
        bookshelfId = bookshelfId,
        bookId = bookUiState.bookDetails.id,
        title = bookUiState.bookDetails.volumeInfo.title,
        description = bookUiState.bookDetails.volumeInfo.description,
        highestImageUrl = highestImageUrl,
        lowestImageUrl = lowestImageUrl,
    )
}

data class BookshelvesState(
    val bookshelves: List<BookshelfBookDetailsState> = emptyList(),
)