package ke.don.feature_book_details.presentation.screens.book_details

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.states.BookUiState
import ke.don.common_datasource.remote.domain.states.BookshelvesState
import ke.don.common_datasource.remote.domain.states.ShowBookshelvesState
import ke.don.common_datasource.remote.domain.states.orFalse
import ke.don.common_datasource.remote.domain.states.orTrue
import ke.don.common_datasource.remote.domain.states.toAddBookToBookshelf
import ke.don.common_datasource.remote.domain.states.toBookshelfBookDetails
import ke.don.common_datasource.remote.domain.usecases.BooksUseCases
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
    private val repository: BooksRepository,
    private val logger : Logger,
    private val colorPaletteExtractor: ColorPaletteExtractor,
    private val booksUseCases : BooksUseCases
) : ViewModel() {

    private val _volumeId = MutableStateFlow<String?>(null)
    private val volumeId: StateFlow<String?> = _volumeId

    private val _showBookshelves= MutableStateFlow(ShowBookshelvesState())
    val showBookshelves: StateFlow<ShowBookshelvesState> = _showBookshelves

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


    var loadingJoke: String by mutableStateOf("")
        private set

    var initialBookshelfState = BookshelvesState()

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
            updateBookState(
                BookUiState(
                    resultState = ResultState.Loading
                )
            )

            _volumeId.filterNotNull().collectLatest { id ->
                when (val result = booksUseCases.getBookDetails(id)){
                    is NetworkResult.Error -> {
                        updateBookState(
                            BookUiState(
                                resultState = ResultState.Error(result.message)
                            )
                        )
                    }
                    is NetworkResult.Success -> {
                        val highestImageUrl = result.data.volumeInfo.imageLinks.let {
                            it.extraLarge ?: it.large ?: it.medium ?: it.small ?: it.thumbnail
                            ?: it.smallThumbnail
                        }?.replace("http", "https")

                        Log.d(TAG, "observeBookDetails: $highestImageUrl")
                        updateBookState(
                            BookUiState(
                                bookDetails = result.data,
                                resultState = ResultState.Success,
                                highestImageUrl = highestImageUrl,
                                colorPallet = if (highestImageUrl.isNullOrEmpty()) {
                                    ColorPallet()
                                } else {
                                    colorPaletteExtractor.extractColorPalette(highestImageUrl)
                                }
                            )
                        )
                        observeBookshelves()

                    }
                }
            }

        }
    }

    private fun observeBookshelves() {
        viewModelScope.launch {
            bookState.collectLatest { state ->
                val bookId = state.bookDetails.id
                if (bookId.isNotBlank()) {
                    repository.fetchBookshelves()
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
        loadingJoke = loadingBookJokes[Random.nextInt(loadingBookJokes.size)]
        logger.logDebug(TAG, "Loading joke: $loadingJoke")
    }

    fun onPushEditedBookshelfBooks() = viewModelScope.launch {
        _showBookshelves.update { ShowBookshelvesState(isLoading = true) }

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

        when (repository.pushEditedBookshelfBooks(bookId, bookshelfIdsToRemove, addBookToBookshelfList)) {
            is NetworkResult.Success -> {
                initialBookshelfState = _bookshelvesState.value.copy()
                _showBookshelves.update { ShowBookshelvesState(isLoading = false, showBooksheves = false) }
            }
            is NetworkResult.Error -> {
                _showBookshelves.update { ShowBookshelvesState(isLoading = false) }
            }
        }
    }

    fun onShowBookshelves(){
        _showBookshelves.update {
            ShowBookshelvesState(
                showBooksheves = !it.showBooksheves
            )
        }
    }

    fun resetPushSuccess() {
        _showBookshelves.update {
            ShowBookshelvesState()
        }
    }
    override fun onCleared() {
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


