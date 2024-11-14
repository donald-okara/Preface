package com.don.preface.tests.viewmodels

import android.util.Log
import com.don.preface.fake.data.FakeBookDetailsDataSource
import com.don.preface.fake.repositories.FakeBookRepository
import com.don.preface.presentation.screens.book_details.BookDetailsViewModel
import com.don.preface.presentation.screens.book_details.BookState
import com.don.preface.domain.logger.Logger
import com.don.preface.rules.TestDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class BookDetailsViewModelTest {
    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private val mockLogger = mock(Logger::class.java)
    private val repository = FakeBookRepository()
    private val viewModel = BookDetailsViewModel(repository, mockLogger)

    @Test
    fun `updateBookState updates bookState`() = runTest {
        //Given
        val testState = BookState.Loading
        //When
        viewModel.updateBookState(testState)

        //Then
        assertEquals(
            viewModel.bookState.value,
            testState,
        )
    }

    @Test
    fun `clearState sets bookState to empty`() = runTest {
        //Given
        val emptyState = BookState.Empty

        //When
        viewModel.clearState()

        //Then
        assertEquals(
            viewModel.bookState.value,
            emptyState,
        )
    }

    @Test
    fun `onLoading populates loadingJoke`() = runTest {
        //When
        viewModel.onLoading()

        //Then
        assert(
            viewModel.loadingJoke.isNotEmpty()
        )
    }

    @Test
    fun `getBookDetails changes bookState to Success and populates loadingJoke`() = runTest {
        //Given
        val bookId = "testId"
        val expectedResponse = FakeBookDetailsDataSource.fakeBookDetails

        //When
        viewModel.getBookDetails(bookId)

        //Then
        assert(
            viewModel.loadingJoke.isNotEmpty()
        )

        assertEquals(
            viewModel.bookState.value,
            BookState.Success(expectedResponse.body()!!)
        )
    }

    @Test
    fun `getBookDetails returns Failed to load book details error`() = runTest {
        //Given
        val bookId = "errorBookId"
        val expectedResponse = "Failed to load book details"

        //When
        viewModel.getBookDetails(bookId)

        //Then
        assertEquals(
            viewModel.bookState.value,
            BookState.Error(expectedResponse)
        )

    }

    @Test
    fun `getBookDetails returns emptySuccess`() = runTest {
        //Given
        val bookId = "successEmpty"
        val expectedResponse = "No data available"

        //When
        viewModel.getBookDetails(bookId)

        //Then
        assertEquals(
            viewModel.bookState.value,
            BookState.Error(expectedResponse)
        )

    }

    @Test
    fun `getBookDetails returns ConnectionError branch`() = runTest {
        // Given
        val bookId = "errorBookIdConnectionError"
        val expectedResponse = "Network error. Check your internet and try again"

        // When
        viewModel.getBookDetails(bookId)

        // Then
        assertEquals(
            BookState.Error(expectedResponse)
            ,viewModel.bookState.value
        )
    }

    @Test
    fun `getBookDetails returns Exception branch`() = runTest {
        // Given
        val bookId = "errorBookIdGeneralException"
        val expectedResponse = "An error occurred"

        // When
        viewModel.getBookDetails(bookId)

        // Then
        assertEquals(
            BookState.Error(expectedResponse)
            ,viewModel.bookState.value
        )
    }

    

}