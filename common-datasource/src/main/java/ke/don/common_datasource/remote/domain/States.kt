package ke.don.common_datasource.remote.domain

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.local.roomdb.entities.toBookshelf
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.states.BookshelfBookDetailsState
import ke.don.shared_domain.states.SuccessState
import ke.don.shared_domain.states.toBookshelfRef

data class UserLibraryState(
    val userBookshelves: List<BookShelf> = emptyList(),
    val successState: SuccessState = SuccessState.IDLE,
)

fun BookshelfEntity.toBookshelfBookDetails(
    isBookPresent: Boolean
): BookshelfBookDetailsState {
    return BookshelfBookDetailsState(
        bookshelfBookDetails = this.toBookshelf().toBookshelfRef(),
        isBookPresent = isBookPresent
    )
}