package com.don.preface.fake.network

import com.don.preface.data.model.BookDetailsResponse
import com.don.preface.data.model.BookListItemResponse
import com.don.preface.data.model.LibraryResponse
import com.don.preface.fake.data.FakeBookDetailsDataSource
import com.don.preface.fake.data.FakeBooksDataSource
import com.don.preface.network.GoogleBooksApi
import retrofit2.Response

class FakeBookApiService : GoogleBooksApi {
    override suspend fun searchBooks(query: String): Response<BookListItemResponse> = FakeBooksDataSource.fakeBookList

    override suspend fun getBookDetails(bookId: String): Response<BookDetailsResponse> = FakeBookDetailsDataSource.fakeBookDetails

    override suspend fun getUserLibrary(accessToken: String, apiKey: String): LibraryResponse{
        TODO()
    }
}