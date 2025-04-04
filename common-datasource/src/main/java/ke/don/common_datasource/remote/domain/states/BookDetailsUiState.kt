package ke.don.common_datasource.remote.domain.states

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.UserProgressResponse
import ke.don.shared_domain.utils.color_utils.model.ColorPallet

data class BookUiState(
    val bookDetails: BookDetailsResponse = BookDetailsResponse(),
    val userProgressState: UserProgressState = UserProgressState(),
    val showUpdateProgressDialog: ShowOptionState = ShowOptionState(),
    val showBookshelvesDropDown: ShowOptionState = ShowOptionState(),
    val showBottomSheet: ShowOptionState = ShowOptionState(),
    val colorPallet: ColorPallet = ColorPallet(),
    val highestImageUrl: String? = null,
    val loadingJoke: String = "",
    val resultState: ResultState = ResultState.Loading,
)


data class UserProgressState(
    val bookProgress: UserProgressResponse = UserProgressResponse(),
    val isPresent : Boolean = false,
    val isError: Boolean = true,
    val newProgress: Int = 0,
)

data class BookshelfBookDetailsState(
    val bookshelfBookDetails: BookshelfEntity = BookshelfEntity(),
    val isSelected: Boolean = false,
    val isBookPresent: Boolean = false,
)

data class BookshelvesState(
    val bookshelves: List<BookshelfBookDetailsState> = emptyList(),
)

data class ShowOptionState(
    val showOption : Boolean = false,
    val isLoading : Boolean = false,
)


fun BookshelfEntity.toAddBookToBookshelf(
    book : BookDetailsResponse
): AddBookToBookshelf {
    val lowestImageUrl = book.volumeInfo.imageLinks.let {
        it.extraLarge ?: it.large ?: it.medium ?: it.small ?: it.thumbnail
        ?: it.smallThumbnail
    }?.replace("http", "https")
    val highestImageUrl = book.volumeInfo.imageLinks.let {
        it.extraLarge ?: it.large ?: it.medium ?: it.small ?: it.thumbnail
        ?: it.smallThumbnail
    }?.replace("http", "https")

    return AddBookToBookshelf(
        bookshelfId = this.id,
        bookId = book.id,
        title = book.volumeInfo.title,
        description = book.volumeInfo.description,
        highestImageUrl = highestImageUrl,
        lowestImageUrl = lowestImageUrl,
        authors = book.volumeInfo.authors,
        categories = book.volumeInfo.categories,
        publishedDate = book.volumeInfo.publishedDate,
        publisher = book.volumeInfo.publisher,
        maturityRating = book.volumeInfo.maturityRating,
        language = book.volumeInfo.language,
        previewLink = book.volumeInfo.previewLink,
        pageCount = book.volumeInfo.pageCount
    )
}

