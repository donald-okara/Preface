package ke.don.feature_bookshelf.presentation.screens.add_bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.toBookshelf
import ke.don.shared_domain.data_models.BookshelfType
import ke.don.shared_domain.states.SuccessState
import ke.don.shared_domain.states.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookshelfViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
): ViewModel() {
    private val _addBookshelfState = MutableStateFlow(AddBookshelfState())
    val addBookshelfState: StateFlow<AddBookshelfState> = _addBookshelfState

    fun handleEvent(event: AddBookshelfEventHandler){
        when(event) {
            is AddBookshelfEventHandler.FetchBookshelf -> {
                fetchBookshelf(event.id)
            }
            is AddBookshelfEventHandler.OnNameChange -> onNameChange(event.name)
            is AddBookshelfEventHandler.OnDescriptionChange -> onDescriptionChange(event.description)
            is AddBookshelfEventHandler.OnBookshelfTypeChange -> onBookshelfTypeChange(event.bookshelfType)
            is AddBookshelfEventHandler.OnSubmit -> onSubmit(event.onNavigateBack)
            is AddBookshelfEventHandler.OnCleared -> onCleared()
        }
    }
    fun fetchBookshelf(id: Int?) {
        viewModelScope.launch {
            Log.d(TAG,"Passed id:: $id")
            when (val result = id?.let { bookshelfRepository.fetchBookshelfRef(it) }) {
                is NetworkResult.Error -> {
                    _addBookshelfState.update { it.copy(successState = SuccessState.ERROR) }
                }

                is NetworkResult.Success -> {
                    _addBookshelfState.update {
                        it.copy(
                            bookshelfId = id,
                            name = result.data.name,
                            description = result.data.description
                        )
                    }
                }

                null -> {
                    //Do nothing
                }
            }

        }
    }

    fun onNameChange(name: String) {
        if (name.length <= MAX_NAME_LENGTH) {
            _addBookshelfState.update { current ->
                if (current.name != name) current.copy(name = name)
                else current
            }
        }
    }



    fun onDescriptionChange(description: String) {
        if (description.length <= MAX_DESCRIPTION_LENGTH) {
            _addBookshelfState.update { current ->
                if (current.description != description) current.copy(description = description)
                else current
            }
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
            when(bookshelfRepository.editBookshelf(_addBookshelfState.value.bookshelfId!!, bookshelf)){
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

            if (_addBookshelfState.value.bookshelfId == null) {
                createBookshelf(bookshelf, onNavigateBack)
                return@launch
            } else {
                editBookshelf(bookshelf, onNavigateBack)
                return@launch
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        _addBookshelfState.update {
            AddBookshelfState()
        }
    }

    companion object {
        const val TAG = "AddBookshelfViewModel"
        const val MAX_NAME_LENGTH = 30
        const val MAX_DESCRIPTION_LENGTH = 200
    }
}