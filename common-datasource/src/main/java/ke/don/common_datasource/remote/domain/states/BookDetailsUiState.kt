package ke.don.common_datasource.remote.domain.states

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.SupabaseBook
import ke.don.shared_domain.data_models.UserProgressResponse
import ke.don.shared_domain.utils.color_utils.model.ColorPallet

data class BookUiState(
    val bookDetails: BookDetailsResponse = BookDetailsResponse(),
    val userProgressState: UserProgressState = UserProgressState(),
    val colorPallet: ColorPallet = ColorPallet(),
    val highestImageUrl: String? = null,
    val resultState: ResultState = ResultState.Loading,
)


data class UserProgressState(
    val bookProgress: UserProgressResponse = UserProgressResponse(),
    val isPresent : Boolean = false,
    val isError: Boolean = true,
    val newProgress: Int = 0,
    val showUpdateProgressDialog: ShowOptionState = ShowOptionState()
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
        previewLink = bookUiState.bookDetails.volumeInfo.previewLink,
        pageCount = bookUiState.bookDetails.volumeInfo.pageCount
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
        previewLink = this.bookDetails.volumeInfo.previewLink,
        pageCount = this.bookDetails.volumeInfo.pageCount
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
        previewLink = this.previewLink,
        pageCount = this.pageCount
    )
}

fun BookDetailsResponse.toAddBookToBookshelf(
    bookshelfId : Int
): AddBookToBookshelf{
    val lowestImageUrl = this.volumeInfo.imageLinks.let {
        it.extraLarge ?: it.large ?: it.medium ?: it.small ?: it.thumbnail
        ?: it.smallThumbnail
    }?.replace("http", "https")
    val highestImageUrl = this.volumeInfo.imageLinks.let {
        it.extraLarge ?: it.large ?: it.medium ?: it.small ?: it.thumbnail
        ?: it.smallThumbnail
    }?.replace("http", "https")

    return AddBookToBookshelf(
        bookshelfId = bookshelfId,
        bookId = this.id,
        title = this.volumeInfo.title,
        description = this.volumeInfo.description,
        highestImageUrl = highestImageUrl,
        lowestImageUrl = lowestImageUrl,
        authors = this.volumeInfo.authors,
        categories = this.volumeInfo.categories,
        publishedDate = this.volumeInfo.publishedDate,
        publisher = this.volumeInfo.publisher,
        maturityRating = this.volumeInfo.maturityRating,
        language = this.volumeInfo.language,
        previewLink = this.volumeInfo.previewLink,
        pageCount = this.volumeInfo.pageCount
    )
}

fun Boolean?.orFalse(): Boolean = this ?: false
fun Boolean?.orTrue(): Boolean = this ?: true

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

