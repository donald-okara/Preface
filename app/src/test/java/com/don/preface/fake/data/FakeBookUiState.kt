package com.don.preface.fake.data

import com.don.preface.domain.states.BookUiState
import com.don.preface.domain.states.ResultState
import com.don.preface.domain.utils.color_utils.model.ColorPallet
import com.don.preface.fake.data.FakeBookDetailsDataSource.fakeBookDetailsResponse

object FakeBookUiState {

    val fakeBookUiStateSuccess: BookUiState =
        BookUiState(
            bookDetails = fakeBookDetailsResponse,
            resultState = ResultState.Success,
            highestImageUrl = "https://example.com/highest_image.jpg",
            colorPallet = ColorPallet()
        )
    val fakeBookUiStateError: BookUiState =
        BookUiState(
            resultState = ResultState.Error("Error message")
        )
}