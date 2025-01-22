package ke.don.feature_book_details.presentation.screens.book_details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.shared_domain.screens.Screens
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val repository: ke.don.feature_book_details.domain.repositories.BooksRepository,
    private val logger : ke.don.feature_book_details.domain.logger.Logger,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val volumeId = savedStateHandle.get<String>(Screens.BookDetails.volumeIdNavigationArgument)

    val bookState: StateFlow<ke.don.feature_book_details.domain.states.BookUiState> = repository.bookUiState

    var loadingJoke: String by mutableStateOf("")
        private set

    init {
        onLoading()

        viewModelScope.launch {
            volumeId?.let {
                repository.getBookDetails(it)
            }
        }
    }

    fun refreshAction() = viewModelScope.launch {
        volumeId?.let {
            repository.getBookDetails(it)
        }
        onLoading()
    }


    fun onLoading() {
        loadingJoke = loadingBookJokes[Random.nextInt(loadingBookJokes.size)]
        logger.logDebug(TAG, "Loading joke: $loadingJoke")
    }


    companion object {
        const val TAG = "BookDetailsViewModel"
    }
}


