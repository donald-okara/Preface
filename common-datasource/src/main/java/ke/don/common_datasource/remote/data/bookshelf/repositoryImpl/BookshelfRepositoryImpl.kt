package ke.don.common_datasource.remote.data.bookshelf.repositoryImpl

import android.content.Context
import android.util.Log
import android.widget.Toast
import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.local.roomdb.entities.toBookshelf
import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
import ke.don.common_datasource.remote.domain.states.BookshelfUiState
import ke.don.common_datasource.remote.domain.states.UserLibraryState
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.common_datasource.remote.domain.states.toEntity
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.BookshelfType
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.SuccessState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookshelfRepositoryImpl(
    private val bookshelfNetworkClass: BookshelfNetworkClass,
    private val profileRepository: ProfileRepository,
    private val userProfile : Profile?,
    private val bookshelfDao: BookshelfDao,
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
            if (bookshelfNetworkClass.createBookshelf(bookshelf) == ResultState.Success){
                profileRepository.userId.value?.let {
                    fetchUserBookShelves()
                }
                _addBookshelfState.update {
                    it.copy(
                        name = "",
                        description = "",
                        bookshelfType = BookshelfType.GENERAL,
                        successState = SuccessState.SUCCESS
                    )
                }
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
            bookshelfDao.insertAll(bookshelfNetworkClass.fetchUserBookshelves(userProfile?.authId!!))
            Log.d(TAG, "Fetching user bookshelves")
            _userLibraryState.update { libraryState ->
                libraryState.copy(
                    userBookshelves = bookshelfDao.getAllBookshelvesFlow()
                        .map { list -> list.map { it.toBookshelf() } },
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

    override suspend fun fetchBookshelfRef(bookshelfId: Int) {
        bookshelfNetworkClass.fetchBookshelfRef(bookshelfId)?.let { bookshelfRef ->

        _addBookshelfState.update {
                it.copy(
                    name = bookshelfRef.name,
                    description = bookshelfRef.description
                )
            }

        }
    }

    override suspend fun editBookshelf(bookshelfId: Int, bookshelf: BookshelfRef) {
        _addBookshelfState.update {
            it.copy(successState = SuccessState.LOADING)
        }
        if(bookshelfNetworkClass.updateBookshelf(bookshelfId =  bookshelfId, bookshelf = bookshelf)== ResultState.Success){
            bookshelfDao.update(bookshelfEntity = bookshelf.toEntity())
        }

        profileRepository.userId.value?.let {
            fetchBookshelfById(bookshelfId)
            fetchUserBookShelves()
        }
        _addBookshelfState.update {
            it.copy(successState = SuccessState.SUCCESS)
        }
    }

    override suspend fun fetchBookshelfById(bookshelfId: Int) {
        try {
            _bookshelfUiState.update {
                it.copy(resultState = ResultState.Loading)
            }

            val bookshelf = bookshelfDao.getBookshelfById(bookshelfId)
            _bookshelfUiState.update {
                it.copy(
                    bookShelf = bookshelf,
                    resultState = ResultState.Success

                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _bookshelfUiState.update {
                it.copy(resultState = ResultState.Error())
            }
        }


    }

    override suspend fun addBookToBookshelf(addBookToBookshelf: AddBookToBookshelf):ResultState {
        return try {
            Log.d(TAG, "Attempting to add book")
            bookshelfNetworkClass.addBookToBookshelf(addBookToBookshelf)
            ResultState.Success
        }catch (e: Exception){
            e.printStackTrace()
            ResultState.Error(e.message.toString())
        }
    }

    override suspend fun removeBookFromBookshelf(bookId: String, bookshelfId: Int):ResultState {
        return try {
            bookshelfNetworkClass.removeBookFromBookshelf(bookId, bookshelfId)
            ResultState.Success
        }catch (e: Exception){
            e.printStackTrace()
            ResultState.Error(e.message.toString())
        }
    }

    override suspend fun deleteBookshelf(bookshelfId: Int): ResultState {
        try {
            if(bookshelfNetworkClass.deleteBookshelf(bookshelfId) == ResultState.Success){
                bookshelfDao.deleteBookshelfById(bookshelfId)
                return ResultState.Success
            }else{
                return ResultState.Error("Something went wrong")
            }
        }catch (e: Exception) {
            e.printStackTrace()
            return ResultState.Error(e.message.toString())
        }
    }


    companion object {
        const val TAG = "BookshelfRepositoryImpl"
    }
}