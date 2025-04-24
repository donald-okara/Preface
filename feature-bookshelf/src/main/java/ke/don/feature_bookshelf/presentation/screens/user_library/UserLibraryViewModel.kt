package ke.don.feature_bookshelf.presentation.screens.user_library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.states.UserLibraryState
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.SuccessState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserLibraryViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    private val _userLibraryState = MutableStateFlow(UserLibraryState())
    val userLibraryState: StateFlow<UserLibraryState> = _userLibraryState

    fun handleEvent(eventHandler: LibraryEventHandler){
        when (eventHandler){
            is LibraryEventHandler.FetchBookshelves -> {
                refreshAction()
            }

            is LibraryEventHandler.DeleteBookshelf -> {
                deleteBookshelf(bookshelfId = eventHandler.bookshelfId)
            }

            is LibraryEventHandler.RefreshAction -> {
                refreshAction()
            }

            is LibraryEventHandler.SelectBookshelf -> {
                onShowBottomSheet(bookshelfId = eventHandler.bookshelfId)
            }

            is LibraryEventHandler.ToggleBottomSheet -> {
                updateShowSheet(eventHandler.newState)
            }

            is LibraryEventHandler.ResetState -> {
                onCleared()
            }
        }
    }

    fun refreshAction() {
        viewModelScope.launch {
            _userLibraryState.update {
                it.copy(isRefreshing = true)
            }
            delay(2000)

            fetchUserBookShelves()
            _userLibraryState.update {
                it.copy(isRefreshing = false)
            }

        }
    }

    fun onShowBottomSheet(bookshelfId: Int?) {
        if (bookshelfId != null) {

            _userLibraryState.update {
                it.copy(
                    selectedBookshelfId = bookshelfId
                )
            }
            updateShowSheet(
                newState = !userLibraryState.value.showOptionsSheet
            )
        }else{
            _userLibraryState.update {
                it.copy(
                    selectedBookshelfId = null
                )
            }
            updateShowSheet(
                newState = !userLibraryState.value.showOptionsSheet
            )
        }
    }

    fun updateShowSheet(newState: Boolean) {
        _userLibraryState.update {
            it.copy(
                showOptionsSheet = newState
            )
        }
    }

    fun fetchUserBookShelves() {
        viewModelScope.launch {
            _userLibraryState.update {
                it.copy(successState = SuccessState.LOADING)
            }
            when (val result = bookshelfRepository.fetchUserBookShelves()) {
                is NetworkResult.Error -> {
                    _userLibraryState.update { libraryState ->
                        libraryState.copy(
                            successState = SuccessState.ERROR
                        )
                    }
                }

                is NetworkResult.Success -> {
                    _userLibraryState.update { libraryState ->
                        libraryState.copy(
                            userBookshelves = result.data,
                            successState = SuccessState.SUCCESS,
                        )
                    }
                }
            }
        }
    }

    fun deleteBookshelf(bookshelfId : Int){
        viewModelScope.launch {
            when (bookshelfRepository.deleteBookshelf(bookshelfId)){
                is NetworkResult.Error -> {
                    _userLibraryState.update { libraryState ->
                        libraryState.copy(
                            successState = SuccessState.ERROR
                        )
                    }
                }
                is NetworkResult.Success -> {
                    updateShowSheet(false)
                    refreshAction()
                    _userLibraryState.update { libraryState ->
                        libraryState.copy(
                            successState = SuccessState.SUCCESS
                        )
                    }
                }

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _userLibraryState.update { 
            UserLibraryState()
        }
    }
}
