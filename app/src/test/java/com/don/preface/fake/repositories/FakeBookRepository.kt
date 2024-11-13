package com.don.preface.fake.repositories

import com.don.preface.data.model.BookDetailsResponse
import com.don.preface.data.model.BookListItemResponse
import com.don.preface.data.repositories.BooksRepository
import com.don.preface.fake.data.FakeBookDetailsDataSource
import com.don.preface.fake.data.FakeBooksDataSource
import retrofit2.Response
import java.net.ConnectException

class FakeBookRepository: BooksRepository {
    override suspend fun searchBooks(query: String): Response<BookListItemResponse> {
        return if (query == "errorQuery") {
            Response.error(404, okhttp3.ResponseBody.create(null, "Not Found"))

        } else if (query == "generalExceptionQuery") {
            throw Exception("An error occurred. Check your internet and try again")
        }
        else{
            FakeBooksDataSource.fakeBookList
        }
    }

    override suspend fun getBookDetails(bookId: String): Response<BookDetailsResponse> {
        return when (bookId) {
            "errorBookId" -> {
                Response.error(404, okhttp3.ResponseBody.create(null, "Failed to load book details"))
            }
            "errorBookIdConnectionError" -> {
                // Simulates connection error
                throw ConnectException("Network error. Check your internet and try again")
            }"errorBookIdGeneralException" -> {
                // Simulates connection error
                throw Exception("An error occurred")
            }
            "successEmpty" -> {
                // Simulates successful response with empty body
                Response.success<BookDetailsResponse>(null)
            }
            else -> {
                // Simulate successful response with actual data
                FakeBookDetailsDataSource.fakeBookDetails
            }
        }
    }
}