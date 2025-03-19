package ke.don.common_datasource.remote.domain.states

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.SupabaseBook
import ke.don.shared_domain.utils.color_utils.model.ColorPallet

data class BookUiState(
    val bookDetails: BookDetailsResponse = BookDetailsResponse(),
    val colorPallet: ColorPallet = ColorPallet(),
    val highestImageUrl: String? = null,
    val bookshelvesState: BookshelvesState = BookshelvesState(),
    val resultState: ResultState = ResultState.Empty,
    val pushSuccess: Boolean = false
)


data class BookshelfBookDetailsState(
    val bookshelfBookDetails: BookshelfEntity = BookshelfEntity(),
    val isSelected: Boolean = false,
    val isBookPresent: Boolean = false,
)

//
//fun BookshelfRef.toBookshelfBookDetails(
//    bookshelf: BookshelfRef,
//    isBookPresent: Boolean = false
//): BookshelfBookDetailsState {
//    return BookshelfBookDetailsState(
//        bookshelfBookDetails = bookshelf,
//        isBookPresent = isBookPresent
//    )
//}

fun BookShelf.toBookshelfRef(): BookshelfRef{
    return BookshelfRef(
        id = this.supabaseBookShelf.id,
        name = this.supabaseBookShelf.name,
        description = this.supabaseBookShelf.description,
        userId = this.supabaseBookShelf.userId
    )
}



//fun BookShelf.toBookshelfBookDetails(
//    bookshelf: BookShelf,
//    isBookPresent: Boolean = false
//): BookshelfBookDetailsState {
//    return BookshelfBookDetailsState(
//        bookshelfBookDetails = bookshelf.supabaseBookShelf,
//        isBookPresent = isBookPresent
//    )
//}

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
        authors = bookUiState.bookDetails.volumeInfo.authors,
        categories = bookUiState.bookDetails.volumeInfo.categories,
        publishedDate = bookUiState.bookDetails.volumeInfo.publishedDate,
        publisher = bookUiState.bookDetails.volumeInfo.publisher,
        maturityRating = bookUiState.bookDetails.volumeInfo.maturityRating,
        language = bookUiState.bookDetails.volumeInfo.language,
        previewLink = bookUiState.bookDetails.volumeInfo.previewLink
    )
}

fun BookUiState.toSupabaseBook(): SupabaseBook{
    return SupabaseBook(
        bookId = this.bookDetails.id,
        title = this.bookDetails.volumeInfo.title,
        description = this.bookDetails.volumeInfo.description,
        highestImageUrl = this.highestImageUrl,
        lowestImageUrl = this.bookDetails.volumeInfo.imageLinks.let {
            it.extraLarge ?: it.large ?: it.medium ?: it.small ?: it.thumbnail
            ?: it.smallThumbnail
        }?.replace("http", "https"),
        authors = this.bookDetails.volumeInfo.authors,
        categories = this.bookDetails.volumeInfo.categories,
        publishedDate = this.bookDetails.volumeInfo.publishedDate,
        publisher = this.bookDetails.volumeInfo.publisher,
        maturityRating = this.bookDetails.volumeInfo.maturityRating,
        language = this.bookDetails.volumeInfo.language,
        previewLink = this.bookDetails.volumeInfo.previewLink
    )
}

fun AddBookToBookshelf.toSupabaseBook(): SupabaseBook{
    return SupabaseBook(
        bookId = this.bookId,
        title = this.title,
        description = this.description,
        highestImageUrl = this.highestImageUrl,
        lowestImageUrl = this.lowestImageUrl,
        authors = this.authors,
        categories = this.categories,
        publishedDate = this.publishedDate,
        publisher = this.publisher,
        maturityRating = this.maturityRating,
        language = this.language,
        previewLink = this.previewLink
    )
}


data class BookshelvesState(
    val bookshelves: List<BookshelfBookDetailsState> = emptyList(),
)