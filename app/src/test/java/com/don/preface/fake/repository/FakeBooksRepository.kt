package com.don.preface.fake.repository

import com.don.preface.data.model.BookListItemResponse
import com.don.preface.domain.repositories.BooksRepository
import com.don.preface.domain.states.BookUiState
import com.don.preface.fake.data.FakeBookUiState.fakeBookUiStateError
import com.don.preface.fake.data.FakeBookUiState.fakeBookUiStateSuccess
import com.don.preface.fake.data.FakeBooksDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeBooksRepository: BooksRepository {
    private val _bookState = MutableStateFlow(BookUiState())
    override val bookUiState: StateFlow<BookUiState> = _bookState

    override suspend fun searchBooks(query: String): Response<BookListItemResponse> {
        return when (query) {
            "errorQuery" -> {
                val errorBody = """{"error": "Book not found"}"""
                    .toResponseBody("application/json".toMediaTypeOrNull())
                Response.error(404, errorBody)
            }
            "emptyQuery" -> {
                throw IllegalArgumentException("Query cannot be empty")
            }
            else -> {
                Response.success(FakeBooksDataSource.fakeBookListItemResponse)
            }
        }

    }

    override suspend fun getBookDetails(bookId: String) {
        if (bookId == "exampleErrorId") {
            _bookState.value = fakeBookUiStateError
        }else {
            _bookState.value = fakeBookUiStateSuccess

        }
    }

    override fun updateBookState(newState: BookUiState) {
        _bookState.update {
            newState
        }
    }


}