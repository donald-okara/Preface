package ke.don.feature_bookshelf.presentation.screens.bookshelf_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.states.BookshelfUiState
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookshelfDetailsViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
): ViewModel() {
    private val _bookshelfId = MutableStateFlow<Int?>(null)
    val bookshelfId: MutableStateFlow<Int?> = _bookshelfId

    private val _showOptionsSheet = MutableStateFlow(false)
    val showOptionsSheet: StateFlow<Boolean> = _showOptionsSheet

    private val _bookshelfUiState = MutableStateFlow(BookshelfUiState())
    val bookshelfUiState = _bookshelfUiState
        .onStart { observeBookshelfId() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BookshelfUiState()
        )


    private fun observeBookshelfId() {
        viewModelScope.launch {
            _bookshelfId.filterNotNull().collect { id ->
                when (val result = bookshelfRepository.fetchBookshelfById(id)){
                    is NetworkResult.Error -> {
                        _bookshelfUiState.update { bookshelfUiState ->
                            bookshelfUiState.copy(
                                resultState = ResultState.Error("Something went wrong")
                            )
                        }
                    }
                    is NetworkResult.Success -> {
                        _bookshelfUiState.update { uiState ->
                            uiState.copy(
                                bookShelf = result.data,
                                resultState = ResultState.Success
                            )
                        }
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

    fun updateShowSheet(newState: Boolean){
        _showOptionsSheet.update {
            newState
        }
    }


    fun deleteBookshelf(onNavigateBack: () -> Unit, bookshelfId : Int){
        viewModelScope.launch {
            when (bookshelfRepository.deleteBookshelf(bookshelfId)){
                is NetworkResult.Error -> {
                    _bookshelfUiState.update { bookshelfUiState ->
                        bookshelfUiState.copy(
                            resultState = ResultState.Error("Something went wrong")
                        )
                    }
                }
                is NetworkResult.Success -> {
                    updateShowSheet(false)
                    onNavigateBack()
                    _bookshelfUiState.update { bookshelfUiState ->
                        bookshelfUiState.copy(
                            resultState = ResultState.Success
                        )
                    }
                }

            }
        }
    }

    public override fun onCleared() {
        super.onCleared()
        _bookshelfUiState.update {
            BookshelfUiState()
        }
    }
}