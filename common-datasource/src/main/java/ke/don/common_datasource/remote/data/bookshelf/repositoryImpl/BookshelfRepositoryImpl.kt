package ke.don.common_datasource.remote.data.bookshelf.repositoryImpl

import android.content.Context
import android.widget.Toast
import ke.don.common_datasource.local.datastore.profile.profileDataStore
import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.data_models.BookshelfType
import ke.don.shared_domain.states.SuccessState
import ke.don.shared_domain.data_models.SupabaseBookshelf
import ke.don.shared_domain.states.UserLibraryState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookshelfRepositoryImpl(
    private val bookshelfNetworkClass: BookshelfNetworkClass,
    private val context : Context
): BookshelfRepository {
    private val _addBookshelfState = MutableStateFlow(AddBookshelfState())
    override val addBookshelfState: StateFlow<AddBookshelfState> = _addBookshelfState

    private val _userLibraryState = MutableStateFlow(UserLibraryState())
    override val userLibraryState: StateFlow<UserLibraryState> = _userLibraryState

    private val userId = MutableStateFlow<String?>(null)

    init {
        CoroutineScope(Dispatchers.IO).launch{
            val profile = context.profileDataStore.data.first()
            userId.value = profile.authId

            userId.value?.let {
                fetchUserBookshelves(userId.value!!)
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

    override suspend fun createBookshelf(bookshelf: SupabaseBookshelf) {
        try {
            _addBookshelfState.update {
                it.copy(successState = SuccessState.LOADING)
            }
            bookshelfNetworkClass.createBookshelf(bookshelf)

            userId.value?.let {
                fetchUserBookshelves(it)
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

    override suspend fun fetchUserBookshelves(userId: String) {
        _userLibraryState.update{
            it.copy(successState = SuccessState.LOADING)
        }
        try {
            _userLibraryState.update {
                it.copy(
                    userBookshelves = bookshelfNetworkClass.fetchUserBookshelves(userId),
                    successState = SuccessState.SUCCESS
                )
            }
        }catch (e: Exception){
            e.printStackTrace()
            _userLibraryState.update{
                it.copy(successState = SuccessState.ERROR)
            }
        }
    }

    override suspend fun fetchBookshelfById(bookshelfId: Int): SupabaseBookshelf? {
        return bookshelfNetworkClass.fetchBookshelfById(bookshelfId)
    }
}