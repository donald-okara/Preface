package ke.don.feature_bookshelf.tests

import ke.don.feature_bookshelf.fake.FakeBookshelf.scienceBookshelfRef
import ke.don.feature_bookshelf.fake.FakeBookshelfRepository
import ke.don.feature_bookshelf.presentation.screens.add_bookshelf.AddBookshelfViewModel
import ke.don.shared_domain.states.AddBookshelfState
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

@ExperimentalCoroutinesApi
class AddBookshelfViewModelTest {
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

    val viewModel = AddBookshelfViewModel(
        bookshelfRepository = FakeBookshelfRepository()
    )

    @Test
    fun `fetchBookshelf returns bookshelf ref when id is passed`() = runTest{
        // Arrange
        val uiState = viewModel.addBookshelfState

        // Act
        viewModel.fetchBookshelf(1)
        advanceUntilIdle()

        // Assert
        assertEquals(scienceBookshelfRef.name, uiState.value.name)
    }

    @Test
    fun `fetchBookshelf does nothing when null id is passed`() = runTest{
        // Arrange
        val uiState = viewModel.addBookshelfState
        // Act
        viewModel.fetchBookshelf(null)
        advanceUntilIdle()

        // Assert
        assertEquals(AddBookshelfState(), uiState.value)
    }

    @Test
    fun `onNameChange updates name`() = runTest{
        // Arrange
        val uiState = viewModel.addBookshelfState
        val newName = "SciFi"

        // Act
        viewModel.onNameChange(newName)

        // Assert
        assertEquals(newName, uiState.value.name)

    }

}