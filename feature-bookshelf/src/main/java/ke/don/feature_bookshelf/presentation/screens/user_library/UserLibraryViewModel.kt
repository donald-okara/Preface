package ke.don.feature_bookshelf.presentation.screens.user_library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.shared_domain.states.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserLibraryViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    val userLibraryState = bookshelfRepository.userLibraryState

    private val _selectedBookshelfId = MutableStateFlow<Int?>(null)
    val selectedBookshelfId: StateFlow<Int?> = _selectedBookshelfId

    private val _showOptionsSheet = MutableStateFlow(false)
    val showOptionsSheet: StateFlow<Boolean> = _showOptionsSheet

    fun refreshAction(onRefreshComplete: () -> Unit){
        viewModelScope.launch {
            try {
                bookshelfRepository.fetchUserBookShelves()
            } finally {
                onRefreshComplete()
            }
        }
    }

    fun updateSelectedBookshelf(bookshelfId: Int?) {
        _selectedBookshelfId.value = bookshelfId
    }

    fun updateShowSheet(newState: Boolean){
        _showOptionsSheet.update {
            newState
        }
    }

    fun deleteBookshelf(onRefreshComplete: () -> Unit, bookshelfId : Int){
        viewModelScope.launch {
            if (bookshelfRepository.deleteBookshelf(bookshelfId) == ResultState.Success){
                updateShowSheet(false)
                refreshAction(onRefreshComplete)
            }
        }
    }
}
