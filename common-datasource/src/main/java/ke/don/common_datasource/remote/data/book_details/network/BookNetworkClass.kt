package ke.don.common_datasource.remote.data.book_details.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.SupabaseBook
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.values.BOOKS

class BookNetworkClass(
    private val supabaseClient: SupabaseClient,
) {
    /**
     * CREATE
     */
    suspend fun addBook(book: SupabaseBook): NetworkResult<NoDataReturned> {
        return try {
            val response = supabaseClient.from(BOOKS)
                .insert(book)

            NetworkResult.Success(NoDataReturned())
        }catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())

        }
    }

    /**
     * READ
     */
    suspend fun fetchBook(bookId: String): NetworkResult<SupabaseBook?>{
        return try {
            val response = supabaseClient.from(BOOKS)
                .select {
                    filter { SupabaseBook::bookId eq bookId }
                }
                .decodeSingleOrNull<SupabaseBook>()

            NetworkResult.Success(response)
        }catch (e: Exception){
            e.printStackTrace()
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
        }
    }
}