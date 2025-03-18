package ke.don.feature_bookshelf.presentation.screens.user_library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.local.roomdb.entities.toBookshelf
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.states.UserLibraryState
import ke.don.shared_domain.states.EmptyResultState
import ke.don.shared_domain.states.SuccessState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserLibraryViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    private val _userLibraryState = MutableStateFlow(UserLibraryState())
    val userLibraryState = _userLibraryState
        .onStart { fetchUserBookShelves() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserLibraryState()
        )

    private val _selectedBookshelfId = MutableStateFlow<Int?>(null)
    val selectedBookshelfId: StateFlow<Int?> = _selectedBookshelfId

    private val _showOptionsSheet = MutableStateFlow(false)
    val showOptionsSheet: StateFlow<Boolean> = _showOptionsSheet

    fun fetchUserBookShelves() {
        viewModelScope.launch {
            _userLibraryState.update{
                it.copy(successState = SuccessState.LOADING)
            }

            _userLibraryState.update { libraryState ->
                libraryState.copy(
                    userBookshelves = bookshelfRepository.fetchUserBookShelves(),
                    successState = SuccessState.SUCCESS
                )
            }

        }
    }

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
            if (bookshelfRepository.deleteBookshelf(bookshelfId) == EmptyResultState.Success){
                updateShowSheet(false)
                refreshAction(onRefreshComplete)
            }
        }
    }
}
