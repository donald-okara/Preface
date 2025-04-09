package ke.don.feature_book_details.tests.viewmodels

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.remote.domain.states.BookUiState
import ke.don.common_datasource.remote.domain.states.BookshelfBookDetailsState
import ke.don.common_datasource.remote.domain.states.BookshelvesState
import ke.don.common_datasource.remote.domain.states.ShowOptionState
import ke.don.common_datasource.remote.domain.states.UserProgressState
import ke.don.feature_book_details.fake.contracts.FakeBooksUseCases
import ke.don.feature_book_details.fake.contracts.FakeColorPaletteExtractor
import ke.don.feature_book_details.fake.data.FakeBookDetailsDataSource.fakeBookDetailsResponse
import ke.don.feature_book_details.fake.data.FakeBookshelfState.fakeBookshelvesState
import ke.don.feature_book_details.fake.data.FakeUserProgress.fakeUserProgressState
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsViewModel
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.VolumeInfoDet
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

class BookDetailsViewModelTest {
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

    private val viewModel = BookDetailsViewModel(
        colorPaletteExtractor = FakeColorPaletteExtractor(),
        booksUseCases = FakeBooksUseCases()
    )

    @Test
    fun `updateBookState updates book state correctly`() = runTest { //Success case
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
    fun `fetchBookDetails returns book details when volumeId is not null`() =
        runTest { // Success case
            // Arrange
            // Act
            viewModel.updateBookState(BookUiState(volumeId = "5cu7sER89nwC"))

            // Assert

            assertEquals(viewModel.fetchBookDetails(), fakeBookDetailsResponse)
        }

    @Test
    fun `fetchBookDetails returns null when volumeId is null`() = runTest { //Boundary && error case
        // Arrange
        // Act
        viewModel.updateBookState(BookUiState(volumeId = null))

        // Assert
        assertEquals(viewModel.fetchBookDetails(), null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchAndUpdateBookUiState updates volumeId and userID in state`() =
        runTest { // Success state
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
    fun `fetchAndUpdateBookUiState returns an error result state when volume id is error`() =
        runTest {
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
    fun `fetchAndUpdateBookUiState updates book state correctly if volumeId is valid`() =
        runTest(testDispatcher) {
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `refreshAction reloads the bookUiState`() = runTest {
        // Arrange
        val volumeId = "5cu7sER89nwC"
        val uiState = viewModel.bookState
        viewModel.updateBookState(BookUiState(volumeId = volumeId))

        // Act
        viewModel.refreshAction()
        advanceUntilIdle()

        // Assert
        assertEquals(volumeId, uiState.value.volumeId)
        assert(uiState.value.resultState is ResultState.Success)
        assertEquals(fakeBookDetailsResponse, uiState.value.bookDetails)
    }

    @Test
    fun `onLoading updates loading joke`() = runTest {
        // Arrange
        val uiState = viewModel.bookState
        viewModel.updateBookState(BookUiState(loadingJoke = ""))

        // Act
        viewModel.onLoading()

        // Assert
        assertNotEquals("", uiState.value.loadingJoke)
    }

    @Test
    fun `onCurrentPageUpdate updates book pages if progress is less than total pages`() = runTest {
        // Arrange
        val uiState = viewModel.bookState

        //Page count set to 50
        viewModel.updateBookState(
            BookUiState(
                bookDetails = BookDetailsResponse(
                    volumeInfo = VolumeInfoDet(
                        pageCount = 50
                    )
                )
            )
        )

        // Act
        viewModel.onCurrentPageUpdate(5)

        // Assert
        assertEquals(false, uiState.value.userProgressState.isError)
        assertEquals(5, uiState.value.userProgressState.newProgress)
    }


    @Test
    fun `onCurrentPageUpdate returns error if progress is more than total pages`() = runTest {
        // Arrange
        val uiState = viewModel.bookState

        //Page count set to 50
        viewModel.updateBookState(
            BookUiState(
                bookDetails = BookDetailsResponse(
                    volumeInfo = VolumeInfoDet(
                        pageCount = 50
                    )
                )
            )
        )

        // Act
        viewModel.onCurrentPageUpdate(51)

        // Assert
        assertEquals(true, uiState.value.userProgressState.isError)
        assertEquals(0, uiState.value.userProgressState.newProgress)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchUserProgress fetches user progress`() = runTest {
        // Arrange
        val bookId = "5cu7sER89nwC"
        val userId = "fakeUserId"
        val uiState = viewModel.bookState

        viewModel.updateBookState(BookUiState(volumeId = bookId, userId = userId))

        // Act
        viewModel.fetchUserProgress()
        advanceUntilIdle()


        // Assert
        assertEquals(fakeUserProgressState, uiState.value.userProgressState)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchUserProgress does not update state if it is already success`() = runTest {
        // Arrange
        val bookId = "5cu7sER89nwC"
        val userId = "fakeUserId"
        val uiState = viewModel.bookState

        viewModel.updateBookState(
            BookUiState(
                volumeId = bookId,
                userId = userId,
                userProgressState = UserProgressState(resultState = ResultState.Success)
            )
        )
        // Act
        viewModel.fetchUserProgress()
        advanceUntilIdle()

        // Assert
        assertNotEquals(fakeUserProgressState, uiState.value.userProgressState)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchUserProgress returns error if no data is returned from use cases`() = runTest {
        // Arrange
        val bookId = "null"
        val userId = "null"
        val uiState = viewModel.bookState

        viewModel.updateBookState(BookUiState(volumeId = bookId, userId = userId))

        // Act
        viewModel.fetchUserProgress()
        advanceUntilIdle()

        // Assert
        assert(uiState.value.userProgressState.resultState is ResultState.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `updateProgressDialogState toggles show option if toggle is true`() = runTest {
        // Arrange
        val toggle = true
        val uiState = viewModel.bookState
        val currentShowOptionSnapshot = uiState.value.showUpdateProgressDialog.showOption

        // Act

        viewModel.updateProgressDialogState(toggle = toggle)
        advanceUntilIdle()

        // Assert
        assertNotEquals(
            uiState.value.showUpdateProgressDialog.showOption,
            currentShowOptionSnapshot
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSaveBookProgress updates existing progress successfully`() = runTest {
        // Arrange
        val uiState = viewModel.bookState
        val bookId = "5cu7sER89nwC"
        val userId = "user123"
        val newProgress = 30
        val totalPages = 1000

        viewModel.updateBookState(
            BookUiState(
                volumeId = bookId,
                userId = userId,
                bookDetails = BookDetailsResponse(volumeInfo = VolumeInfoDet(pageCount = totalPages)),
                userProgressState = UserProgressState(
                    newProgress = newProgress,
                    isPresent = true
                )
            )
        )

        // Act
        viewModel.onSaveBookProgress()
        advanceUntilIdle()

        // Assert
        assertEquals(fakeUserProgressState, uiState.value.userProgressState)
        assert(uiState.value.userProgressState.resultState is ResultState.Success)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSaveBookProgress updates existing progress successfully when book isn't present`() = runTest {
        // Arrange
        val uiState = viewModel.bookState
        val bookId = "5cu7sER89nwC"
        val userId = "user123"
        val newProgress = 30
        val totalPages = 1000

        viewModel.updateBookState(
            BookUiState(
                volumeId = bookId,
                userId = userId,
                bookDetails = BookDetailsResponse(volumeInfo = VolumeInfoDet(pageCount = totalPages)),
                userProgressState = UserProgressState(
                    newProgress = newProgress,
                    isPresent = false
                )
            )
        )

        // Act
        viewModel.onSaveBookProgress()
        advanceUntilIdle()

        // Assert
        assertEquals(fakeUserProgressState, uiState.value.userProgressState)
        assert(uiState.value.userProgressState.resultState is ResultState.Success)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onSelectBookshelf selects a bookshelf`() = runTest{
        // Arrange
        val uiState = viewModel.bookState
        val initialUiState = uiState.value
        viewModel.updateBookState(
            BookUiState(
                bookshelvesState = fakeBookshelvesState
            )
        )

        // Act
        viewModel.onSelectBookshelf(1)
        advanceUntilIdle()

        // Assert
        assertNotEquals(uiState.value, initialUiState)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onToggleBookshelfDropDown toggles show option and fetches bookshelves`() = runTest{
        // Arrange
        val uiState = viewModel.bookState
        val bookId = "5cu7sER89nwC"

        viewModel.updateBookState(BookUiState(volumeId = bookId, showBookshelvesDropDown = ShowOptionState(showOption = true)))
        val initialState = uiState.value

        // Act
        viewModel.onToggleBookshelfDropDown()
        advanceUntilIdle()

        // Assert
        assertNotEquals(uiState.value, initialState)
        assertEquals(fakeBookshelvesState, uiState.value.bookshelvesState)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchBookshelves fetches bookshelves when volumeId isn't null and initial state is set`() = runTest{
        // Arrange
        val uiState = viewModel.bookState
        val bookId = "5cu7sER89nwC"

        viewModel.updateBookState(
            BookUiState(
                volumeId = bookId
            )
        )
        val initialState = uiState.value

        // Act
        viewModel.fetchBookshelves()
        advanceUntilIdle()

        // Assert
        assertEquals(fakeBookshelvesState, uiState.value.bookshelvesState)
        assertEquals(fakeBookshelvesState, viewModel.initialBookshelfState)
        assertNotEquals(initialState, viewModel.initialBookshelfState)
    }

    @Test
    fun `fetchBookshelves doesn't fetch anything if it is already successful`() = runTest{
        // Arrange
        val uiState = viewModel.bookState
        val bookId = "5cu7sER89nwC"
        viewModel.updateBookState(
            BookUiState(
                volumeId = bookId,
                bookshelvesState = BookshelvesState(resultState = ResultState.Success)
            )
        )
        val initialState = uiState.value

        // Act
        viewModel.fetchBookshelves()

        // Assert
        assertEquals(initialState, uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onShowBottomSheet toggles the bottom sheet `() = runTest{
        // Arrange
        val uiState = viewModel.bookState
        val initialState = uiState.value.showBottomSheet.showOption

        // Act
        viewModel.onShowBottomSheet()
        advanceUntilIdle()

        // Assert
        assertNotEquals(initialState, uiState.value.showBottomSheet.showOption)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onPushEditedBookshelfBooks triggers push with correct data`() = runTest {
        // Arrange
        val bookId = "book123"
        val userId = "user1223"

        val initialState = BookUiState(
            volumeId = bookId,
            userId = userId,
            bookDetails = BookDetailsResponse(id = bookId),
            bookshelvesState = BookshelvesState(
                bookshelves = listOf(
                    BookshelfBookDetailsState(bookshelfBookDetails = BookshelfEntity(id = 1), isBookPresent = false),
                    BookshelfBookDetailsState(bookshelfBookDetails = BookshelfEntity(id = 2), isBookPresent = true)
                )
            ),
            showBookshelvesDropDown = ShowOptionState(),
            resultState = ResultState.Success
        )

        viewModel.initialBookshelfState = BookshelvesState(
            bookshelves = listOf(
                BookshelfBookDetailsState(bookshelfBookDetails = BookshelfEntity(id = 1), isBookPresent = true),
                BookshelfBookDetailsState(bookshelfBookDetails = BookshelfEntity(id = 2), isBookPresent = false)
            )
        )

        viewModel.updateBookState(initialState)

        // Act
        viewModel.onPushEditedBookshelfBooks()
        advanceUntilIdle()

        // Assert
        val uiState = viewModel.bookState
        assertEquals(fakeBookshelvesState, uiState.value.bookshelvesState)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onCleared resets the state`() = runTest{
        // Arrange
        val uiState = viewModel.bookState
        val bookId = "5cu7sER89nwC"
        viewModel.updateBookState(
            BookUiState(
                volumeId = bookId,
                bookshelvesState = BookshelvesState(resultState = ResultState.Success)
            )
        )
        val initialState = uiState.value

        // Act
        viewModel.onCleared()
        advanceUntilIdle()

        // Assert
        assertNotEquals(initialState, uiState.value)
    }

}

