package ke.don.feature_book_details.presentation.screens.book_details

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.usecases.BooksUseCases
import ke.don.shared_domain.logger.Logger
import ke.don.shared_domain.states.BookUiState
import ke.don.shared_domain.states.loadingBookJokes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val repository: BooksRepository,
    private val logger : Logger,
    private val booksUseCases : BooksUseCases
) : ViewModel() {

    private val _volumeId = MutableStateFlow<String?>(null)
    private val volumeId: StateFlow<String?> = _volumeId

    private val _bookState = MutableStateFlow(BookUiState())
    val bookState: StateFlow<BookUiState> = _bookState

    var loadingJoke: String by mutableStateOf("")
        private set

    init {
        onLoading()
        observeVolumeId()
    }

    private fun observeVolumeId() {
        viewModelScope.launch {
            _volumeId.filterNotNull().collectLatest { id ->
                booksUseCases.getBookDetails(id)
                repository.bookUiState.collectLatest { state ->
                    _bookState.update {
                        state
                    }
                }


            }
        }
    }


    fun onVolumeIdPassed(passedVolumeId: String) {
        _volumeId.update {
            passedVolumeId
        }
    }

    fun refreshAction() = viewModelScope.launch {
        volumeId.value?.let {
            booksUseCases.getBookDetails(it)
        }
        onLoading()
    }

    fun onSearchAuthor(author: String) {
        booksUseCases.onSearchQueryChange(author)
        viewModelScope.launch {
            booksUseCases.onSearch()
        }
    }

    fun onSelectBookshelf(bookshelfId: Int) = viewModelScope.launch {
        Log.d(TAG, "onSelectBookshelf: $bookshelfId")
        booksUseCases.onSelectBookshelf(bookshelfId)

        Log.d(TAG, "SelectedBookshelves: ${_bookState.value.bookshelvesState.bookshelves}")
    }


    private fun onLoading() {
        loadingJoke = loadingBookJokes[Random.nextInt(loadingBookJokes.size)]
        logger.logDebug(TAG, "Loading joke: $loadingJoke")
    }

    fun onPushEditedBookshelfBooks() = viewModelScope.launch {
        Log.d(TAG, "onPushEditedBookshelfBooks: ${_bookState.value.bookshelvesState.bookshelves}")
        booksUseCases.onPushEditedBookshelfBooks()
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


