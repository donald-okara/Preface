package ke.don.feature_book_details.tests.viewmodels

import androidx.lifecycle.SavedStateHandle
import ke.don.feature_book_details.domain.logger.Logger
import ke.don.feature_book_details.fake.data.FakeBookUiState.fakeBookUiStateSuccess
import ke.don.feature_book_details.fake.repository.FakeBooksRepository
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsViewModel
import ke.don.feature_book_details.rules.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class BookDetailsViewModelTest {
    private val repository = FakeBooksRepository()
    private val logger : Logger = mock()
    private val savedStateHandle = SavedStateHandle()
    private val viewModel =
        BookDetailsViewModel(
            repository,
            logger,
            savedStateHandle
        )

    @get:Rule
    val dispatcherRule = TestDispatcherRule()


    @Test
    fun `onLoading updates the loading joke`() = runTest{
        // Arrange

        // Act
        viewModel.onLoading()

        // Assert
        assert(viewModel.loadingJoke.isNotEmpty())

    }


}