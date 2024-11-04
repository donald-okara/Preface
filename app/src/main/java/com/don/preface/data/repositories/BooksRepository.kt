package com.don.preface.data.repositories

import com.don.preface.data.model.BookResponse
import com.don.preface.data.model.VolumeData
import retrofit2.Response

interface BooksRepository {
    suspend fun searchBooks(query: String): Response<BookResponse>

    suspend fun getBookDetails(bookId: String): Response<VolumeData>
}

