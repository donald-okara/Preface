package ke.don.common_datasource.remote.data.bookshelf.repositoryImpl

import android.content.Context
import android.util.Log
import android.widget.Toast
import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.local.roomdb.entities.toBookshelf
import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.common_datasource.remote.domain.states.UserLibraryState
import ke.don.common_datasource.remote.domain.states.toEntity
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.EmptyResultState
import ke.don.shared_domain.states.SuccessState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
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




    init {
        CoroutineScope(Dispatchers.IO).launch{
            Log.d(TAG, "userProfile: $userProfile")
            userProfile?.authId?.let {
                fetchUserBookShelves()
            }
        }
    }



    override suspend fun createBookshelf(bookshelf: BookshelfRef): EmptyResultState {
        return try {

            if (bookshelfNetworkClass.createBookshelf(bookshelf) == EmptyResultState.Success){
                profileRepository.userId.value?.let {
                    fetchUserBookShelves()
                }
            }
            EmptyResultState.Success
        } catch (e: Exception) {
            e.printStackTrace()
            _addBookshelfState.update {
                it.copy(successState = SuccessState.ERROR)
            }
            Toast.makeText(context, "Something went wrong, please try again", Toast.LENGTH_SHORT).show()
            EmptyResultState.Success
        }
    }
    override suspend fun fetchUserBookShelves():Flow<List<BookShelf>> {
        bookshelfDao.insertAll(bookshelfNetworkClass.fetchUserBookshelves(userProfile?.authId!!))
        return bookshelfDao.getAllBookshelvesFlow()
                .map { list -> list.map { it.toBookshelf() } }
    }

    override suspend fun fetchBookshelfRef(bookshelfId: Int): BookshelfRef? {
        return bookshelfNetworkClass.fetchBookshelfRef(bookshelfId)
    }

    override suspend fun editBookshelf(bookshelfId: Int, bookshelf: BookshelfRef): EmptyResultState {
        return try {
            if(bookshelfNetworkClass.updateBookshelf(bookshelfId =  bookshelfId, bookshelf = bookshelf)== EmptyResultState.Success){
                bookshelfDao.update(bookshelfEntity = bookshelf.toEntity())
            }
           EmptyResultState.Success
        } catch (e: Exception) {
            EmptyResultState.Error()
        }
    }

    override suspend fun fetchBookshelfById(bookshelfId: Int): Flow<BookshelfEntity> =
        bookshelfDao.getBookshelfById(bookshelfId)

    override suspend fun addBookToBookshelf(addBookToBookshelf: AddBookToBookshelf):EmptyResultState {
        return try {
            Log.d(TAG, "Attempting to add book")
            bookshelfNetworkClass.addBookToBookshelf(addBookToBookshelf)
            EmptyResultState.Success
        }catch (e: Exception){
            e.printStackTrace()
            EmptyResultState.Error(e.message.toString())
        }
    }

    override suspend fun removeBookFromBookshelf(bookId: String, bookshelfId: Int):EmptyResultState {
        return try {
            bookshelfNetworkClass.removeBookFromBookshelf(bookId, bookshelfId)
            EmptyResultState.Success
        }catch (e: Exception){
            e.printStackTrace()
            EmptyResultState.Error(e.message.toString())
        }
    }

    override suspend fun deleteBookshelf(bookshelfId: Int): EmptyResultState {
        try {
            if(bookshelfNetworkClass.deleteBookshelf(bookshelfId) == EmptyResultState.Success){
                bookshelfDao.deleteBookshelfById(bookshelfId)
                return EmptyResultState.Success
            }else{
                return EmptyResultState.Error("Something went wrong")
            }
        }catch (e: Exception) {
            e.printStackTrace()
            return EmptyResultState.Error(e.message.toString())
        }
    }


    companion object {
        const val TAG = "BookshelfRepositoryImpl"
    }
}