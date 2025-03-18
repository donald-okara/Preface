package ke.don.feature_book_details.fake.data

import ke.don.shared_domain.states.BookUiState
import ke.don.shared_domain.states.EmptyResultState
import ke.don.shared_domain.utils.color_utils.model.ColorPallet
import ke.don.feature_book_details.fake.data.FakeBookDetailsDataSource.fakeBookDetailsResponse

object FakeBookUiState {

    val fakeBookUiStateSuccess: BookUiState =
        BookUiState(
            bookDetails = fakeBookDetailsResponse,
            resultState = EmptyResultState.Success,
            //highestImageUrl = "https://example.com/highest_image.jpg",
            colorPallet = ColorPallet()
        )
    val fakeBookUiStateError: BookUiState =
        BookUiState(
            resultState = EmptyResultState.Error(
                "Error message"
            )
        )
}