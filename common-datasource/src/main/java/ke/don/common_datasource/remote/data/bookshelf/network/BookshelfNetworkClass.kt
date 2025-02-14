package ke.don.common_datasource.remote.data.bookshelf.network

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import ke.don.shared_domain.data_models.AddBookToBookshelf
import ke.don.shared_domain.data_models.SupabaseBookshelf
import ke.don.shared_domain.values.ADDBOOKSTOBOOKSHELF
import ke.don.shared_domain.values.BOOKSHELFTABLE

class BookshelfNetworkClass(
    private val supabaseClient: SupabaseClient,
) {
   /**
     * CREATE
     */
    suspend fun createBookshelf(
        bookshelf :SupabaseBookshelf
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

    suspend fun fetchUserBookshelves(
        userId: String
    ): List<SupabaseBookshelf> {
        return try {
            supabaseClient.from(BOOKSHELFTABLE)
                .select{
                    filter {
                        SupabaseBookshelf::userId eq userId
                    }
                }
                .decodeList<SupabaseBookshelf>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun fetchBookshelfById(
        bookshelfId: Int
    ): SupabaseBookshelf?{
        return try {
            supabaseClient.from(BOOKSHELFTABLE)
                .select {
                    filter {
                        SupabaseBookshelf::id eq bookshelfId
                    }
                }
                .decodeSingleOrNull<SupabaseBookshelf>()
                }catch (e: Exception){
                    e.printStackTrace()
                    null
                }
        }

    /**
     * UPDATE
     */
    suspend fun addBookToBookshelf(
        addBookToBookshelf: AddBookToBookshelf
    ){
        try {
            supabaseClient.from(
                ADDBOOKSTOBOOKSHELF
            ).insert(addBookToBookshelf)
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