package ke.don.feature_book_details.presentation.screens.book_details

sealed class BookDetailsEvent{
    data class VolumeIdPassed(val volumeId: String) : BookDetailsEvent()

    data object Refresh: BookDetailsEvent()

    data object ChangeLoadingJoke: BookDetailsEvent()

    data class ChangeCurrentPage(val newPage: Int): BookDetailsEvent()

    data object OnNavigateToProgressTab: BookDetailsEvent()

    data class ToggleUpdateProgressDialog(val isLoading: Boolean? = null, val toggle: Boolean = false) : BookDetailsEvent()

    data object SaveBookProgress: BookDetailsEvent()

    data class SelectBookshelf(val bookshelfId: Int): BookDetailsEvent()

    data object ToggleBookshelfDropDown: BookDetailsEvent()

    data object FetchBookshelves: BookDetailsEvent()

    data object FetchProgress: BookDetailsEvent()

    data object ShowBottomSheet: BookDetailsEvent()

    data object PushEditedBookshelfBooks: BookDetailsEvent()

    data object ClearState: BookDetailsEvent()
}