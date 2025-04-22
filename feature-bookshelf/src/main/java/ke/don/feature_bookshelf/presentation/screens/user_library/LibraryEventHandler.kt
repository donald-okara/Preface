package ke.don.feature_bookshelf.presentation.screens.user_library

sealed class LibraryEventHandler {
    data object FetchBookshelves: LibraryEventHandler()

    data object RefreshAction: LibraryEventHandler()

    data class SelectBookshelf(val bookshelfId: Int?): LibraryEventHandler()

    data class DeleteBookshelf(val bookshelfId: Int): LibraryEventHandler()

    data class ToggleBottomSheet(val newState: Boolean): LibraryEventHandler()
}