package ke.don.feature_book_details.tests.repositories

import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.remote.data.book_details.repositoryImpl.BooksRepositoryImpl
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.SearchResult
import ke.don.feature_book_details.fake.contracts.FakeColorPaletteExtractor
import ke.don.feature_book_details.fake.data.FakeBookUiState.fakeBookUiStateSuccess
import ke.don.feature_book_details.fake.data.FakeBooksDataSource.fakeSearchSuccessState
import ke.don.feature_book_details.fake.network.FakeBookApiService
import ke.don.feature_book_details.rules.TestDispatcherRule
import ke.don.shared_domain.logger.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class BooksRepositoryTest {
    private val mockLogger: Logger = mock()
    private val mockDao : BookshelfDao = mock()
    private val bookshelfRepository : BookshelfRepository = mock()

    private val repository: BooksRepository =
        BooksRepositoryImpl(
            googleBooksApi = FakeBookApiService(),
            apiKey = "fake_api_key",
            logger = mockLogger,
            colorPaletteExtractor = FakeColorPaletteExtractor(),
            bookshelfRepository = bookshelfRepository,
            bookshelfDao = mockDao
        )

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @Test
    fun `updateSearchState updates the search state`() = runTest{
        // Arrange
        val searchState = repository.searchUiState
        // Act
        repository.updateSearchState(fakeSearchSuccessState)

        // Assert
        assertEquals(searchState.value, fakeSearchSuccessState)
    }

    @Test
    fun `onSearchQueryChange updates the search query`() = runTest{
        // Arrange
        val searchQuery = repository.searchQuery
        // Act
        repository.onSearchQueryChange("Harry Potter")

        // Assert
        assertEquals(searchQuery.value, "Harry Potter")
    }

    @Test
    fun `clearSearch clears the search state`() = runTest{
        // Arrange
        val searchQuery = repository.searchQuery
        val searchState = repository.searchUiState
        repository.onSearchQueryChange("Harry Potter")
        repository.updateSearchState(fakeSearchSuccessState)

        // Act
        repository.clearSearch()

        // Assert
        assertEquals(searchState.value, SearchResult.Empty)
        assertEquals(searchQuery.value, "")
    }

    @Test
    fun `suggestRandomBook fills the suggested book`() = runTest{
        // Arrange
        val suggestedBook = repository.suggestedBook

        // Act
        repository.suggestRandomBook()

        // Assert
        assert(suggestedBook.value.isNotEmpty())
    }

    @Test
    fun `onLoading fills the search message`() = runTest{
        // Arrange
        val searchMessage = repository.searchMessage

        // Act
        repository.onLoading()

        // Assert
        assert(searchMessage.value.isNotEmpty())
    }

    @Test
    fun `assignSuggestedBook fills the search query with suggested book`() = runTest{
        // Arrange
        val searchQuery = repository.searchQuery
        val suggestedTitle = repository.suggestedBook

        // Act
        repository.suggestRandomBook()
        repository.assignSuggestedBook()

        // Assert
        assertEquals(searchQuery.value, suggestedTitle.value)
    }

    @Test
    fun `shuffleBook should update suggested book and search query`() = runTest{
        // Arrange
        val suggestedBook = repository.suggestedBook
        val searchQuery = repository.searchQuery

        // Act
        repository.shuffleBook()

        // Assert
        assert(suggestedBook.value.isNotEmpty())
        assert(searchQuery.value.isNotEmpty())

        assertEquals(suggestedBook.value, searchQuery.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `searchBooks should return a list of books`() = runTest{
        // Arrange
        val query = "Harry Potter"

        // Act
        repository.searchBooks(query)
        advanceUntilIdle()

        // Assert
        assertEquals(
            fakeSearchSuccessState,
            repository.searchUiState.value
        )

    }

    @Test
    fun `assert updateBookState updates the book state`() = runTest{
        // Arrange
        val newBookUiState = fakeBookUiStateSuccess

        // Act
        repository.updateBookState(newBookUiState)

        // Assert
        assertEquals(
            newBookUiState,
            repository.bookUiState.value
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getBookDetails should update the book state with book details`() = runTest{
        // Arrange
        val bookId = "5cu7sER89nwC"
        // Act
        withTimeout(10_000) { // Timeout after 10 seconds
            repository.getBookDetails(bookId)
        }
        advanceUntilIdle() // Ensure all coroutines complete

        // Assert
        assertEquals(
            ResultState.Success,
            repository.bookUiState.value.resultState
        )
    }


    @Test
    fun `getBookDetails should update the book state with error when API call fails`() = runTest{
        // Arrange
        val bookId = "errorExample"

        // Act
        repository.getBookDetails(bookId)

        // Assert
        assertEquals(
            ResultState.Error("Failed to load book details"),
            repository.bookUiState.value.resultState
        )

    }




}