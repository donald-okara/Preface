package ke.don.common_datasource.remote.data.bookshelf.network

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import ke.don.common_datasource.remote.data.bookshelf.repositoryImpl.BookshelfRepositoryImpl
import ke.don.common_datasource.remote.data.bookshelf.repositoryImpl.BookshelfRepositoryImpl.Companion
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookshelfCatalog
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.SupabaseBook
import ke.don.shared_domain.values.ADDBOOKSTOBOOKSHELF
import ke.don.shared_domain.values.BOOKSHELFCATALOG
import ke.don.shared_domain.values.BOOKSHELFTABLE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookshelfNetworkClass(
    private val supabaseClient: SupabaseClient,
) {
   /**
     * CREATE
     */
    suspend fun createBookshelf(
        bookshelf :BookshelfRef
    ){
        try {
            supabaseClient.from(BOOKSHELFTABLE).insert(bookshelf)
            Log.d(TAG, "Bookshelf inserted successfully")

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    /**
     * READ
     */

    suspend fun fetchBookshelfById(
        bookshelfId: Int
    ): BookshelfRef?{
        return try {
            supabaseClient.from(BOOKSHELFTABLE)
                .select {
                    filter {
                        BookshelfRef::id eq bookshelfId
                    }
                }
                .decodeSingleOrNull<BookshelfRef>()
                }catch (e: Exception){
                    e.printStackTrace()
                    null
                }
        }


    suspend fun fetchBooksFromBookshelf(supabaseClient: SupabaseClient, bookshelfId: Long): List<SupabaseBook> {
        return withContext(Dispatchers.IO) {
            try {
                // Step 1: Fetch book IDs from bookshelf_catalog
                val bookIds = supabaseClient.from("bookshelf_catalog")
                    .select{
                        filter { BookshelfCatalog::bookshelfId eq bookshelfId }

                    }
                    .decodeList<BookshelfCatalog>()
                    .map { it.bookId }

                if (bookIds.isEmpty()) {
                    return@withContext emptyList()
                }

                // Step 2: Fetch books from books table where book_id is in bookIds list
                val books = supabaseClient.from("books")
                    .select(){
                        filter { SupabaseBook::bookId isIn(bookIds) }

                    }
                    .decodeList<SupabaseBook>()

                books
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }


    /**
     * UPDATE
     */
    suspend fun addBookToBookshelf(
        addBookToBookshelf: AddBookToBookshelf
    ){
        try {
            Log.d(TAG, "Attempting to add book")

            supabaseClient.from(
                ADDBOOKSTOBOOKSHELF
            ).insert(addBookToBookshelf)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    suspend fun removeBookFromBookshelf(
        bookId: String,
        bookshelfId: Int
    ){
        try {
            supabaseClient.from(BOOKSHELFCATALOG).delete {
                filter {
                    BookshelfCatalog::bookshelfId eq bookshelfId
                    BookshelfCatalog::bookId eq bookId
                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    /**
     * DELETE
     */

    companion object {
        private const val TAG = "BookshelfNetworkClass"
    }
}