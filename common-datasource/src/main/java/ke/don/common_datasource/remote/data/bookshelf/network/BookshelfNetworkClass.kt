package ke.don.common_datasource.remote.data.bookshelf.network

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import ke.don.common_datasource.remote.data.bookshelf.repositoryImpl.BookshelfRepositoryImpl
import ke.don.common_datasource.remote.data.bookshelf.repositoryImpl.BookshelfRepositoryImpl.Companion
import ke.don.common_datasource.remote.data.profile.network.ProfileNetworkClass
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

            // Fetch the bookshelf reference (includes books JSONB array)
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

            // Use the indexed JSONB array to extract book IDs
            val bookIds = bookshelfRef.books.map { it.bookId }

            // Fetch full book details efficiently using the indexed JSONB filter
            val books = if (bookIds.isNotEmpty()) {
                supabaseClient.from(BOOKS)
                    .select(){
                        filter { SupabaseBook::bookId isIn  bookIds } // Uses indexing for fast lookups

                    }
                    .decodeList<SupabaseBook>()
            } else {
                emptyList()
            }

            Log.d(TAG, "Fetched books: $books")

            // Return the detailed BookShelf object
            Log.d(TAG, "Bookshelf fetched successfully: $bookshelfRef, $books")
            BookShelf(
                supabaseBookShelf = bookshelfRef,
                books = books
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Error fetching bookshelf with ID: $bookshelfId", e)
            null // Return null on failure
        }
    }

    suspend fun fetchUserBookshelves(userId: String): List<BookShelf> {
        return try {
            Log.d(TAG, "Attempting to fetch bookshelves for user: $userId")

            // Fetch bookshelf references for the user (includes books JSONB array)
            val bookshelfRefs = supabaseClient.from(BOOKSHELFTABLE)
                .select(){
                    filter { BookshelfRef::userId eq userId }

                }
                .decodeList<BookshelfRef>()

            Log.d(TAG, "Fetched bookshelfRefs: $bookshelfRefs")

            // Extract book IDs from all bookshelves
            val bookIds = bookshelfRefs.flatMap { bookshelf -> bookshelf.books.map { it.bookId } }
                .distinct() // Avoid duplicate book queries

            // Fetch all books associated with these book IDs using a single query
            val books = if (bookIds.isNotEmpty()) {
                supabaseClient.from(BOOKS)
                    .select(){
                        filter { SupabaseBook::bookId isIn bookIds }

                    }
                    .decodeList<SupabaseBook>()
            } else {
                emptyList()
            }

            Log.d(TAG, "Fetched books: $books")

            // Map bookshelves to include their respective books
            val bookshelvesDetailed = bookshelfRefs.map { bookshelfRef ->
                val currentBooks = books.filter { book ->
                    bookshelfRef.books.any { it.bookId == book.bookId }
                }

                // Return a detailed BookShelf object
                Log.d(TAG, "Bookshelf assembled: $bookshelfRef with books: $currentBooks")
                BookShelf(
                    supabaseBookShelf = bookshelfRef,
                    books = currentBooks
                )
            }

            return bookshelvesDetailed
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Error fetching bookshelves for user: $userId", e)
            emptyList() // Handle errors gracefully
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