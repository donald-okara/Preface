package ke.don.feature_bookshelf.presentation.screens.add_bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.feature_bookshelf.domain.AddBookshelfState
import ke.don.feature_bookshelf.domain.SuccessState
import ke.don.feature_bookshelf.domain.toBookshelf
import ke.don.shared_domain.data_models.BookshelfType
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

    fun onNameChange(name: String){
        _addBookshelfState.update {
            it.copy(
                name = name
            )
        }

    }

    fun isAddButtonEnabled(): Boolean{
        return addBookshelfState.value.name.isNotEmpty()
    }

    fun onDescriptionChange(description: String){
        _addBookshelfState.update {
            it.copy(
                description = description
            )
        }
    }

    fun onBookshelfTypeChange(bookshelfType: BookshelfType){
        _addBookshelfState.update {
            it.copy(
                bookshelfType = bookshelfType
            )
        }
    }

    fun onAddBookshelf(){
        try {
            viewModelScope.launch {
                _addBookshelfState.update {
                    it.copy(
                        successState = SuccessState.LOADING
                    )
                }
                bookshelfRepository.createBookshelf(addBookshelfState.value.toBookshelf())
                _addBookshelfState.update {
                    it.copy(
                        successState = SuccessState.SUCCESS
                    )
                }

            }
        } catch (e: Exception) {
            _addBookshelfState.update {
                it.copy(
                    successState = SuccessState.ERROR
                )
            }
            e.printStackTrace()
        }
    }

}