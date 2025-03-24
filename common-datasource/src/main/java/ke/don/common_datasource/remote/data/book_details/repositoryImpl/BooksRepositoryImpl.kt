package ke.don.common_datasource.remote.data.book_details.repositoryImpl

import android.content.Context
import android.util.Log
import android.widget.Toast
import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.remote.data.book_details.network.GoogleBooksApi
import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass.Companion
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.states.BookUiState
import ke.don.common_datasource.remote.domain.states.BookshelfBookDetailsState
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.BookItem
import ke.don.shared_domain.states.NetworkResult
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class BooksRepositoryImpl(
    private val bookshelfNetworkClass: BookshelfNetworkClass,
    private val googleBooksApi: GoogleBooksApi,
    private val apiKey: String,
    private val context : Context,
    private val bookshelfDao: BookshelfDao,
    private val bookshelfRepository: BookshelfRepository,
) : BooksRepository {

    override suspend fun getBookDetails(bookId: String): NetworkResult<BookDetailsResponse> {
        return try {
            // Fetch book details from the API
            val response: Response<BookDetailsResponse> =
                googleBooksApi.getBookDetails(bookId, apiKey)

            if (response.isSuccessful) {
                NetworkResult.Success(response.body() ?: BookDetailsResponse())
            } else {
                Toast.makeText(
                    context,
                    "${response.code()}: ${response.message()}",
                    //"Something went wrong. Please check your internet and try again",
                    Toast.LENGTH_SHORT
                ).show()

                NetworkResult.Error(
                    message = response.message(),
                    code = response.code().toString(),
                    hint = response.code().toString(),
                    details = response.body().toString()
                )
            }

        } catch (e: Exception) {
            // Handle network error
            NetworkResult.Error(
                message = "Something went wrong. Please check your internet and try again",
            )
        }
    }

     override suspend fun fetchBookshelves(): Flow<List<BookshelfEntity>> {
         return bookshelfDao.getAllBookshelvesFlow()
    }


    override suspend fun pushEditedBookshelfBooks(
        bookId: String,
        bookshelfIds: List<Int>,
        addBookshelves: List<AddBookToBookshelf>
    ): NetworkResult<NoDataReturned> = try {
        Log.d(TAG, "Attempting to remove book from bookshelves:: $bookshelfIds")

        bookshelfNetworkClass.removeBookFromMultipleBookshelves(bookId, bookshelfIds).also { result ->
            if (result is NetworkResult.Error) {
                Toast.makeText(context, "${result.message} ${result.hint ?: ""}", Toast.LENGTH_SHORT).show()
                return result
            }
        }

        if (addBookshelves.isNotEmpty()) {
            bookshelfNetworkClass.addMultipleBooksToBookshelf(addBookshelves).also { result ->
                if (result is NetworkResult.Error) {
                    Toast.makeText(context, "${result.message} ${result.hint ?: ""}", Toast.LENGTH_SHORT).show()
                    return result
                }
            }
        }

        bookshelfRepository.syncLocalBookshelvesDb()
    } catch (e: Exception) {
        NetworkResult.Error(
            message = e.message.orEmpty(),
            hint = e.cause?.toString().orEmpty(),
            details = e.stackTraceToString()
        )
    }


    /**
     *   SearchBook funs
     *
     */

    // New function to get a random book suggestion

    override suspend fun searchBooks(query: String): NetworkResult<List<BookItem>> {
        return try {
            val response = googleBooksApi.searchBooks(query, apiKey)

            if (response.isSuccessful) {
                NetworkResult.Success(response.body()?.items ?: emptyList())
            } else {
                NetworkResult.Error(
                    message = response.message(),
                    code = response.code().toString(),
                    hint = response.code().toString(),
                    details = response.body().toString()
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                message = "Something went wrong. Please check your internet and try again",
            )
        }
    }



    companion object {
        const val TAG = "BooksRepositoryImpl"
    }

}