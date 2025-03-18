package ke.don.feature_bookshelf.presentation.screens.add_bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.toBookshelf
import ke.don.shared_domain.data_models.BookshelfType
import ke.don.shared_domain.states.EmptyResultState
import ke.don.shared_domain.states.SuccessState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookshelfViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
): ViewModel() {
    private val _bookShelfId = MutableStateFlow<Int?>(null)
    val bookshelfId: StateFlow<Int?> = _bookShelfId

    private val _addBookshelfState = MutableStateFlow(AddBookshelfState())
    val addBookshelfState: StateFlow<AddBookshelfState> = _addBookshelfState

    init {
        observeBookshelfId()
    }


    private fun observeBookshelfId() {
        viewModelScope.launch {
            bookshelfId.collectLatest { id ->
                id?.let {
                    bookshelfRepository.fetchBookshelfRef(it)?.let { result ->
                        _addBookshelfState.update { state ->
                            state.copy(
                                name = result.name,
                                description = result.description,
                            )
                        }
                    }
                }
            }
        }
    }

    fun onBookshelfIdPassed(passedBookshelfId: Int?) {
        _bookShelfId.update {
            passedBookshelfId
        }
    }

    fun onNameChange(name: String) {
        _addBookshelfState.update {
            it.copy(name = name)
        }
    }

    fun isAddButtonEnabled(): Boolean{
        return addBookshelfState.value.name.isNotEmpty() && addBookshelfState.value.successState != SuccessState.LOADING
    }

    fun onDescriptionChange(description: String) {
        _addBookshelfState.update {
            it.copy(description = description)
        }

    }

    fun onBookshelfTypeChange(bookshelfType: BookshelfType) {
        _addBookshelfState.update {
            it.copy(bookshelfType = bookshelfType)
        }
    }

    fun onAddBookshelf(onNavigateBack: () -> Unit) {
        viewModelScope.launch {
            _addBookshelfState.update { it.copy(successState = SuccessState.LOADING) }

            val bookshelf = addBookshelfState.value.toBookshelf()
            val result = if (bookshelfId.value == null) {
                bookshelfRepository.createBookshelf(bookshelf)
            } else {
                bookshelfRepository.editBookshelf(bookshelfId.value!!, bookshelf)
            }

            if (result == EmptyResultState.Success) {
                _addBookshelfState.update { it.copy(successState = SuccessState.SUCCESS) }
                onNavigateBack()
            }
        }
    }

}