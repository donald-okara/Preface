package ke.don.feature_bookshelf.presentation.screens.add_bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.toBookshelf
import ke.don.shared_domain.data_models.BookshelfType
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookshelfViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
): ViewModel() {
    val addBookshelfState: StateFlow<AddBookshelfState> = bookshelfRepository.addBookshelfState

    fun onNameChange(name: String) = bookshelfRepository.onNameChange(name)

    fun isAddButtonEnabled(): Boolean{
        return addBookshelfState.value.name.isNotEmpty()
    }

    fun onDescriptionChange(description: String) = bookshelfRepository.onDescriptionChange(description)

    fun onBookshelfTypeChange(bookshelfType: BookshelfType) = bookshelfRepository.onBookshelfTypeChange(bookshelfType)

    fun onAddBookshelf(){
        viewModelScope.launch {
            bookshelfRepository.createBookshelf(addBookshelfState.value.toBookshelf())
        }
    }

}