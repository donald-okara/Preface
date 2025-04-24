package ke.don.feature_bookshelf.presentation.screens.bookshelf_details

sealed class BookshelfEventHandler {
    data class FetchBookshelf(val bookShelfId: Int) : BookshelfEventHandler()

    data class DeleteBookshelf(val onNavigateBack: () -> Unit, val bookShelfId: Int) : BookshelfEventHandler()

    data object ToggleBottomSheet: BookshelfEventHandler()
}