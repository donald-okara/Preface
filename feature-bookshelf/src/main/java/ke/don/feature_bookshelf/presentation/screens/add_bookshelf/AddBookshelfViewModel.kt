package ke.don.feature_bookshelf.presentation.screens.add_bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.states.UserLibraryState
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.toBookshelf
import ke.don.shared_domain.data_models.BookshelfType
import ke.don.shared_domain.states.SuccessState
import ke.don.shared_domain.states.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
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
    val addBookshelfState = _addBookshelfState
        .onStart { observeBookshelfId() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AddBookshelfState()
        )

    private fun observeBookshelfId() {
        viewModelScope.launch {
            bookshelfId.collectLatest { id ->
                when (val result = id?.let { bookshelfRepository.fetchBookshelfRef(it) }) {
                    is NetworkResult.Error -> {
                        _addBookshelfState.update { it.copy(successState = SuccessState.ERROR) }
                    }

                    is NetworkResult.Success -> {
                        _addBookshelfState.update {
                            it.copy(
                                name = result.result.name,
                                description = result.result.description
                            )
                        }
                    }

                    null -> {
                        //Do nothing
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

    private fun createBookshelf(bookshelf: BookshelfRef, onNavigateBack: () -> Unit) {
        viewModelScope.launch {
            when(bookshelfRepository.createBookshelf(bookshelf)){
                is NetworkResult.Error -> {
                    _addBookshelfState.update { it.copy(successState = SuccessState.ERROR) }
                }
                is NetworkResult.Success -> {
                    _addBookshelfState.update { it.copy(successState = SuccessState.SUCCESS) }
                    onNavigateBack()
                }
            }
        }

    }

    private fun editBookshelf(bookshelf: BookshelfRef, onNavigateBack: () -> Unit){
        viewModelScope.launch {
            when(bookshelfRepository.editBookshelf(bookshelfId.value!!, bookshelf)){
                is NetworkResult.Error -> {
                    _addBookshelfState.update { it.copy(successState = SuccessState.ERROR) }
                }

                is NetworkResult.Success -> {
                    _addBookshelfState.update { it.copy(successState = SuccessState.SUCCESS) }
                    onNavigateBack()
                }
            }
        }
    }

    fun onSubmit(onNavigateBack: () -> Unit) {
        viewModelScope.launch {
            _addBookshelfState.update { it.copy(successState = SuccessState.LOADING) }

            val bookshelf = addBookshelfState.value.toBookshelf()

            if (bookshelfId.value == null) {
                createBookshelf(bookshelf, onNavigateBack)
                return@launch
            } else {
                editBookshelf(bookshelf, onNavigateBack)
                return@launch
            }

        }
    }

}