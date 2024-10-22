package com.don.bookish

import com.don.bookish.data.model.BookResponse
import com.don.bookish.data.model.VolumeData
import com.don.bookish.data.repositories.BooksRepository
import com.don.bookish.fake.createFakeVolumeData
import com.don.bookish.presentation.book_details.BookDetailsViewModel
import com.don.bookish.presentation.book_details.BookState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
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

        repository = mock()
        viewModel = BookDetailsViewModel(
            repository
        )
        Dispatchers.setMain(testDispatcher)

    }

    @Test
    fun `getBookDetails returns success`() = runBlocking{
        // Given
        val mockResponse = createFakeVolumeData()
        Mockito.`when`(repository.getBookDetails("5cu7sER89nwC"))
            .thenReturn(Response.success(mockResponse))

        // When
        viewModel.getBookDetails("5cu7sER89nwC")

        // Advance the coroutine until all coroutines have completed
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assert(viewModel.bookState is BookState.Success)
        val successState = viewModel.bookState as BookState.Success
        assert(successState.data.id == "5cu7sER89nwC")
    }

    @Test
    fun `getBookDetails returns error on unsuccessful response`() = runBlocking {
        // Create a mock ResponseBody (can be empty if not needed)
        val responseBody = mock<ResponseBody>()

        val unsuccessfulResponse = Response.error<VolumeData>(404, responseBody)

        // Given
        whenever(repository.getBookDetails("5cu7sER89nwC")).thenReturn(unsuccessfulResponse)

        // Act
        viewModel.getBookDetails("5cu7sER89nwC")

        // Advance the coroutine until all coroutines have completed
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assert(viewModel.bookState is BookState.Error)
        assert((viewModel.bookState as BookState.Error).message == "Error: 404")


    }

    @Test
    fun `getBookDetails returns error on exception`() = runBlocking {
        // Given
        whenever(repository.getBookDetails("5cu7sER89nwC")).thenThrow(RuntimeException("Network error"))

        viewModel.getBookDetails("5cu7sER89nwC")

        // Advance the coroutine until all coroutines have completed
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assert(viewModel.bookState is BookState.Error)
        assert((viewModel.bookState as BookState.Error).message == "Network error: Network error")

    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}