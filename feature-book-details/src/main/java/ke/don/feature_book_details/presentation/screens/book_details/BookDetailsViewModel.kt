package ke.don.feature_book_details.presentation.screens.book_details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.common_datasource.remote.domain.repositories.UserProgressRepository
import ke.don.common_datasource.remote.domain.states.BookUiState
import ke.don.common_datasource.remote.domain.states.BookshelvesState
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.common_datasource.remote.domain.states.ShowOptionState
import ke.don.common_datasource.remote.domain.states.UserProgressState
import ke.don.common_datasource.remote.domain.states.toAddBookToBookshelf
import ke.don.common_datasource.remote.domain.usecases.BooksUseCases
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.CreateUserProgressDTO
import ke.don.shared_domain.logger.Logger
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
    private val booksRepository: BooksRepository,
    private val profileRepository: ProfileRepository,
    private val progressRepository: UserProgressRepository,
    private val logger : Logger,
    private val colorPaletteExtractor: ColorPaletteExtractor,
    private val booksUseCases : BooksUseCases
) : ViewModel() {

    private val _bookState = MutableStateFlow(BookUiState())
    val bookState: StateFlow<BookUiState> = _bookState

//    private val _bookshelvesState = MutableStateFlow(BookshelvesState())
//    val bookshelvesState = _bookshelvesState
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = BookshelvesState()
//        )


    private var initialBookshelfState = BookshelvesState()

    init {
        Log.d("BookViewModel", "Init started")
        onLoading()
        Log.d("BookViewModel", "Loading set, state: ${bookState.value.loadingJoke}")
    }



    /**
     * Ui events
     * TODO
     */

    private fun updateBookState(newState: BookUiState) {
        _bookState.update { newState }
    }

    private fun updateBookshelvesState(newState: BookshelvesState) {
        updateBookState(
            BookUiState(bookshelvesState = newState)
        )
    }

    private fun updateProgressState(newState: UserProgressState){
        updateBookState(
            BookUiState(
                userProgressState = newState
            )
        )
    }


    fun onVolumeIdPassed(passedVolumeId: String) {
        updateBookState(
            BookUiState(
                volumeId = passedVolumeId
            )
        )
    }


    fun onSearchAuthor(author: String) {
//        booksUseCases.onSearchQueryChange(author)
//        viewModelScope.launch {
//            booksUseCases.onSearch()
//        }
        TODO()
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


    private fun onLoading() {
        updateBookState(
            BookUiState(
                loadingJoke = loadingBookJokes[Random.nextInt(loadingBookJokes.size)]
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
        _bookState.update { current ->
            current.copy(
                showBottomSheet = ShowOptionState(
                    showOption = !current.showBottomSheet.showOption
                )
            )
        }

        // Fetch bookshelves if conditions are met
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

    fun onBookProgressUpdate(progress: Int){
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

    /**
     * Backend implementation events
     * TODO
     */

    fun refreshAction() = viewModelScope.launch {
        onLoading()
        bookState.value.volumeId?.let {
            fetchAndUpdateBookUiState(it)
        }
    }


    fun fetchAndUpdateBookUiState(volumeId: String) {
        viewModelScope.launch {
            updateBookState(
                BookUiState(
                    volumeId = volumeId
                )
            )
            try {
                Log.d(TAG, "Attempting to fetch bookUiState",)

                val bookDetailsResult =  fetchBookDetails()

                val resultState =
                    if (bookDetailsResult != null) ResultState.Success else ResultState.Error()

                val highestImageUrl = bookDetailsResult?.volumeInfo?.imageLinks?.getHighestQualityUrl()?.replace("http", "https") ?: ""
                val colorPallet = if (bookDetailsResult != null && highestImageUrl.isNotEmpty()) {
                    withContext(Dispatchers.Default) {
                        colorPaletteExtractor.extractColorPalette(highestImageUrl)
                    }
                } else {
                    ColorPallet() // Default fallback
                }

                updateBookState(
                    BookUiState(
                        resultState = resultState,
                        bookDetails = bookDetailsResult ?: BookDetailsResponse(),
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
        return when (val response = bookState.value.volumeId?.let { booksRepository.getBookDetails(it) }) {
            is NetworkResult.Error -> null

            is NetworkResult.Success -> response.data
            null -> null
        }
    }

    fun onProgressTabNav(){
        viewModelScope.launch{
            if (bookState.value.userProgressState.resultState !is ResultState.Success && bookState.value.volumeId != null && bookState.value.userId != null) {
                try {
                    val newProgressState = booksUseCases.fetchBookProgress(userId = bookState.value.userId!!, bookId = bookState.value.volumeId!!)
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

    private fun reloadBookshelf(){
        viewModelScope.launch {

            bookState.value.volumeId?.let {
                booksUseCases.fetchAndMapBookshelves(it)?.let {
                    updateBookshelvesState(
                        it
                    )
                }
            }

            initialBookshelfState = bookState.value.bookshelvesState

        }
    }

    private fun reloadProgress(){
        viewModelScope.launch {
            bookState.value.volumeId?.let {
                bookState.value.userId?.let { it1 ->
                    booksUseCases.fetchBookProgress(bookId = it, userId = it1)?.let { newState ->
                        updateProgressState(
                            newState
                        )
                    }
                }
            }
        }
    }

    // Synchronous helper function to fetch progress within the same coroutine

    fun onPushEditedBookshelfBooks() {
        viewModelScope.launch {
            updateBookState(
                BookUiState(
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
                .map { it.bookshelfBookDetails.toAddBookToBookshelf(bookState.value.bookDetails) }

            when (booksRepository.pushEditedBookshelfBooks(bookId, bookshelfIdsToRemove, addBookToBookshelfList)) {
                is NetworkResult.Success -> {
                    reloadBookshelf()
                    updateBookState(
                        BookUiState(
                            showBookshelvesDropDown = ShowOptionState(isLoading = false, showOption = false)
                        )
                    )
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
            updateProgressDialogState(isLoading = true)

            val userId = bookState.value.userId?: return@launch
            val bookId = bookState.value.volumeId?: return@launch
            val newPage = _bookState.value.userProgressState.newProgress
            val totalPages = _bookState.value.bookDetails.volumeInfo.pageCount

            val result: NetworkResult<NoDataReturned> = if (_bookState.value.userProgressState.isPresent) {
                progressRepository.updateUserProgress(bookId = bookId, userId = userId, newCurrentPage = newPage)
            } else {
                val dto = CreateUserProgressDTO(bookId, newPage, totalPages)
                progressRepository.addUserProgress(dto)
            }

            if (result is NetworkResult.Success){
                booksUseCases.fetchBookProgress(bookId, userId)
            }

            // Hide loading
            updateProgressDialogState(isLoading = false, toggle = true)
        }
    }

    public override fun onCleared() {
        super.onCleared()
       _bookState.update {
            BookUiState()
        }
        logger.logDebug(TAG, "ViewModel cleared")
    }


    companion object {
        const val TAG = "BookDetailsViewModel"
    }

}