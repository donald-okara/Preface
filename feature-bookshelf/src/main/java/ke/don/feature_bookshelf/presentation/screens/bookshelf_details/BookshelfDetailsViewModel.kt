package ke.don.feature_bookshelf.presentation.screens.bookshelf_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.states.BookshelfUiState
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookshelfDetailsViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
): ViewModel() {
    private val _bookshelfUiState = MutableStateFlow(BookshelfUiState())
    val bookshelfUiState: StateFlow<BookshelfUiState> = _bookshelfUiState

    fun handleEvents(event: BookshelfEventHandler){
        when(event){
            is BookshelfEventHandler.FetchBookshelf -> {
                fetchBookshelf(event.bookShelfId)
            }

            is BookshelfEventHandler.DeleteBookshelf -> {
                deleteBookshelf(event.onNavigateBack, event.bookShelfId)
            }

            is BookshelfEventHandler.ToggleBottomSheet -> {
                updateShowSheet()
            }
        }
    }
    fun fetchBookshelf(bookshelfId: Int) {
        viewModelScope.launch {
                when (val result = bookshelfRepository.fetchBookshelfById(bookshelfId)){
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

    fun updateShowSheet(){
        _bookshelfUiState.update {
            it.copy(
                showOptionsSheet = !_bookshelfUiState.value.showOptionsSheet
            )
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
                    updateShowSheet()
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
}