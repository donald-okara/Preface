package com.don.preface.data.repositoryImpl

import com.don.preface.data.model.BookListItemResponse
import com.don.preface.data.model.BookDetailsResponse
import com.don.preface.data.repositories.BooksRepository
import com.don.preface.network.GoogleBooksApi
import retrofit2.Response

class BooksRepositoryImpl(
    private val googleBooksApi: GoogleBooksApi
) : BooksRepository {
    override suspend fun searchBooks(
        query: String
    ): Response<BookListItemResponse> {
        return googleBooksApi.searchBooks(query)
    }

    override suspend fun getBookDetails(bookId: String): Response<BookDetailsResponse> {
        return googleBooksApi.getBookDetails(bookId)
    }

}