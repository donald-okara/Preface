package com.don.preface

import android.util.Log
import com.don.preface.data.model.VolumeData
import com.don.preface.data.repositories.BooksRepository
import com.don.preface.fake.createFakeVolumeData
import com.don.preface.presentation.book_details.BookDetailsViewModel
import com.don.preface.presentation.book_details.BookState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

class BookDetailsViewModelTest {

    private lateinit var viewModel: BookDetailsViewModel
    private lateinit var repository: BooksRepository
    private val testDispatcher = StandardTestDispatcher()


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockStatic(Log::class.java)

        repository = mock()
        viewModel = BookDetailsViewModel(
            repository
        )
        Dispatchers.setMain(testDispatcher)

    }
    @Test
    fun `onLoading is not empty`() = runBlocking {
        viewModel.onLoading()

        assertTrue(viewModel.loadingJoke.isNotEmpty())

    }

    @Test
    fun `getBookDetails success - updates bookState to Success`() = runTest {
        // Given
        val bookId = "5cu7sER89nwC"
        val mockVolumeData = createFakeVolumeData()
        val mockResponse = Response.success(mockVolumeData)
        whenever(repository.getBookDetails(bookId)).thenReturn(mockResponse)

        // When
        viewModel.getBookDetails(bookId)
        testDispatcher.scheduler.advanceUntilIdle()  // Ensures all coroutines complete

        // Then
        assertEquals(BookState.Success(mockVolumeData), viewModel.bookState)
        verify(repository).getBookDetails(bookId)
    }


    @Test
    fun `getBookDetails returns error on exception`() = runBlocking {
        // Given
        whenever(repository.getBookDetails("5cu7sER89nwC")).thenThrow(RuntimeException("Something went wrong. Try again."))

        viewModel.getBookDetails("5cu7sER89nwC")

        // Advance the coroutine until all coroutines have completed
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assert(viewModel.bookState is BookState.Error)
        assert((viewModel.bookState as BookState.Error).message == "Something went wrong. Try again.")

    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}