package ke.don.feature_bookshelf.tests

import ke.don.feature_bookshelf.fake.FakeBookshelfRepository
import ke.don.feature_bookshelf.presentation.screens.user_library.UserLibraryViewModel
import ke.don.shared_domain.data_models.BookShelf
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
class UserLibraryViewModelTest {
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

    val viewModel = UserLibraryViewModel(
        bookshelfRepository = FakeBookshelfRepository()
    )

    @Test
    fun `fetchUserBookShelves fetches bookshelves`() = runTest{
        // Arrange
        val uiState = viewModel.userLibraryState

        // Act
        viewModel.fetchUserBookShelves()
        advanceUntilIdle()

        // Assert
        assertNotEquals(emptyList<BookShelf>(), uiState.value.userBookshelves)
    }

    @Test
    fun `refreshAction calls onRefreshComplete`() = runTest{
        // Arrange
        val uiState = viewModel.userLibraryState
        var refreshed = false
        val onRefreshComplete = {
            refreshed = !refreshed
        }

        // Act
        viewModel.refreshAction(onRefreshComplete)
        advanceUntilIdle()

        // Assert
        assertNotEquals(false, refreshed)
    }

    @Test
    fun `updateSelectedBookshelf updates selectedBookshelfId`() = runTest{
        // Arrange
        val uiState = viewModel.userLibraryState
        val initialState = uiState.value

        // Act
        viewModel.onShowBottomSheet(2)
        advanceUntilIdle()

        // Assert
        assertNotEquals(initialState.selectedBookshelfId, uiState.value.selectedBookshelfId)
    }

    @Test
    fun `updateShowSheet toggles show sheet`() = runTest{
        // Arrange
        val uiState = viewModel.userLibraryState
        val initialState = uiState.value
        // Act
        viewModel.updateShowSheet(true)
        advanceUntilIdle()

        // Assert
        assertNotEquals(initialState.showOptionsSheet, uiState.value.showOptionsSheet)
    }

    @Test
    fun `deleteBookshelf refreshes state on success`() = runTest{
        // Arrange
        val uiState = viewModel.userLibraryState
        var refreshed = false
        val onRefreshComplete = {
            refreshed = !refreshed
        }

        // Act
        viewModel.deleteBookshelf(onRefreshComplete, 3)
        advanceUntilIdle()

        // Assert
        assertNotEquals(false, refreshed)
    }

    @Test
    fun `deleteBookshelf does not refresh state on error`() = runTest{
        // Arrange
        val uiState = viewModel.userLibraryState
        var refreshed = false
        val onRefreshComplete = {
            refreshed = !refreshed
        }

        // Act
        viewModel.deleteBookshelf(onRefreshComplete, -1)
        advanceUntilIdle()

        // Assert
        assertEquals(false, refreshed)
    }
}