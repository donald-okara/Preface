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
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.BookshelfUiState
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.SuccessState
import ke.don.shared_domain.states.UserLibraryState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookshelfRepositoryImpl(
    private val bookshelfNetworkClass: BookshelfNetworkClass,
    private val profileRepository: ProfileRepository,
    private val userProfile : Profile?,
    private val context : Context
): BookshelfRepository {
    private val _addBookshelfState = MutableStateFlow(AddBookshelfState())
    override val addBookshelfState: StateFlow<AddBookshelfState> = _addBookshelfState


    private val _userLibraryState = MutableStateFlow(UserLibraryState())
    override val userLibraryState: StateFlow<UserLibraryState> = _userLibraryState

    private val _bookshelfUiState = MutableStateFlow(BookshelfUiState())
    override val bookshelfUiState: StateFlow<BookshelfUiState> = _bookshelfUiState

    init {
        CoroutineScope(Dispatchers.IO).launch{
            Log.d(TAG, "userProfile: $userProfile")
            userProfile?.authId?.let {
                fetchUserBookShelves()
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
                fetchUserBookShelves()
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
    override suspend fun fetchUserBookShelves() {
        _userLibraryState.update{
            it.copy(successState = SuccessState.LOADING)
        }
        try {
            Log.d(TAG, "Fetching user bookshelves")
            _userLibraryState.update {
                it.copy(
                    userBookshelves = bookshelfNetworkClass.fetchUserBookshelves(userProfile?.authId!!),
                    successState = SuccessState.SUCCESS
                )
            }
            Log.d(TAG, "User bookshelves fetched successfully: ${userLibraryState.value.userBookshelves}")
        }catch (e: Exception){
            e.printStackTrace()
            _userLibraryState.update{
                it.copy(successState = SuccessState.ERROR)
            }
        }
    }

    override suspend fun fetchBookshelfById(bookshelfId: Int) {
        try {
            _bookshelfUiState.update {
                it.copy(resultState = ResultState.Loading)
            }

            val bookshelf = bookshelfNetworkClass.fetchBookshelfById(bookshelfId)
            if (bookshelf!=null){
                _bookshelfUiState.update {
                    it.copy(
                        bookShelf = bookshelf,
                        resultState = ResultState.Success

                    )
                }
            }else{
                _bookshelfUiState.update {
                    it.copy(resultState = ResultState.Error())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _bookshelfUiState.update {
                it.copy(resultState = ResultState.Error())
            }
        }


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

    override suspend fun deleteBookshelf(bookshelfId: Int): ResultState {
        return try {
            bookshelfNetworkClass.deleteBookshelf(bookshelfId)
        }catch (e: Exception) {
            e.printStackTrace()
            ResultState.Error(e.message.toString())
        }
    }


    companion object {
        const val TAG = "BookshelfRepositoryImpl"
    }
}