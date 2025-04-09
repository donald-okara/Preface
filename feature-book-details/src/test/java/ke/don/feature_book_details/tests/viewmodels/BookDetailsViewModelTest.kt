package ke.don.feature_book_details.tests.viewmodels

import ke.don.common_datasource.remote.domain.states.BookUiState
import ke.don.feature_book_details.fake.contracts.FakeBooksUseCases
import ke.don.feature_book_details.fake.contracts.FakeColorPaletteExtractor
import ke.don.feature_book_details.fake.data.FakeBookDetailsDataSource.fakeBookDetailsResponse
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsViewModel
import ke.don.shared_domain.states.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BookDetailsViewModelTest {
    private val testDispatcher = StandardTestDispatcher() // Use UnconfinedTestDispatcher for testing

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        // Set the Main dispatcher to the test dispatcher
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        // Reset the Main dispatcher after the test
        Dispatchers.resetMain()
    }

    private val viewModel = BookDetailsViewModel(colorPaletteExtractor = FakeColorPaletteExtractor(), booksUseCases = FakeBooksUseCases())
    @Test
    fun `updateBookState updates book state correctly`() = runTest{ //Success case
        // Arrange
        val uiState = viewModel.bookState
        val expectedState = BookUiState(
            resultState = ResultState.Success,
            volumeId = "5cu7sER89nwC",
        )

        // Act
        viewModel.updateBookState(expectedState)

        // Assert
        assert(uiState.value == expectedState)
    }

    @Test
    fun `fetchBookDetails returns book details when volumeId is not null`() = runTest{ // Success case
        // Arrange
        val uiState = viewModel.bookState
        // Act
        viewModel.updateBookState(BookUiState(volumeId = "5cu7sER89nwC"))

        // Assert

        assertEquals(viewModel.fetchBookDetails(), fakeBookDetailsResponse)
    }

    @Test
    fun `fetchBookDetails returns null when volumeId is null`() = runTest{ //Boundary && error case
        // Arrange
        val uiState = viewModel.bookState
        // Act
        viewModel.updateBookState(BookUiState(volumeId = null))

        // Assert
        assertEquals(viewModel.fetchBookDetails(), null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchAndUpdateBookUiState updates volumeId and userID in state`() = runTest{ // Success state
        // Arrange
        val bookId = "5cu7sER89nwC"
        val uiState = viewModel.bookState

        // Act

        viewModel.fetchAndUpdateBookUiState(bookId)
        advanceUntilIdle() // wait for all coroutines to finish


        // Assert
        assertEquals(bookId, uiState.value.volumeId)
        assertEquals("fakeUserId", uiState.value.userId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchAndUpdateBookUiState returns an error result state when volume id is error`() = runTest{
        // Arrange
        val bookId = "erreniousId"
        val uiState = viewModel.bookState

        // Act

        viewModel.fetchAndUpdateBookUiState(bookId)
        advanceUntilIdle() // wait for all coroutines to finish


        // Assert
        assert(uiState.value.resultState is ResultState.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchAndUpdateBookUiState updates book state correctly if volumeId is valid`() = runTest(testDispatcher){
        // Arrange
        val volumeId = "5cu7sER89nwC"
        val uiState = viewModel.bookState
        // Act
        viewModel.fetchAndUpdateBookUiState(volumeId)
        advanceUntilIdle()

        // Assert
        assertEquals(volumeId, uiState.value.volumeId)
        assert(uiState.value.resultState is ResultState.Success)
        assertEquals(fakeBookDetailsResponse, uiState.value.bookDetails)
    }


}

