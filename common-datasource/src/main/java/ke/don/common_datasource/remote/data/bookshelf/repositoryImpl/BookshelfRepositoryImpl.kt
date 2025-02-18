package ke.don.common_datasource.remote.data.bookshelf.repositoryImpl

import android.content.Context
import android.util.Log
import android.widget.Toast
import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.BookshelfType
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.SuccessState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookshelfRepositoryImpl(
    private val bookshelfNetworkClass: BookshelfNetworkClass,
    private val profileRepository: ProfileRepository,
    private val context : Context
): BookshelfRepository {
    private val _addBookshelfState = MutableStateFlow(AddBookshelfState())
    override val addBookshelfState: StateFlow<AddBookshelfState> = _addBookshelfState
    override val userLibraryState = profileRepository.userLibraryState

    init {
        CoroutineScope(Dispatchers.IO).launch{
            profileRepository.userId.value?.let {
                profileRepository.fetchUserBookshelves()
            }
        }
    }

    override fun onNameChange(name: String) {
        _addBookshelfState.update {
            it.copy(name = name)
        }
    }
    override fun onDescriptionChange(description: String) {
        _addBookshelfState.update {
            it.copy(description = description)
        }
    }
    override fun onBookshelfTypeChange(bookshelfType: BookshelfType) {
        _addBookshelfState.update {
            it.copy(bookshelfType = bookshelfType)
        }
    }

    override suspend fun createBookshelf(bookshelf: BookshelfRef) {
        try {
            _addBookshelfState.update {
                it.copy(successState = SuccessState.LOADING)
            }
            bookshelfNetworkClass.createBookshelf(bookshelf)

            profileRepository.userId.value?.let {
                profileRepository.fetchUserBookshelves()
            }
            _addBookshelfState.update {
                it.copy(successState = SuccessState.SUCCESS)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            _addBookshelfState.update {
                it.copy(successState = SuccessState.ERROR)
            }
            Toast.makeText(context, "Something went wrong, please try again", Toast.LENGTH_SHORT).show()

        }
    }

    override suspend fun fetchUserBookShelves() = profileRepository.fetchUserBookshelves()

    override suspend fun fetchBookshelfById(bookshelfId: Int): BookshelfRef? {
        return bookshelfNetworkClass.fetchBookshelfById(bookshelfId)
    }

    override suspend fun addBookToBookshelf(addBookToBookshelf: AddBookToBookshelf) {
        try {
            Log.d(TAG, "Attempting to add book")

            bookshelfNetworkClass.addBookToBookshelf(addBookToBookshelf)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override suspend fun removeBookFromBookshelf(bookId: String, bookshelfId: Int) {
        try {
            bookshelfNetworkClass.removeBookFromBookshelf(bookId, bookshelfId)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    companion object {
        const val TAG = "BookshelfRepositoryImpl"
    }
}