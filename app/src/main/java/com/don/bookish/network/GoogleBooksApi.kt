package com.don.bookish.network

import com.don.bookish.data.model.BookResponse
import com.don.bookish.data.model.VolumeData
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String
    ): Response<BookResponse>

    @GET("volumes/{bookId}")
    suspend fun getBookDetails(
        @Path("bookId") bookId: String
    ): Response<VolumeData>
}
