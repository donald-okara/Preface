package com.don.preface

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.don.preface.data.model.BookItem
import com.don.preface.data.model.BookResponse
import com.don.preface.data.model.VolumeInfo
import com.don.preface.presentation.search.SearchState
import com.don.preface.presentation.search.SearchViewModel
import com.don.preface.data.repositories.BooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SearchViewModel
    private lateinit var repository: BooksRepository


    @Mock
    private lateinit var fakeRepository: BooksRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = mock()
        viewModel = SearchViewModel(fakeRepository)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `clearSearch resets searchQuery and searchUiState`() {
        // Use public function to change searchQuery and modify searchUiState indirectly
        viewModel.onSearchQueryChange("Some query")
        // Here we assume something changed the state, so we can't assign it directly.
        // Instead, simulate a state change by invoking a function that affects the state.
        // In your case, it's enough to invoke clearSearch().
        viewModel.clearSearch()

        // Verify that both searchQuery and searchUiState are reset
        assertEquals("", viewModel.searchQuery)
        assertEquals(SearchState.Empty, viewModel.searchUiState)
    }

    @Test
    fun `onSearchQueryChange updates searchQuery`() {
        val newQuery = "New Query"
        viewModel.onSearchQueryChange(newQuery)
        assertEquals(newQuery, viewModel.searchQuery)

    }

    @Test
    fun `suggestRandomBook updates suggestedBook`() {
        viewModel.suggestRandomBook()
        assertTrue(viewModel.suggestedBook.isNotEmpty())
        assertEquals(viewModel.suggestedBook, viewModel.searchQuery)
    }

    @Test
    fun `onLoading returns a random search message`() {
        viewModel.onLoading()
        assertTrue(viewModel.searchMessage.isNotEmpty())
    }

    @Test
    fun `searchBooks returns success`() = runTest {

        // Mock BookItem and VolumeInfo
        val bookItem = BookItem("1", VolumeInfo("Harry Potter", null, null, null, null, null, null))

        // Mock BookResponse, which is expected by the repository
        val mockBookResponse = BookResponse(
            kind = "books#volumes",
            totalItems = 1,
            items = listOf(bookItem)
        )

        // Mock successful response with BookResponse
        Mockito.`when`(fakeRepository.searchBooks("Harry Potter"))
            .thenReturn(Response.success(mockBookResponse))

        // Call searchBooks
        viewModel.searchBooks("Harry Potter")

        // Advance until idle so coroutine finishes
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert success state
        assertTrue(viewModel.searchUiState is SearchState.Success)
        val successState = viewModel.searchUiState as SearchState.Success
        assertEquals(successState.data[0].volumeInfo.title, "Harry Potter")
    }

    @Test
    fun `searchBooks returns Error state on unsuccessful response`() = runTest {

        // Given
        val query = "test"

        // Create a mock ResponseBody (can be empty if not needed)
        val responseBody = mock<ResponseBody>()

        // Create an unsuccessful response with a 404 status and the mock ResponseBody
        val unsuccessfulResponse = Response.error<BookResponse>(404, responseBody)

        // Use fakeRepository to return the unsuccessful response
        whenever(fakeRepository.searchBooks(query)).thenReturn(unsuccessfulResponse)

        // When
        viewModel.searchBooks(query)

        // Advance the coroutine until all coroutines have completed
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.searchUiState is SearchState.Error)
        assertEquals("Failed with status: 404", (viewModel.searchUiState as SearchState.Error).message)
    }

    @Test
    fun `searchBooks returns Error state on exception`(): Unit = runTest {
        // Given
        val query = "test"
        whenever(repository.searchBooks(query)).thenThrow(RuntimeException("Network error"))

        // When
        viewModel.searchBooks(query)
        // Advance the coroutine until all coroutines have completed
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.searchUiState is SearchState.Error)
        assertEquals("An error occurred. Check your internet and try again", (viewModel.searchUiState as SearchState.Error).message)
    }



    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
