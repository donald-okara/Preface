package com.don.bookish.data.repositoryImpl

import com.don.bookish.data.model.BookResponse
import com.don.bookish.data.model.VolumeData
import com.don.bookish.data.repositories.BooksRepository
import com.don.bookish.network.GoogleBooksApi
import okhttp3.ResponseBody
import retrofit2.Response

class BooksRepositoryImpl(
    private val googleBooksApi: GoogleBooksApi
) : BooksRepository {
    override suspend fun searchBooks(
        query: String
    ): Response<BookResponse> {
        return googleBooksApi.searchBooks(query)
    }

    override suspend fun getBookDetails(bookId: String): Response<VolumeData> {
        return googleBooksApi.getBookDetails(bookId)
    }

}