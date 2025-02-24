package ke.don.common_datasource.remote.data.bookshelf.network

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import ke.don.common_datasource.remote.data.bookshelf.repositoryImpl.BookshelfRepositoryImpl
import ke.don.common_datasource.remote.data.bookshelf.repositoryImpl.BookshelfRepositoryImpl.Companion
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfCatalog
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.SupabaseBook
import ke.don.shared_domain.values.ADDBOOKSTOBOOKSHELF
import ke.don.shared_domain.values.BOOKS
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

    suspend fun fetchBookshelfById(bookshelfId: Int): BookShelf? {
        return try {
            Log.d(TAG, "Attempting to fetch bookshelf with ID: $bookshelfId")

            // Fetch the bookshelf reference
            val bookshelfRef = supabaseClient.from(BOOKSHELFTABLE)
                .select(){
                   filter { BookshelfRef::id eq bookshelfId }

                }
                .decodeSingleOrNull<BookshelfRef>() // Get a single bookshelf reference or null

            if (bookshelfRef == null) {
                Log.d(TAG, "No bookshelf found with ID: $bookshelfId")
                return null
            }

            Log.d(TAG, "Fetched bookshelfRef: $bookshelfRef")

            // Fetch bookshelf catalogs
            val bookshelfCatalogs = supabaseClient.from(BOOKSHELFCATALOG)
                .select{
                    filter { BookshelfCatalog::bookshelfId eq bookshelfId }

                }
                .decodeList<BookshelfCatalog>()

            Log.d(TAG, "Fetched bookshelf catalogs: $bookshelfCatalogs")

            // Get the list of bookIds from the catalogs
            val bookIds = bookshelfCatalogs.map { it.bookId }

            val books = supabaseClient.from(BOOKS)
                .select(columns = Columns.list("*", "bookshelf_catalog!inner(book_id)")) {
                    filter {
                        eq("bookshelf_catalog.bookshelf_id", bookshelfId) // Filter by bookshelf_id
                    }
                }
                .decodeList<SupabaseBook>() // Decode into SupabaseBook objects

            Log.d(TAG, "Fetched books: $books")

            // Return the detailed BookShelf object
            Log.d(TAG, "Bookshelf fetched successfully: $bookshelfRef, $bookshelfCatalogs, $books")
            BookShelf(
                supabaseBookShelf = bookshelfRef,
                catalog = bookshelfCatalogs,
                books = books
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Error fetching bookshelf with ID: $bookshelfId", e)
            null // Return null on failure
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