package ke.don.feature_bookshelf.presentation.screens.user_library

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import javax.inject.Inject

@HiltViewModel
class UserLibraryViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    val userLibraryState = bookshelfRepository.userLibraryState

}