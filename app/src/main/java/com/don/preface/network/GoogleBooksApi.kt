package com.don.preface.network

import com.don.preface.data.model.BookResponse
import com.don.preface.data.model.LibraryResponse
import com.don.preface.data.model.VolumeData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
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

    @GET("mylibrary/bookshelves")
    suspend fun getUserLibrary(
        @Header("Authorization") accessToken: String,
        @Query("key") apiKey: String
    ): LibraryResponse
}
