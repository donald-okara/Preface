package com.don.preface.domain.repositories

import com.don.preface.data.model.BookListItemResponse
import com.don.preface.data.model.BookDetailsResponse
import com.don.preface.domain.states.BookUiState
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response

interface BooksRepository {
    val bookUiState : StateFlow<BookUiState>

    suspend fun searchBooks(query: String): Response<BookListItemResponse>

    suspend fun getBookDetails(bookId: String)

}

