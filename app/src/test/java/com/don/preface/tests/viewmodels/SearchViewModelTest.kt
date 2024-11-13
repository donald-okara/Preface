package com.don.preface.tests.viewmodels

import com.don.preface.fake.data.FakeBooksDataSource
import com.don.preface.fake.repositories.FakeBookRepository
import com.don.preface.presentation.screens.search.SearchState
import com.don.preface.presentation.screens.search.SearchViewModel
import com.don.preface.rules.TestDispatcherRule
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {
    private val repository = FakeBookRepository()
    private val viewModel = SearchViewModel(repository)

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    @Test
    fun `updateState updates searchState`() = runTest {
        //Given
        val newState = SearchState.Success(emptyList())

        //When
        viewModel.updateSearchState(newState)

        //Then
        assert(viewModel.searchUiState.value == newState)
    }


    @Test
    fun `clearSearch clears searchQuery and updates searchState to Empty`() = runTest {
        // Given
        viewModel.searchQuery = "test"
        viewModel.updateSearchState(SearchState.Success(emptyList()))

        // When
        viewModel.clearSearch()

        // Then
        assert(viewModel.searchQuery.isEmpty())
        assert(viewModel.searchUiState.value is SearchState.Empty)

    }

    @Test
    fun `suggestRandomBook populates suggestedBook`() = runTest {
        //When
        viewModel.suggestRandomBook()

        //Then
        assert(viewModel.suggestedBook.isNotEmpty())
    }

    @Test
    fun `onLoading populates searchMessage`() = runTest {
        //When
        viewModel.onLoading()

        //Then
        assert(viewModel.searchMessage.isNotEmpty())

    }

    @Test
    fun `onSearchQueryChange updates searchQuery`() = runTest {
        //Given
        val newQuery = "newQuery"

        //When
        viewModel.onSearchQueryChange(newQuery)

        //Then
        assertEquals(viewModel.searchQuery, newQuery)

    }

    @Test
    fun `assignSuggestedBool assigns suggestedBook to searchQuery`() = runTest {
        //Given
        viewModel.suggestedBook = "testBook"

        //When
        viewModel.assignSuggestedBook()

        //Then
        assertEquals(viewModel.searchQuery, viewModel.suggestedBook)

    }

    
    @Test
    fun `shuffleBook suggests a new book and updates searchQuery`() = runTest {
        //Given
        viewModel.suggestedBook = "Test suggestion"

        //When
        viewModel.shuffleBook()

        //Then
        assert(viewModel.suggestedBook.isNotEmpty())
        assert(viewModel.searchQuery == viewModel.suggestedBook)
    }

    @Test
    fun `onSearch searches user searchQuery and searchMessage is not empty`() = runTest {
        //Given
        val testQuery = "testQuery"
        viewModel.searchQuery = testQuery

        //When
        viewModel.onSearch()

        //Then
        assert(viewModel.searchQuery.isNotEmpty())
        assertEquals(viewModel.searchQuery , testQuery )
        assert(viewModel.searchMessage.isNotEmpty())
    }

    @Test
    fun `onSearch searchQuery and searchMessage is not empty when user enters no query`() = runTest {
        //Given searchQuery is empty

        //When
        viewModel.onSearch()

        //Then
        assert(viewModel.searchQuery.isNotEmpty())
        assert(viewModel.searchMessage.isNotEmpty())
    }

    @Test
    fun `searchBooks returns success for SearchState`() = runTest {
        //Given
        val testQuery = "testQuery"

        //When
        viewModel.searchBooks(testQuery)

        //Then
        assertEquals(viewModel.searchUiState.value, SearchState.Success(FakeBooksDataSource.fakeBookList.body()!!.items))
    }

    @Test
    fun `searchBooks returns error for SearchState`() = runTest {
        // Given
        val testQuery = "errorQuery"

        // When
        viewModel.searchBooks(testQuery)

        // Then
        assertEquals(
            SearchState.Error("Failed with status: 404"),  viewModel.searchUiState.value
        )
    }

     @Test
    fun `searchBooks returns Exception for SearchState`() = runTest {
        // Given
        val testQuery = "generalExceptionQuery"

        // When
        viewModel.searchBooks(testQuery)

        // Then
        assertEquals(
            SearchState.Error("An error occurred. Check your internet and try again"),  viewModel.searchUiState.value
        )
    }

}