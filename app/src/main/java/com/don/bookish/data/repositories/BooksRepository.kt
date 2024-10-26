package com.don.bookish.data.repositories

import com.don.bookish.data.model.BookResponse
import com.don.bookish.data.model.VolumeData
import okhttp3.ResponseBody
import retrofit2.Response

interface BooksRepository {
    suspend fun searchBooks(query: String): Response<BookResponse>

    suspend fun getBookDetails(bookId: String): VolumeData
}

