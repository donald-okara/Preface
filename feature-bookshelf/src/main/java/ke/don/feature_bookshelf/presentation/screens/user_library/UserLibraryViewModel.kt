package ke.don.feature_bookshelf.presentation.screens.user_library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserLibraryViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    val userLibraryState = bookshelfRepository.userLibraryState

    fun refreshAction(onRefreshComplete: () -> Unit){
        viewModelScope.launch {
            try {
                bookshelfRepository.fetchUserBookShelves()
            } finally {
                onRefreshComplete()
            }
        }
    }

}