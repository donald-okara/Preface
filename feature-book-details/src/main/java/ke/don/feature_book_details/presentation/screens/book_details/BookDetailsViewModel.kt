package ke.don.feature_book_details.presentation.screens.book_details

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
import ke.don.shared_domain.data_models.UserProgressResponse
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

    var initialBookshelfState = BookshelvesState()

    fun onBookDetailsEvent(event: BookDetailsEvent){
        when(event){
            is BookDetailsEvent.VolumeIdPassed -> {
                updateBookState(
                    BookUiState()
                )
                loadBookDetails(event.volumeId)
            }

            is BookDetailsEvent.Refresh -> {
                refreshAction()
            }

            is BookDetailsEvent.ChangeLoadingJoke -> {
                onChangeLoadingJoke()
            }

            is BookDetailsEvent.ChangeCurrentPage -> {
                onCurrentPageUpdate(event.newPage)
            }

            is BookDetailsEvent.OnNavigateToProgressTab -> {
                onNavigateToProgressTab()
            }

            is BookDetailsEvent.ToggleUpdateProgressDialog -> {
                onToggleUpdateProgressDialog(isLoading = event.isLoading, toggle = event.toggle)
            }

            is BookDetailsEvent.SaveBookProgress -> {
                onSaveBookProgress()
            }

            is BookDetailsEvent.SelectBookshelf -> {
                onSelectBookshelf(bookshelfId = event.bookshelfId)
            }

            is BookDetailsEvent.ToggleBookshelfDropDown -> {
                onToggleBookshelfDropDown()
            }

            is BookDetailsEvent.ShowBottomSheet -> {
                onShowBottomSheet()
            }

            is BookDetailsEvent.PushEditedBookshelfBooks -> {
                onPushEditedBookshelfBooks()
            }

            is BookDetailsEvent.ClearState -> {
                onCleared()
            }

            is BookDetailsEvent.FetchBookshelves -> {
                fetchBookshelves()
            }

            is BookDetailsEvent.FetchProgress -> {
                fetchUserProgress()
            }
        }
    }
    /**
     * BookDetails
     */
    fun updateBookState(newState: BookUiState) {
        _bookState.update {
            newState
        }
    }

    fun loadBookDetails(volumeId: String) {
        viewModelScope.launch {
            val userId = booksUseCases.fetchProfileId()

            updateBookState(
                _bookState.value.copy(
                    volumeId = volumeId,
                    userId = userId
                )
            )

            try {
                val bookDetailsResult = fetchBookDetails()

                if (bookDetailsResult == null) {
                    updateBookState(_bookState.value.copy(resultState = ResultState.Error("Failed to load book data")))
                    return@launch
                }

                val bookCheckResult = booksUseCases.checkAndAddBook(bookDetailsResult)

                if (bookCheckResult is NetworkResult.Error) {
                    updateBookState(_bookState.value.copy(resultState = ResultState.Error("Failed to load book data")))
                    return@launch
                }

                updateBookState(_bookState.value.copy(resultState = ResultState.Success))

                val highestImageUrl = bookDetailsResult.volumeInfo.imageLinks
                    .getHighestQualityUrl()
                    ?.replace("http", "https") ?: ""

                val colorPallet = if (highestImageUrl.isNotEmpty()) {
                    withContext(Dispatchers.Default) {
                        colorPaletteExtractor.extractColorPalette(highestImageUrl)
                    }
                } else {
                    ColorPallet()
                }

                updateBookState(
                    _bookState.value.copy(
                        bookDetails = bookDetailsResult,
                        highestImageUrl = highestImageUrl,
                        colorPallet = colorPallet
                    )
                )

            } catch (e: Exception) {
                updateBookState(_bookState.value.copy(resultState = ResultState.Error("Failed to load book data")))
            }
        }
    }

    suspend fun fetchBookDetails(): BookDetailsResponse? {
        return when (val response = bookState.value.volumeId?.let { booksUseCases.fetchBookDetails(it) }) {
            is NetworkResult.Error -> null

            is NetworkResult.Success -> response.data
            null -> null
        }
    }


    fun refreshAction() = viewModelScope.launch {
        onChangeLoadingJoke()
        bookState.value.volumeId?.let {
            updateBookState(
                _bookState.value.copy(
                    resultState = ResultState.Loading
                )
            )
            loadBookDetails(it)
        }
    }


    fun onChangeLoadingJoke() {
        updateBookState(
            _bookState.value.copy(
                loadingJoke = loadingBookJokes[Random.nextInt(loadingBookJokes.size)]
            )
        )
    }

    /**
     * UserProgress
     */

    fun onCurrentPageUpdate(progress: Int) {
        val pageCount = bookState.value.bookDetails.volumeInfo.pageCount

        if (progress <= pageCount) {
            updateBookState(
               _bookState.value.copy(
                   userProgressState =  _bookState.value.userProgressState.copy(
                       newProgress = progress,
                       isError = false
                   )
               )
            )
        } else {
            updateBookState(
                _bookState.value.copy(
                    userProgressState = _bookState.value.userProgressState.copy(
                        isError = true
                    )
                )
            )
        }

    }

    private fun onNavigateToProgressTab() {
        fetchUserProgress()
    }

    fun fetchUserProgress() {
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

                    updateBookState(
                        _bookState.value.copy(
                            userProgressState = newProgressState ?: UserProgressState(
                                bookProgress = UserProgressResponse(
                                    totalPages = bookState.value.bookDetails.volumeInfo.pageCount,
                                ),
                                resultState = ResultState.Success,
                            )
                        )
                    )

                } catch (e: Exception) {
                    updateBookState(
                        _bookState.value.copy(
                            userProgressState = UserProgressState(resultState = ResultState.Error("Failed to fetch bookshelves"))
                        )
                    )

                }
            } else {
                println("fetchUserProgress: Either progress state is success or userId/bookId is null, skipping fetch")
            }
        }
    }

    fun onToggleUpdateProgressDialog(
        isLoading: Boolean? = null,
        toggle: Boolean = false
    ) {
        val currentState = _bookState.value.showUpdateProgressDialog

        updateBookState(
            _bookState.value.copy(
                showUpdateProgressDialog = _bookState.value.showUpdateProgressDialog.copy(
                    isLoading = isLoading ?: currentState.isLoading,
                    showOption = if (toggle) !currentState.showOption else currentState.showOption
                ),
            )
        )

    }

    fun onSaveBookProgress() {
        viewModelScope.launch {
            // Show loading
            onToggleUpdateProgressDialog(
                isLoading = true
            )

            updateBookState(
                _bookState.value.copy(
                    userProgressState = _bookState.value.userProgressState.copy(
                        resultState = ResultState.Loading
                    )
                )
            )

            val userId = bookState.value.userId
            val bookId = bookState.value.volumeId
            val newPage = _bookState.value.userProgressState.newProgress
            val totalPages = _bookState.value.bookDetails.volumeInfo.pageCount


            if (userId == null || bookId == null) {
                return@launch
            }

            val result: NetworkResult<NoDataReturned> = if (_bookState.value.userProgressState.isPresent) {
                booksUseCases.updateUserProgress(bookId = bookId, userId = userId, newCurrentPage = newPage)
            } else {
                val dto = CreateUserProgressDTO(bookId, newPage, totalPages)
                booksUseCases.addUserProgress(dto)
            }

            if (result is NetworkResult.Success) {
                fetchUserProgress()
            } else {
                updateBookState(
                    _bookState.value.copy(
                        userProgressState = _bookState.value.userProgressState.copy(
                            resultState = ResultState.Error()
                        )
                    )
                )
            }

            // Hide loading
            onToggleUpdateProgressDialog(isLoading = false, toggle = true)
        }
    }

    /**
     * Bookshelf
     */
    fun onSelectBookshelf(bookshelfId: Int) = viewModelScope.launch {
        updateBookState(
            _bookState.value.copy(
                bookshelvesState = bookState.value.bookshelvesState.copy(
                    bookshelves = bookState.value.bookshelvesState.bookshelves.map { bookshelf ->
                        if (bookshelf.bookshelfBookDetails.id == bookshelfId) {
                            bookshelf.copy(isBookPresent = !bookshelf.isBookPresent)
                        } else {
                            bookshelf
                        }
                    }
                )
            )
        )
    }

    fun onToggleBookshelfDropDown() {
        updateBookState(
            _bookState.value.copy(
                showBookshelvesDropDown = _bookState.value.showBookshelvesDropDown.copy(
                    showOption = !_bookState.value.showBookshelvesDropDown.showOption
                )
            )
        )

        fetchBookshelves()
    }

    fun onShowBottomSheet() {
        // Toggle the bottom sheet visibility
        updateBookState(
            _bookState.value.copy(
                showBottomSheet = _bookState.value.showBottomSheet.copy(
                    showOption = !_bookState.value.showBottomSheet.showOption
                )
            )
        )
    }

    fun fetchBookshelves() {
        viewModelScope.launch {
            val currentState = bookState.value
            if (currentState.bookshelvesState.resultState !is ResultState.Success && currentState.volumeId != null) {
                try {
                    val newBookshelvesState = booksUseCases.fetchAndMapBookshelves(currentState.volumeId!!)
                    updateBookState(
                        _bookState.value.copy(
                            bookshelvesState = newBookshelvesState?.copy()
                                ?: currentState.bookshelvesState.copy(resultState = ResultState.Error())
                        )
                    )
                    initialBookshelfState = bookState.value.bookshelvesState
                } catch (e: Exception) {
                    updateBookState(
                        _bookState.value.copy(
                            bookshelvesState = currentState.bookshelvesState.copy(resultState = ResultState.Error("Failed to fetch bookshelves"))
                        )
                    )
                }
            }
        }
    }

    fun onPushEditedBookshelfBooks() {
        viewModelScope.launch {
            val currentState = _bookState.value  // Fetch the current state

            updateBookState(
                currentState.copy(
                    bookshelvesState = _bookState.value.bookshelvesState.copy(
                        resultState = ResultState.Loading
                    ),
                    showBookshelvesDropDown = _bookState.value.showBookshelvesDropDown.copy(
                        isLoading = true
                    )
                )
            )

            val bookId = currentState.bookDetails.id
            val currentBookshelves = currentState.bookshelvesState.bookshelves
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
                    updateBookState(
                        currentState.copy(
                            showBookshelvesDropDown = currentState.showBookshelvesDropDown.copy(isLoading = false)
                        )
                    )
                    fetchBookshelves()
                }
                is NetworkResult.Error -> {
                    updateBookState(
                        currentState.copy(
                            showBookshelvesDropDown = currentState.showBookshelvesDropDown.copy(isLoading = false)
                        )
                    )
                }
            }
        }
    }


    public override fun onCleared() {
        super.onCleared()
        updateBookState(
            BookUiState()
        )
    }


    companion object {
        const val TAG = "BookDetailsViewModel"
    }

}

