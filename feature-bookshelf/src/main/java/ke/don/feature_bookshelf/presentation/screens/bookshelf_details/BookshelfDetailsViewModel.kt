package ke.don.feature_bookshelf.presentation.screens.bookshelf_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.shared_domain.states.BookshelfUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookshelfDetailsViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
): ViewModel() {
    private val _bookshelfId = MutableStateFlow<Int?>(null)
    val bookshelfId: MutableStateFlow<Int?> = _bookshelfId

    private val _bookshelfUiState = MutableStateFlow(BookshelfUiState())
    val bookshelfUiState: MutableStateFlow<BookshelfUiState> = _bookshelfUiState

    init {
        observeBookshelfId()
    }

    private fun observeBookshelfId() {
        viewModelScope.launch {
            _bookshelfId.filterNotNull().collectLatest { id ->
                bookshelfRepository.fetchBookshelfById(id)
                bookshelfRepository.bookshelfUiState.collectLatest { state ->
                    _bookshelfUiState.update {
                        state
                    }
                }
            }
        }
    }

    fun onBookshelfIdPassed(passedBookshelfId : Int){
        _bookshelfId.update {
            passedBookshelfId
        }
    }

}