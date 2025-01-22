package ke.don.feature_book_details.network

import ke.don.feature_book_details.data.model.BookDetailsResponse
import ke.don.feature_book_details.data.model.BookListItemResponse
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
