package ke.don.shared_domain.states

import ke.don.shared_domain.data_models.BookshelfType
import ke.don.shared_domain.data_models.SupabaseBookshelf

data class AddBookshelfState(
    val name: String = "",
    val description: String = "",
    val bookshelfType: BookshelfType = BookshelfType.GENERAL,
    val successState: SuccessState = SuccessState.IDLE,
)

enum class SuccessState{
    SUCCESS,
    ERROR,
    LOADING,
    IDLE
}

fun AddBookshelfState.toBookshelf(): SupabaseBookshelf {
    return SupabaseBookshelf(
        name = name,
        description = description,
        bookshelfType = bookshelfType.name
    )
}