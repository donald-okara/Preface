package ke.don.common_datasource.remote.domain.states

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.SuccessState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.Serializable

data class UserLibraryState(
    val userBookshelves: List<BookShelf> = emptyList(),
    val successState: SuccessState = SuccessState.IDLE,
    val selectedBookshelfId: Int? = null,
    val isRefreshing: Boolean = true,
    val showOptionsSheet: Boolean = false
)

data class BookshelfUiState(
    val bookshelfId: Int? =null,
    val bookShelf: BookshelfEntity = BookshelfEntity(),
    val resultState: ResultState = ResultState.Empty,
    val showOptionsSheet: Boolean = false,

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

@Serializable
class NoDataReturned()