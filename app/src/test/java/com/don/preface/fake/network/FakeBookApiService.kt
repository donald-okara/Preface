package com.don.preface.fake.network

import com.don.preface.data.model.BookDetailsResponse
import com.don.preface.data.model.BookListItemResponse
import com.don.preface.data.model.LibraryResponse
import com.don.preface.fake.data.FakeBookDetailsDataSource
import com.don.preface.fake.data.FakeBooksDataSource
import com.don.preface.network.GoogleBooksApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeBookApiService : GoogleBooksApi {
    override suspend fun searchBooks(
        query: String,
        apiKey: String
    ): Response<BookListItemResponse> {
        return FakeBooksDataSource.fakeBookList
    }

    override suspend fun getBookDetails(
        bookId: String,
        apiKey: String
    ): Response<BookDetailsResponse> {
        return if (bookId == "errorExample") {
            val errorBody = """{"error": "Book not found"}"""
                .toResponseBody("application/json".toMediaTypeOrNull())
            Response.error(404, errorBody)
        } else {
            Response.success(FakeBookDetailsDataSource.fakeBookDetailsResponse)
        }
    }

}