package ke.don.feature_bookshelf.presentation.screens.add_bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.toBookshelf
import ke.don.shared_domain.data_models.BookshelfType
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.SuccessState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookshelfViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
): ViewModel() {
    private val _bookShelfId = MutableStateFlow<Int?>(null)
    val bookshelfId: StateFlow<Int?> = _bookShelfId

    val addBookshelfState: StateFlow<AddBookshelfState> = bookshelfRepository.addBookshelfState

    init {
        observeBookshelfId()
    }
    private fun observeBookshelfId() {
        viewModelScope.launch {
            _bookShelfId.filterNotNull().collectLatest { id ->
                bookshelfRepository.fetchBookshelfRef(id)
            }
        }
    }

    fun onBookshelfIdPassed(passedBookshelfId: Int?) {
        _bookShelfId.update {
            passedBookshelfId
        }
    }

    fun onNameChange(name: String) = bookshelfRepository.onNameChange(name)

    fun isAddButtonEnabled(): Boolean{
        return addBookshelfState.value.name.isNotEmpty()
    }

    fun onDescriptionChange(description: String) = bookshelfRepository.onDescriptionChange(description)

    fun onBookshelfTypeChange(bookshelfType: BookshelfType) = bookshelfRepository.onBookshelfTypeChange(bookshelfType)

    fun onAddBookshelf(onNavigateBack: () -> Unit){
        viewModelScope.launch {
            if (bookshelfId.value == null){
                bookshelfRepository.createBookshelf(addBookshelfState.value.toBookshelf())
            }else{
                bookshelfRepository.editBookshelf(bookshelfId = bookshelfId.value!!, addBookshelfState.value.toBookshelf())
            }

            when(addBookshelfState.value.successState){
                SuccessState.SUCCESS -> onNavigateBack()
                else -> {}
            }
        }
    }

}