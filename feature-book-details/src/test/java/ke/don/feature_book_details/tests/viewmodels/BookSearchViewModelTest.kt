package ke.don.feature_book_details.tests.viewmodels

import ke.don.feature_book_details.fake.contracts.FakeBooksRepository
import ke.don.feature_book_details.fake.data.FakeBooksDataSource.fakeBookListItemResponse
import ke.don.feature_book_details.presentation.screens.search.SearchViewModel
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.SearchState
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

@OptIn(ExperimentalCoroutinesApi::class)
class BookSearchViewModelTest {
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

    val viewModel = SearchViewModel(
        repository = FakeBooksRepository()
    )

    @Test
    fun `updateSearchState updates search state`() = runTest{
        // Arrange
        val uiState = viewModel.searchUiState
        val expectedState = SearchState(
            resultState = ResultState.Success,
            searchQuery = "Example query"
        )

        // Act
        viewModel.updateSearchState(
            expectedState
        )
        advanceUntilIdle()
        // Assert

        assertEquals(expectedState, uiState.value)
    }


    @Test
    fun `clearSearch clears search state and updates suggested book`() = runTest{
        // Arrange
        val uiState = viewModel.searchUiState

        // Act
        viewModel.clearSearch()
        advanceUntilIdle()

        // Assert
        assert(uiState.value.resultState is ResultState.Empty)
        assert((uiState.value.suggestedBook.isNotEmpty()))
    }

    @Test
    fun `onLoading populates search message and updates state to loading`() = runTest{
        // Arrange
        val uiState = viewModel.searchUiState

        // Act
        viewModel.onLoading()
        advanceUntilIdle()

        // Assert
        assert(uiState.value.resultState is ResultState.Loading)
        assert(uiState.value.searchMessage.isNotEmpty())
    }

    @Test
    fun `onSearchQueryChange updates search query`() = runTest{
        // Arrange
        val uiState = viewModel.searchUiState
        val expectedQuery = "Harry po'ah"

        // Act
        viewModel.onSearchQueryChange(expectedQuery)
        advanceUntilIdle()

        // Assert
        assertEquals(expectedQuery, uiState.value.searchQuery)
    }

    @Test
    fun `assignSuggestedBook assigns suggested book to query`() = runTest{
        // Arrange
        val uiState = viewModel.searchUiState
        val expectedQuery = "Harry po'ah"
        viewModel.updateSearchState(
            uiState.value.copy(
                suggestedBook = expectedQuery
            )
        )

        // Act
        viewModel.assignSuggestedBook()
        advanceUntilIdle()

        // Assert
        assertEquals(expectedQuery, uiState.value.searchQuery)
    }

    @Test
    fun `shuffleBook suggests and assigns book to query`() = runTest{
        // Arrange
        val uiState = viewModel.searchUiState

        // Act
        viewModel.shuffleBook()

        // Assert
        assert(uiState.value.searchQuery.isNotEmpty())
    }

    @Test
    fun `onSearch populates search data`() = runTest{
        // Arrange
        val uiState = viewModel.searchUiState
        val expectedQuery = "Harry po'ah"

        viewModel.onSearchQueryChange(expectedQuery)

        // Act
        viewModel.onSearch()
        advanceUntilIdle()

        // Assert
        assertEquals(fakeBookListItemResponse, uiState.value.data)
    }

    @Test
    fun `onSearch changes state to error`() = runTest{
        // Arrange
        val uiState = viewModel.searchUiState

        // Act
        viewModel.onSearch()
        advanceUntilIdle()

        // Assert
        assert(uiState.value.resultState is ResultState.Error)
        assert(uiState.value.data.isEmpty())
    }

}