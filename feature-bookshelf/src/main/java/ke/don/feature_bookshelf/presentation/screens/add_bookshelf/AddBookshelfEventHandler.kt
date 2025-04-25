package ke.don.feature_bookshelf.presentation.screens.add_bookshelf

import ke.don.shared_domain.data_models.BookshelfType

sealed class AddBookshelfEventHandler {
    data class FetchBookshelf(val id: Int?): AddBookshelfEventHandler()

    data class OnNameChange(val name: String): AddBookshelfEventHandler()

    data class OnDescriptionChange(val description: String): AddBookshelfEventHandler()

    data class OnBookshelfTypeChange(val bookshelfType: BookshelfType): AddBookshelfEventHandler()

    data class OnSubmit(val onNavigateBack: () -> Unit): AddBookshelfEventHandler()

    data object OnCleared: AddBookshelfEventHandler()
}