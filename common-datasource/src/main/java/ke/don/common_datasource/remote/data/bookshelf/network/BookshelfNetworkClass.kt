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

    suspend fun fetchUserBookshelves(
        userId: String
    ): List<BookShelf> {
        try {
            Log.d(TAG, "Attempting to fetch bookshelf")

            // Fetch bookshelf references for the user
            val bookshelfRefs = supabaseClient.from(BOOKSHELFTABLE)
                .select {
                    filter {
                        BookshelfRef::userId eq userId
                    }
                }
                .decodeList<BookshelfRef>()

            Log.d(TAG, "Fetched bookshelfRefs: $bookshelfRefs")

            // Fetch all catalogs for the bookshelves in a single query
            Log.d(TAG, "Attempting to fetch bookshelf catalogs")
            val bookshelfCatalogs = supabaseClient.from(BOOKSHELFCATALOG)
                .select {
                    filter {
                        BookshelfCatalog::bookshelfId isIn bookshelfRefs.map { it.id }
                    }
                }
                .decodeList<BookshelfCatalog>()

            Log.d(TAG, "Fetched bookshelf catalogs: $bookshelfCatalogs")

            // Get the list of bookIds from the catalogs
            val bookIds = bookshelfCatalogs.map { it.bookId }

            // Fetch all books associated with these bookIds in a single query
            val books = supabaseClient.from(BOOKS)
                .select {
                    filter {
                        SupabaseBook::bookId isIn bookIds
                    }
                }
                .decodeList<SupabaseBook>()
            Log.d(TAG, "Fetched books: $books")

            // Organize catalogs and books by bookshelfId
            val bookshelvesDetailed = bookshelfRefs.map { bookshelfRef ->
                // Filter catalogs and books for the current bookshelf
                val currentCatalogs = bookshelfCatalogs.filter { it.bookshelfId == bookshelfRef.id }
                val currentBooks = books.filter { book ->
                    currentCatalogs.any { catalog -> catalog.bookId == book.bookId }
                }

                // Return a detailed BookShelf object
                Log.d(TAG, "Bookshelves fetched successfully: $bookshelfRef, $currentCatalogs, $currentBooks")
                BookShelf(
                    supabaseBookShelf = bookshelfRef,
                    catalog = currentCatalogs,
                    books = currentBooks
                )
            }

            // Return the list of bookshelves with their catalogs and books
            return bookshelvesDetailed
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList() // Handle errors by returning an empty list
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