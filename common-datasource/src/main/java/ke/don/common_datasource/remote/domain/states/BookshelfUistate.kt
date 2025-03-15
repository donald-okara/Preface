package ke.don.common_datasource.remote.domain.states

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.local.roomdb.entities.toBookshelf
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.SuccessState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class UserLibraryState(
    val userBookshelves: Flow<List<BookShelf>> = emptyFlow(),
    val successState: SuccessState = SuccessState.IDLE,
)

data class BookshelfUiState(
    val bookShelf: Flow<BookshelfEntity> = emptyFlow(),
    val resultState: ResultState = ResultState.Empty,
)

fun BookshelfEntity.toBookshelfBookDetails(
    isBookPresent: Boolean
): BookshelfBookDetailsState {
    return BookshelfBookDetailsState(
        bookshelfBookDetails = this,
        isBookPresent = isBookPresent
    )
}

fun BookshelfRef.toEntity(): BookshelfEntity {
    return BookshelfEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        userId = this.userId,
        bookshelfType = this.bookshelfType
    )
}