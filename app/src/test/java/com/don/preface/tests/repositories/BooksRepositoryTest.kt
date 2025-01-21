package com.don.preface.tests.repositories

import com.don.preface.data.repositoryImpl.BooksRepositoryImpl
import com.don.preface.domain.logger.Logger
import com.don.preface.domain.repositories.BooksRepository
import com.don.preface.domain.states.ResultState
import com.don.preface.fake.contracts.FakeColorPaletteExtractor
import com.don.preface.fake.data.FakeBookUiState.fakeBookUiStateError
import com.don.preface.fake.data.FakeBookUiState.fakeBookUiStateSuccess
import com.don.preface.fake.data.FakeBooksDataSource
import com.don.preface.fake.network.FakeBookApiService
import com.don.preface.rules.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class BooksRepositoryTest {
    private val mockLogger: Logger = mock()

    private val repository: BooksRepository = BooksRepositoryImpl(
        googleBooksApi = FakeBookApiService(),
        apiKey = "fake_api_key",
        logger = mockLogger,
        colorPaletteExtractor = FakeColorPaletteExtractor()
    )

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @Test
    fun `searchBooks should return a list of books`() = runTest{
        // Arrange
        val query = "Harry Potter"
        // Act
        val response = repository.searchBooks(query)

        // Assert
        assert(response.isSuccessful)
        assert(response.body()?.items?.isNotEmpty() == true)
    }

    @Test
    fun `assert updateBookState updates the book state`() = runTest{
        // Arrange
        val newBookUiState = fakeBookUiStateSuccess

        // Act
        repository.updateBookState(newBookUiState)

        // Assert
        assertEquals(
            newBookUiState,
            repository.bookUiState.value
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getBookDetails should update the book state with book details`() = runTest{
        // Arrange
        val bookId = "5cu7sER89nwC"
        // Act
        withTimeout(10_000) { // Timeout after 10 seconds
            repository.getBookDetails(bookId)
        }
        advanceUntilIdle() // Ensure all coroutines complete

        // Assert
        assertEquals(
            ResultState.Success,
            repository.bookUiState.value.resultState
        )
    }


    @Test
    fun `getBookDetails should update the book state with error when API call fails`() = runTest{
        // Arrange
        val bookId = "errorExample"

        // Act
        repository.getBookDetails(bookId)

        // Assert
        assertEquals(
            ResultState.Error("Failed to load book details"),
            repository.bookUiState.value.resultState
        )

    }




}