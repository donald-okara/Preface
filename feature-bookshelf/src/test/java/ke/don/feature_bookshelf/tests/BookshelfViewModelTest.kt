package ke.don.feature_bookshelf.tests

import ke.don.feature_bookshelf.fake.FakeBookshelfRepository
import ke.don.feature_bookshelf.presentation.screens.bookshelf_details.BookshelfDetailsViewModel
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
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookshelfViewModelTest {
    private val testDispatcher =
        StandardTestDispatcher() // Use UnconfinedTestDispatcher for testing

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

    private val viewModel = BookshelfDetailsViewModel(
        bookshelfRepository = FakeBookshelfRepository()
    )

    @Test
    fun `fetchBookshelf populates state on success`() = runTest{
        // Arrange
        val uiState = viewModel.bookshelfUiState
        val initialState = uiState.value

        // Act
        viewModel.fetchBookshelf(1)
        advanceUntilIdle()

        // Assert
        assertNotEquals(initialState.bookShelf, uiState.value.bookShelf)
    }

    @Test
    fun `fetchBookshelf turns state to error on fail`() = runTest{
        // Arrange
        val uiState = viewModel.bookshelfUiState
        val initialState = uiState.value

        // Act
        viewModel.fetchBookshelf(-1)
        advanceUntilIdle()

        // Assert
        assertEquals(initialState.bookShelf, uiState.value.bookShelf)
        assert(uiState.value.resultState is ResultState.Error)
    }

    @Test
    fun `updateShowSheet toggles show sheet`() = runTest{
        // Arrange
        val uiState = viewModel.bookshelfUiState
        val initialState = uiState.value

        // Act
        viewModel.updateShowSheet()
        advanceUntilIdle()

        // Assert
        assertNotEquals(initialState.showOptionsSheet, uiState.value.showOptionsSheet)
    }

    @Test
    fun `deleteBookshelf navigates back on success`() = runTest{
        // Arrange
        var navBack = false
        val onNavBack = {
            navBack = !navBack
        }

        // Act
        viewModel.deleteBookshelf(onNavigateBack = onNavBack, bookshelfId = 2)
        advanceUntilIdle()

        // Assert
        assertNotEquals(false, navBack)
    }

    @Test
    fun `deleteBookshelf does not navigate back on failure`() = runTest{
        // Arrange
        var navBack = false
        val onNavBack = {
            navBack = !navBack
        }

        // Act
        viewModel.deleteBookshelf(onNavigateBack = onNavBack, bookshelfId = -1)
        advanceUntilIdle()

        // Assert
        assertEquals(false, navBack)
    }


    @Test
    fun `deleteBookshelf updates result state to error on failure`() = runTest{
        // Arrange
        val uiState = viewModel.bookshelfUiState
        var navBack = false
        val onNavBack = {
            navBack = !navBack
        }
        // Act
        viewModel.deleteBookshelf(onNavigateBack = onNavBack, bookshelfId = -1)
        advanceUntilIdle()

        // Assert
        assert(uiState.value.resultState is ResultState.Error)
    }

}