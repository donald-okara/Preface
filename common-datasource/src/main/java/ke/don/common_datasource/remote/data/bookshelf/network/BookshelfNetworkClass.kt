package ke.don.common_datasource.remote.data.bookshelf.network

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.local.roomdb.entities.toEntity
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfCatalog
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.SupabaseBook
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.values.ADDBOOKSTOBOOKSHELF
import ke.don.shared_domain.values.BOOKS
import ke.don.shared_domain.values.BOOKSHELFCATALOG
import ke.don.shared_domain.values.BOOKSHELFTABLE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class BookshelfNetworkClass(
    private val supabaseClient: SupabaseClient,
) {
   /**
     * CREATE
     */
    suspend fun createBookshelf(
        bookshelf :BookshelfRef
    ): NetworkResult<NoDataReturned>{
        return try {
            val response = supabaseClient.from(BOOKSHELFTABLE).insert(bookshelf){select()}.decodeSingleOrNull<BookshelfRef>()
            Log.d(TAG, "Bookshelf inserted successfully::: $response")
            NetworkResult.Success(NoDataReturned())
        }catch (e: Exception){
            e.printStackTrace()
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
        }
    }

    /**
     * READ
     */

    suspend fun fetchBookshelfRef(bookshelfId: Int): NetworkResult<BookshelfRef>{
        return try{
            val result = supabaseClient.from(BOOKSHELFTABLE)
                .select {
                    filter { BookshelfRef::id eq bookshelfId }
                }
                .decodeSingle<BookshelfRef>()
            NetworkResult.Success(result)
        }catch (e: Exception){
            e.printStackTrace()
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
        }
    }


    suspend fun fetchUserBookshelves(userId: String): NetworkResult<List<BookshelfEntity>> = withContext(Dispatchers.IO){
        return@withContext try {
            val result = supabaseClient.postgrest.rpc(
                function = "fetch_user_bookshelves",
                parameters = mapOf("p_user_id" to userId)
            ).decodeList<BookshelfEntity>()

            NetworkResult.Success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(
                message = e.message ?: "Unknown error",
                hint = e.cause?.toString() ?: "No cause",
                details = e.stackTraceToString()
            )
        }
    }



    /**
     * UPDATE
     */
    suspend fun addBookToBookshelf(
        addBookToBookshelf: AddBookToBookshelf
    ): NetworkResult<NoDataReturned>{
        return try {
            Log.d(TAG, "Attempting to add book")

            supabaseClient.from(
                ADDBOOKSTOBOOKSHELF
            ).insert(addBookToBookshelf)
            NetworkResult.Success(NoDataReturned())
        }catch (e: Exception){
            e.printStackTrace()
            NetworkResult.Error(
                message = e.message.toString(),
                hint = e.cause.toString(),
                details = e.stackTrace.toString()
            )
        }
    }

    suspend fun addMultipleBooksToBookshelf(books: List<AddBookToBookshelf>): NetworkResult<NoDataReturned> {
        return try {
            if (books.isEmpty()) {
                return NetworkResult.Error(
                    message = "Book list is empty",
                    hint = "Ensure that the list contains at least one book before inserting.",
                    details = "Empty lists are not allowed for batch insert."
                )
            }

            supabaseClient.from(ADDBOOKSTOBOOKSHELF)
                .insert(books)

            NetworkResult.Success(NoDataReturned())
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(
                message = e.message.toString(),
                hint = e.cause?.toString() ?: "No cause available",
                details = e.stackTraceToString()
            )
        }
    }

    suspend fun removeBookFromMultipleBookshelves(
        bookId: String,
        bookshelfIds: List<Int>
    ): NetworkResult<NoDataReturned>{
        Log.d(TAG, "Attempting to remove book from bookshelves:: $bookshelfIds")
        return try {
            supabaseClient.from(BOOKSHELFCATALOG).delete {
                filter {
                    BookshelfCatalog::bookshelfId isIn bookshelfIds
                    BookshelfCatalog::bookId eq bookId
                }
            }
            NetworkResult.Success(NoDataReturned())

        }catch (e: Exception){
            e.printStackTrace()
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
        }
    }

    suspend fun removeMultipleBooksFromBookshelf(bookshelfId: Int, bookIds: List<String>): NetworkResult<NoDataReturned>{
        return try {
            supabaseClient.from(BOOKSHELFCATALOG).delete {
                filter {
                    BookshelfCatalog::bookshelfId eq bookshelfId
                    BookshelfCatalog::bookId isIn bookIds
                }
            }
            NetworkResult.Success(NoDataReturned())
        }catch (e: Exception){
            e.printStackTrace()
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
        }
    }


    suspend fun removeBookFromBookshelf(
        bookId: String,
        bookshelfId: Int
    ): NetworkResult<NoDataReturned> {
        return try {
            supabaseClient.from(BOOKSHELFCATALOG).delete {
                filter {
                    BookshelfCatalog::bookshelfId eq bookshelfId
                    BookshelfCatalog::bookId eq bookId
                }
            }
            NetworkResult.Success(NoDataReturned())
        }catch (e: Exception){
            e.printStackTrace()
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
        }
    }

    suspend fun updateBookshelf(bookshelfId: Int,bookshelf: BookshelfRef): NetworkResult<NoDataReturned> {
        return try {
            supabaseClient.from(BOOKSHELFTABLE).update(
                {
                    BookshelfRef::name setTo bookshelf.name
                    BookshelfRef::description setTo bookshelf.description
                    BookshelfRef::bookshelfType setTo bookshelf.bookshelfType
                }
            ){
                filter {
                    BookshelfRef::id eq bookshelfId
                }
            }
            NetworkResult.Success(NoDataReturned())
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
        }
    }

    /**
     * DELETE
     */
    suspend fun deleteBookshelf(
        bookshelfId: Int
    ): NetworkResult<NoDataReturned> {
        return try {
            supabaseClient.from(BOOKSHELFTABLE).delete {
                filter {
                    BookshelfRef::id eq bookshelfId
                }
            }
            NetworkResult.Success(NoDataReturned())
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
        }
    }

    companion object {
        private const val TAG = "BookshelfNetworkClass"
    }
}