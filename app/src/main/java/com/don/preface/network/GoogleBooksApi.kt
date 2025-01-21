package com.don.preface.network

import com.don.preface.R
import com.don.preface.data.model.BookListItemResponse
import com.don.preface.data.model.LibraryResponse
import com.don.preface.data.model.BookDetailsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
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
