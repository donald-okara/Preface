package ke.don.common_datasource.remote.data.book_details.network

import ke.don.common_datasource.remote.domain.model.BookDetailsResponse
import ke.don.common_datasource.remote.domain.model.BookListItemResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): Response<BookListItemResponse>

    @GET("volumes/{bookId}")
    suspend fun getBookDetails(
        @Path("bookId") bookId: String,
        @Query("key") apiKey: String
    ): Response<BookDetailsResponse>


}
