package com.don.preface.data.repositories

import com.don.preface.data.model.BookListItemResponse
import com.don.preface.data.model.BookDetailsResponse
import retrofit2.Response

interface BooksRepository {
    suspend fun searchBooks(query: String): Response<BookListItemResponse>

    suspend fun getBookDetails(bookId: String): Response<BookDetailsResponse>
}

