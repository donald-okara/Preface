package ke.don.feature_book_details.presentation.screens.book_details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.states.BookUiState
import ke.don.common_datasource.remote.domain.states.BookshelvesState
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.common_datasource.remote.domain.states.ShowOptionState
import ke.don.common_datasource.remote.domain.states.UserProgressState
import ke.don.common_datasource.remote.domain.usecases.BooksUseCases
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.CreateUserProgressDTO
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.loadingBookJokes
import ke.don.shared_domain.utils.color_utils.ColorPaletteExtractor
import ke.don.shared_domain.utils.color_utils.model.ColorPallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
     private val colorPaletteExtractor: ColorPaletteExtractor,
    private val booksUseCases : BooksUseCases
) : ViewModel() {

    private val _bookState = MutableStateFlow(BookUiState())
    val bookState: StateFlow<BookUiState> = _bookState

    private var initialBookshelfState = BookshelvesState()
    /**
     * BookDetails
     */
    private fun updateBookState(newState: BookUiState) {
        _bookState.update { newState }
    }

    fun fetchAndUpdateBookUiState(volumeId: String) {
        viewModelScope.launch {
            val userId = booksUseCases.fetchProfileId()
            updateBookState(
                BookUiState(
                    volumeId = volumeId,
                    userId = userId
                )
            )
            try {
                Log.d(TAG, "Attempting to fetch bookUiState",)



                val bookDetailsResult = fetchBookDetails()

                if (bookDetailsResult == null){
                    updateBookState(BookUiState(resultState = ResultState.Error("Failed to load book data")))
                    return@launch
                }else{
                    val bookCheckResult = booksUseCases.checkAndAddBook(bookDetailsResult)
                    if (bookCheckResult is NetworkResult.Error){
                        updateBookState(BookUiState(resultState = ResultState.Error("Failed to load book data")))
                        return@launch
                    }else(
                        updateBookState(
                            BookUiState(
                                resultState = ResultState.Success
                            )
                        )
                    )
                }


                val highestImageUrl = bookDetailsResult.volumeInfo.imageLinks.getHighestQualityUrl()?.replace("http", "https") ?: ""
                val colorPallet = if (highestImageUrl.isNotEmpty()) {
                    withContext(Dispatchers.Default) {
                        colorPaletteExtractor.extractColorPalette(highestImageUrl)
                    }
                } else {
                    ColorPallet() // Default fallback
                }

                updateBookState(
                    BookUiState(
                        bookDetails = bookDetailsResult,
                        highestImageUrl = highestImageUrl,
                        colorPallet = colorPallet
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching book data", e)
                // Optionally update state to reflect error
                updateBookState(BookUiState(resultState = ResultState.Error("Failed to load book data")))
            }
        }
    }

    private suspend fun fetchBookDetails(): BookDetailsResponse? {
        return when (val response = bookState.value.volumeId?.let { booksUseCases.fetchBookDetails(it) }) {
            is NetworkResult.Error -> null

            is NetworkResult.Success -> response.data
            null -> null
        }
    }


    fun refreshAction() = viewModelScope.launch {
        onLoading()
        bookState.value.volumeId?.let {
            fetchAndUpdateBookUiState(it)
        }
    }


    fun onSearchAuthor(author: String) {
//        booksUseCases.onSearchQueryChange(author)
//        viewModelScope.launch {
//            booksUseCases.onSearch()
//        }
        TODO()
    }

    private fun onLoading() {
        updateBookState(
            BookUiState(
                loadingJoke = loadingBookJokes[Random.nextInt(loadingBookJokes.size)]
            )
        )
    }

    /**
     * UserProgress
     */
    private fun updateProgressState(newState: UserProgressState){
        updateBookState(
            BookUiState(
                userProgressState = newState
            )
        )
    }

    fun onCurrentPageUpdate(progress: Int){
        if (progress <= bookState.value.bookDetails.volumeInfo.pageCount){
            updateBookState(
                BookUiState(
                    userProgressState = UserProgressState(isError = true)
                )
            )
        }else {
            updateBookState(
                BookUiState(
                    userProgressState = UserProgressState(isError = false)
                )
            )
        }

        updateBookState(
            BookUiState(
                userProgressState = UserProgressState(newProgress = progress)
            )
        )
    }

    fun onProgressTabNav() {
        fetchUserProgress()
    }

    private fun fetchUserProgress() {
        viewModelScope.launch {
            val state = bookState.value
            if (state.userProgressState.resultState !is ResultState.Success &&
                state.volumeId != null &&
                state.userId != null
            ) {
                try {
                    val newProgressState = booksUseCases.fetchBookProgress(
                        userId = state.userId!!,
                        bookId = state.volumeId!!
                    )
                    updateProgressState(
                        newProgressState ?: UserProgressState(resultState = ResultState.Error())
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching bookshelves: ${e.message}", e)
                    updateProgressState(
                        UserProgressState(resultState = ResultState.Error("Failed to fetch bookshelves"))
                    )
                }
            }
        }
    }

    fun updateProgressDialogState(
        isLoading: Boolean? = null,
        toggle: Boolean = false
    ) {
        val currentState = _bookState.value.showUpdateProgressDialog

        updateBookState(
            BookUiState(
                showUpdateProgressDialog = _bookState.value.showUpdateProgressDialog.copy(
                    isLoading = isLoading ?: currentState.isLoading,
                    showOption = if (toggle) !currentState.showOption else currentState.showOption
                )
            )
        )
    }

    fun onSaveBookProgress() {
        viewModelScope.launch {
            // Show loading
            updateProgressDialogState(
                isLoading = true
            )

            updateProgressState(
                UserProgressState(
                    resultState = ResultState.Loading
                )
            )

            val userId = bookState.value.userId?: return@launch
            val bookId = bookState.value.volumeId?: return@launch
            val newPage = _bookState.value.userProgressState.newProgress
            val totalPages = _bookState.value.bookDetails.volumeInfo.pageCount

            val result: NetworkResult<NoDataReturned> = if (_bookState.value.userProgressState.isPresent) {
                booksUseCases.updateUserProgress(bookId = bookId, userId = userId, newCurrentPage = newPage)
            } else {
                val dto = CreateUserProgressDTO(bookId, newPage, totalPages)
                booksUseCases.addUserProgress(dto)
            }

            if (result is NetworkResult.Success){
                fetchUserProgress()
            }

            // Hide loading
            updateProgressDialogState(isLoading = false, toggle = true)
        }
    }

    /**
     * Bookshelf
     */
    private fun updateBookshelvesState(newState: BookshelvesState) {
        updateBookState(
            BookUiState(bookshelvesState = newState)
        )
    }

    fun onSelectBookshelf(bookshelfId: Int) = viewModelScope.launch {
        updateBookshelvesState(
            BookshelvesState(
                bookshelves = bookState.value.bookshelvesState.bookshelves.map { bookshelf ->
                    if (bookshelf.bookshelfBookDetails.id == bookshelfId) {
                        bookshelf.copy(isBookPresent = !bookshelf.isBookPresent)
                    } else {
                        bookshelf
                    }
                }

            )
        )


    }

    fun onShowBookshelves(){
        updateBookState(
            _bookState.value.copy(
                showBookshelvesDropDown = ShowOptionState(
                    showOption = !_bookState.value.showBookshelvesDropDown.showOption
                )
            )
        )
    }

    fun onShowBottomSheet() {
        // Toggle the bottom sheet visibility
        onShowBookshelves()

        // Fetch bookshelves if conditions are met
        fetchBookshelves()
    }

    private fun fetchBookshelves() {
        viewModelScope.launch {
            val currentState = bookState.value
            if (currentState.userProgressState.resultState !is ResultState.Success && currentState.volumeId != null) {
                try {
                    val newBookshelvesState = booksUseCases.fetchAndMapBookshelves(currentState.volumeId!!)
                    updateBookState(
                        BookUiState(
                            bookshelvesState = newBookshelvesState ?: BookshelvesState(resultState = ResultState.Error())
                        )
                    )
                    initialBookshelfState = bookState.value.bookshelvesState
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching bookshelves: ${e.message}", e)
                    updateBookState(
                        BookUiState(
                            bookshelvesState = BookshelvesState(resultState = ResultState.Error("Failed to fetch bookshelves"))
                        )
                    )
                }
            }
        }
    }

    fun onPushEditedBookshelfBooks() {
        viewModelScope.launch {
            updateBookState(
                BookUiState(
                    resultState = ResultState.Loading,
                    showBookshelvesDropDown = ShowOptionState(isLoading = true)
                )
            )

            val bookId = bookState.value.bookDetails.id
            val currentBookshelves = bookState.value.bookshelvesState.bookshelves
            val initialBookshelves = initialBookshelfState.bookshelves

            // Bookshelves to remove the book from
            val bookshelfIdsToRemove = initialBookshelves
                .filter { it.isBookPresent && currentBookshelves.none { cb -> cb.bookshelfBookDetails.id == it.bookshelfBookDetails.id && cb.isBookPresent } }
                .map { it.bookshelfBookDetails.id }

            // Bookshelves to add the book to
            val addBookToBookshelfList = currentBookshelves
                .filter { cb -> !initialBookshelves.any { it.bookshelfBookDetails.id == cb.bookshelfBookDetails.id && it.isBookPresent } && cb.isBookPresent }
                .map { it.bookshelfBookDetails.id }

            when (booksUseCases.pushEditedBookshelfBooks(bookId, bookshelfIdsToRemove, addBookToBookshelfList)) {
                is NetworkResult.Success -> {
                    fetchBookshelves()
                }
                is NetworkResult.Error -> {
                    updateBookState(
                        BookUiState(
                            showBookshelvesDropDown = ShowOptionState(isLoading = false)
                        )
                    )
                }
            }
        }
    }


    public override fun onCleared() {
        super.onCleared()
       _bookState.update {
            BookUiState()
        }
    }


    companion object {
        const val TAG = "BookDetailsViewModel"
    }

}