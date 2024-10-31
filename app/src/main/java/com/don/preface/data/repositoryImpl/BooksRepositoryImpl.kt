package com.don.preface.data.repositoryImpl

import com.don.preface.data.model.BookResponse
import com.don.preface.data.model.VolumeData
import com.don.preface.data.repositories.BooksRepository
import com.don.preface.network.GoogleBooksApi
import retrofit2.Response

class BooksRepositoryImpl(
    private val googleBooksApi: GoogleBooksApi
) : BooksRepository {
    override suspend fun searchBooks(
        query: String
    ): Response<BookResponse> {
        return googleBooksApi.searchBooks(query)
    }

    override suspend fun getBookDetails(bookId: String): VolumeData {
        return googleBooksApi.getBookDetails(bookId)
    }

}