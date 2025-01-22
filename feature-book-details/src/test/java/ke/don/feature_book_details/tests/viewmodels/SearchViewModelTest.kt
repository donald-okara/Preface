package ke.don.feature_book_details.tests.viewmodels

import junit.framework.TestCase.assertEquals
import ke.don.feature_book_details.fake.data.FakeBooksDataSource.fakeSearchErrorResponseState
import ke.don.feature_book_details.fake.data.FakeBooksDataSource.fakeSearchErrorState
import ke.don.feature_book_details.fake.data.FakeBooksDataSource.fakeSearchSuccessState
import ke.don.feature_book_details.fake.repository.FakeBooksRepository
import ke.don.feature_book_details.presentation.screens.search.SearchState
import ke.don.feature_book_details.presentation.screens.search.SearchViewModel
import ke.don.feature_book_details.rules.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {
    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val repository = FakeBooksRepository()
    private val viewModel = SearchViewModel(
        repository
    )

    @Test
    fun `updateSearchState updates the search state`() = runTest{
        // Arrange
        val searchState = SearchState.Loading

        // Act
        viewModel.updateSearchState(searchState)

        // Assert
        assert(viewModel.searchUiState.value == searchState)

    }

    @Test
    fun `suggestRandomBook updates the suggested book`() = runTest{
        // Arrange

        // Act
        viewModel.suggestRandomBook()

        // Assert
        assert(viewModel.suggestedBook.isNotEmpty())

    }

    @Test
    fun `onLoading updates the search message`() = runTest{
        // Arrange

        // Act
        viewModel.onLoading()

        // Assert
        assert(viewModel.searchMessage.isNotEmpty())

    }

    @Test
    fun `onSearchQueryChange updates the search query`() = runTest{
        // Arrange
        val searchQuery = "Harry Potter"
        // Act
        viewModel.onSearchQueryChange(searchQuery)

        // Assert
        assertEquals(viewModel.searchQuery, searchQuery)

    }

    @Test
    fun `assignSuggestedBook updates the search query`() = runTest {
        // Arrange
        val suggestedBook = "Harry Potter"
        // Act
        viewModel.suggestedBook = suggestedBook
        viewModel.assignSuggestedBook()

        // Assert
        assertEquals(viewModel.searchQuery, suggestedBook)

    }

    @Test
    fun `shuffleBook updates the search query`() = runTest{
        // Arrange
        viewModel.suggestedBook = ""

        // Act
        viewModel.shuffleBook()

        // Assert
        assert(viewModel.searchQuery.isNotEmpty())

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `searchBooks returns Book list`() = runTest{
        // Arrange
        val query = "Harry Potter"
        // Act
        viewModel.searchBooks(query)

        advanceUntilIdle()
        // Assert
        assertEquals(viewModel.searchUiState.value, fakeSearchSuccessState )

    }

    @Test
    fun `searchBooks returns error`() = runTest{
        // Arrange
        val query = "errorQuery"

        // Act
        viewModel.searchBooks(query)

        // Assert
        assertEquals(viewModel.searchUiState.value, fakeSearchErrorResponseState)
    }


    @Test
    fun `searchBooks returns general error on general exception`() = runTest{
        // Arrange
        val query = "emptyQuery"

        // Act
        viewModel.searchBooks(query)

        // Assert
        assertEquals(viewModel.searchUiState.value, fakeSearchErrorState)

    }



}