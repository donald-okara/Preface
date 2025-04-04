package ke.don.feature_book_details.presentation.screens.book_details

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import ke.don.common_datasource.remote.domain.states.toBookshelfBookDetails
import ke.don.common_datasource.remote.domain.usecases.BooksUseCases
import ke.don.shared_domain.data_models.CreateUserProgressDTO
import ke.don.shared_domain.data_models.UserProgressResponse
import ke.don.shared_domain.logger.Logger
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.loadingBookJokes
import ke.don.shared_domain.utils.color_utils.ColorPaletteExtractor
import ke.don.shared_domain.utils.color_utils.model.ColorPallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    private val _volumeId = MutableStateFlow<String?>(null)
    private val volumeId: StateFlow<String?> = _volumeId

    private val _bookState = MutableStateFlow(BookUiState())
    val bookState = _bookState
        .onStart { observeBookDetails() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BookUiState()
        )

    private val _bookshelvesState = MutableStateFlow(BookshelvesState())
    val bookshelvesState = _bookshelvesState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BookshelvesState()
        )


    private var initialBookshelfState = BookshelvesState()

    init {
        onLoading()
    }


    private fun updateBookState(newState: BookUiState) {
        _bookState.update { newState }
    }

    private fun updateBookshelvesState(newState: BookshelvesState) {
        _bookshelvesState.update { newState }
    }

    fun onVolumeIdPassed(passedVolumeId: String) {
        _volumeId.update {
            passedVolumeId
        }
    }


    private fun observeBookDetails() {
        viewModelScope.launch {
            _volumeId.filterNotNull().collectLatest { id ->
                val result = booksUseCases.getBookDetails(id)
                val newState = when (result) {
                    is NetworkResult.Error -> _bookState.value.copy(
                        resultState = ResultState.Error(result.message)
                    )
                    is NetworkResult.Success -> {
                        val imageLinks = result.data.volumeInfo.imageLinks
                        val highestImageUrl = imageLinks.getHighestQualityUrl()?.replace("http", "https")

                        // Fetch progress synchronously within the same state update
                        val progress = fetchBookProgressSync(result.data.id, result.data.volumeInfo.pageCount)

                        _bookState.value.copy(
                            bookDetails = result.data,
                            resultState = ResultState.Success,
                            highestImageUrl = highestImageUrl,
                            userProgressState =   progress,
                            colorPallet = highestImageUrl?.let {
                                colorPaletteExtractor.extractColorPalette(it)
                            } ?: ColorPallet()
                        ).also {
                            observeBookshelves()
                        }
                    }
                }
                Log.d(TAG, "Book progress ::: ${newState.userProgressState} of total pages ::: ${newState.bookDetails.volumeInfo.pageCount}")
                updateBookState(newState)
            }
        }
    }

    // Synchronous helper function to fetch progress within the same coroutine
    private suspend fun fetchBookProgressSync(bookId: String, totalPages: Int): UserProgressState {
        val userId = profileRepository.fetchProfileFromDataStore().authId
        val progressResult = progressRepository.fetchBookProgressByUserAndBook(userId, bookId)

        return if (progressResult is NetworkResult.Success && progressResult.data != null) {
            UserProgressState(
                bookProgress = progressResult.data!!.copy(totalPages = totalPages),
                isPresent = true
            )
        } else {
            UserProgressState(
                bookProgress =  UserProgressResponse(
                    userId = userId,
                    bookId = bookId,
                    currentPage = 0,
                    lastUpdated = "not read yet",
                    totalPages = totalPages
                ),
                isPresent = false
            )

        }
    }


    private fun observeBookshelves() {
        viewModelScope.launch {
            bookState.collectLatest { state ->
                val bookId = state.bookDetails.id
                if (bookId.isNotBlank()) {
                    booksRepository.fetchBookshelves()
                        .firstOrNull() // Get only the first emitted value to avoid multiple updates
                        ?.let { bookshelves ->
                            val bookshelfDetails = bookshelves.map { bookshelf ->
                                bookshelf.toBookshelfBookDetails(
                                    isBookPresent = bookshelf.books.any { book ->
                                        book.bookId == bookId
                                    }
                                )
                            }
                            Log.d(TAG, "InitialState :: $bookshelfDetails")

                            updateBookshelvesState(BookshelvesState(bookshelves = bookshelfDetails))

                            // Ensure initialBookshelfState is set only once
                            initialBookshelfState = BookshelvesState(bookshelves = bookshelfDetails)
                        }
                }
            }
        }
    }



    fun refreshAction() = viewModelScope.launch {
        volumeId.value?.let {
            booksUseCases.getBookDetails(it)
        }
        onLoading()
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
                bookshelves = _bookshelvesState.value.bookshelves.map { bookshelf ->
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

    fun onPushEditedBookshelfBooks() = viewModelScope.launch {
        updateBookState(
            BookUiState(
                showBookshelvesDropDown = ShowOptionState(isLoading = true)
            )
        )

        val bookId = bookState.value.bookDetails.id
        val currentBookshelves = _bookshelvesState.value.bookshelves
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
                initialBookshelfState = _bookshelvesState.value.copy()
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

    fun onShowBookshelves(){
        updateBookState(
            _bookState.value.copy(
                showBookshelvesDropDown = ShowOptionState(
                    showOption = !_bookState.value.showBookshelvesDropDown.showOption
                )
            )
        )
    }

    fun onShowBottomSheet(){
        updateBookState(
            _bookState.value.copy(
                showBottomSheet = ShowOptionState(
                    showOption = !_bookState.value.showBottomSheet.showOption
                )
            )
        )
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

            val userId = profileRepository.fetchProfileFromDataStore().authId
            val bookId = volumeId.value ?: return@launch
            val newPage = _bookState.value.userProgressState.newProgress
            val totalPages = _bookState.value.bookDetails.volumeInfo.pageCount

            val result: NetworkResult<NoDataReturned> = if (_bookState.value.userProgressState.isPresent) {
                progressRepository.updateUserProgress(bookId = bookId, userId = userId, newCurrentPage = newPage)
            } else {
                val dto = CreateUserProgressDTO(bookId, newPage, totalPages)
                progressRepository.addUserProgress(dto)
            }

            if (result is NetworkResult.Success){
                fetchBookProgressSync(bookId, totalPages)
            }

            // Hide loading
            updateProgressDialogState(isLoading = false, toggle = true)
        }
    }

    public override fun onCleared() {
        super.onCleared()
        _volumeId.update {
            null
        }
        _bookState.update {
            BookUiState()
        }
        logger.logDebug(TAG, "ViewModel cleared")
    }


    companion object {
        const val TAG = "BookDetailsViewModel"
    }

}