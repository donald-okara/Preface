package com.don.preface.tests.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.don.preface.domain.logger.Logger
import com.don.preface.fake.data.FakeBookUiState.fakeBookUiStateSuccess
import com.don.preface.fake.repository.FakeBooksRepository
import com.don.preface.presentation.screens.book_details.BookDetailsViewModel
import com.don.preface.rules.TestDispatcherRule
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
    private val viewModel = BookDetailsViewModel(repository, logger, savedStateHandle)

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